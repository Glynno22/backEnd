package pi.enset.settings;

import lombok.AllArgsConstructor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import pi.enset.entities.Module;
import pi.enset.entities.*;
import pi.enset.entities.enums.NumeroSemester;
import pi.enset.entities.enums.TypeSalle;
import pi.enset.services.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class LoadFormExcelToDb {
    private ISalleService iSalleService;
    private IFiliereService iFiliereService;
    private IClasseService iClasseService;
    private IEnseignantService iEnseignantService;
    private IElementDeModuleService iElementDeModuleService;
    private IModuleService iModuleService;
    private ISemestreService iSemestreService;
    private IDepartementService iDepartementService;


    public boolean PutDataToDb(String path) throws IOException {
        boolean isImported = true;
        try {
            Workbook workbook = WorkbookFactory.create(new File(path));
            // Retrieving the number of sheets in the Workbook
            int numberOfSheets = workbook.getNumberOfSheets();
            // Getting the Sheet at index zero
            List<Semestre> semestres = new ArrayList<>();
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet.getSheetName().contains("SALLES")) {
                    Salle salle = null;
                    for (Row row : sheet) {
                        if (row.getRowNum() >= 4) {
                            for (Cell cell : row) {

                                String salleNm = "-1";
                                if (cell.getColumnIndex() == 2) {
                                    salle = new Salle();
                                    if (!cell.getStringCellValue().equals("")) {
                                        salleNm = (cell.getStringCellValue().split("_")[1]);
                                        salle.setNumSalle(salleNm);
                                    }
                                }
                                if (cell.getColumnIndex() == 3) {
                                    if (!cell.getStringCellValue().equals("")) {
                                        DataFormatter formatter = new DataFormatter();
                                        assert salle != null;
                                        salle.setTypeSalle(TypeSalle.valueOf(cell.getStringCellValue()));
                                        salle.setCapacite((int) cell.getRow().getCell(4).getNumericCellValue());

                                        String codestr = formatter.formatCellValue(cell.getRow().getCell(5));
                                        salle.setCode(codestr); // Si setCapacite() accepte un String

                                        String locsrt = formatter.formatCellValue(cell.getRow().getCell(6));
                                        salle.setLocalisation(locsrt); // Si setCapacite() accepte un String

                                        String nomsrt = formatter.formatCellValue(cell.getRow().getCell(7));
                                        salle.setNom(nomsrt); // Si setCapacite() accepte un String
                                    }
                                    iSalleService.addSalle(salle);
                                }
                            }

                        }
                    }
                }
                if (sheet.getSheetName().contains("ENSEIGNANTS")) {
                    for (Row row : sheet) {
                        if (row.getRowNum() >= 4) { // On commence à la ligne 4 (après les titres)
                            Enseignant e = new Enseignant(); // Crée un nouvel enseignant

                            // Colonne 2 : Nom et Prénom (ex: "Dupont_Jean")
                            Cell nomCell = row.getCell(2);
                            if (nomCell != null && !nomCell.getStringCellValue().isEmpty()) {
                                String[] nomPrenom = nomCell.getStringCellValue().split("_");
                                e.setNom(nomPrenom[0]); // "Dupont"
                                e.setPrenom(nomPrenom.length > 1 ? nomPrenom[1] : ""); // "Jean" (ou vide si pas de "_")
                            }

                            // Colonne 3 : Spécialité (ex: "Mathématiques")
                            Cell specCell = row.getCell(3);
                            if (specCell != null && !specCell.getStringCellValue().isEmpty()) {
                                e.setSpecialite(specCell.getStringCellValue());
                            }

                            // Colonne 4 : Téléphone (ex: "0601020304" ou 601020304)
                            Cell telCell = row.getCell(4);
                            if (telCell != null) {
                                if (telCell.getCellType() == CellType.STRING) {
                                    e.setTel(telCell.getStringCellValue().trim());
                                } else if (telCell.getCellType() == CellType.NUMERIC) {
                                    e.setTel(String.valueOf((long) telCell.getNumericCellValue())); // Convertit les nombres en texte
                                }
                            }

                            // Colonne 5 : Email (ex: "jean.dupont@ecole.fr")
                            Cell emailCell = row.getCell(5);
                            if (emailCell != null && !emailCell.getStringCellValue().isEmpty()) {
                                e.setEmail(emailCell.getStringCellValue().trim());
                            }

                            // Colonne 6 : Login (ex: "jdupont")
                            Cell loginCell = row.getCell(6);
                            if (loginCell != null && !loginCell.getStringCellValue().isEmpty()) {
                                e.setLogin(loginCell.getStringCellValue().trim());
                            }

                            // Colonne 7 : CNE (ex: "P123456789")
                            Cell cneCell = row.getCell(7);
                            if (cneCell != null && !cneCell.getStringCellValue().isEmpty()) {
                                e.setCne(cneCell.getStringCellValue().trim());
                            }

                            // Colonne 8 : Civilité (ex: "M." ou "Mme")
                            Cell civiliteCell = row.getCell(8);
                            if (civiliteCell != null && !civiliteCell.getStringCellValue().isEmpty()) {
                                e.setCivilite(civiliteCell.getStringCellValue().trim());
                            }

                            // Colonne 9 : Password (ex: "motdepasse123")
                            Cell passwordCell = row.getCell(9);
                            if (passwordCell != null && !passwordCell.getStringCellValue().isEmpty()) {
                                e.setPassword(passwordCell.getStringCellValue().trim());
                            }

                            // On vérifie d'abord par CNE (ou email) qui doit être UNIQUE
                            if (e.getCne() != null && !e.getCne().isEmpty()) {
                                if (iEnseignantService.findEnseignantByCne(e.getCne()).isEmpty()) {
                                    iEnseignantService.addEnseignant(e);
                                    System.out.println("✅ Enseignant ajouté : " + e.getNom() + " (CNE: " + e.getCne() + ")");
                                } else {
                                    System.out.println("⚠ Déjà existant (CNE dupliqué) : " + e.getCne());
                                }
                            } else {
                                System.out.println("❌ Erreur : CNE manquant pour " + e.getNom());
                            }
                        }
                    }
                }
                if (!sheet.getSheetName().contains("SALLES") && !sheet.getSheetName().contains("ENSEIGNANTS")) {
                    Departement departement = new Departement();
                    Filiere filiere = new Filiere();
                    filiere.setClasses(new ArrayList<>());
                    filiere.setLibelle(sheet.getRow(2).getCell(3).getStringCellValue());
                    filiere.setCodeFi(sheet.getRow(2).getCell(2).getStringCellValue());
                    filiere.setChefFiliere(sheet.getRow(2).getCell(4).getStringCellValue());
                    departement.setLibelle(sheet.getRow(2).getCell(5).getStringCellValue());

                    // Create a DataFormatter to format and get each cell's value as String
                    DataFormatter dataFormatter = new DataFormatter();
                    Semestre semestre = null;
                    Classe classe = null;
                    Module module = null;
                    ElementDeModule element;
                    int j = 5;

                    for (Row row : sheet) {
                        if (row.getRowNum() >= 6) {
                            j++;
                            for (Cell cell : row) {
                                if (cell.getColumnIndex() == 1) {
                                    if (!cell.getStringCellValue().equals("")) {
                                        semestre = new Semestre();
                                        semestre.setNum(NumeroSemester.valueOf(cell.getStringCellValue()));
                                        semestre.setAnneeUniv(sheet.getRow(0).getCell(2).getStringCellValue());

                                        classe = new Classe();
                                        classe.setNbrEleves((int) sheet.getRow(j).getCell(7).getNumericCellValue());

                                        // Nouvelle logique pour le nom de la classe
                                        int numeroSemestre = Integer.parseInt(semestre.getNum().toString().substring(1));
                                        int niveau = (numeroSemestre + 1) / 2;
                                        classe.setLibelle(filiere.getLibelle() + " Niveau " + niveau);

                                        if (iSemestreService.findSemestreByNum(semestre.getNum()).size() == 0) {
                                            iSemestreService.addSemestre(semestre);
                                            classe.setSemestre(semestre);
                                        } else {
                                            classe.setSemestre(iSemestreService.findSemestreByNum(semestre.getNum()).get(0));
                                        }

                                        filiere.getClasses().add(classe);

                                        if (iDepartementService.findDepartementByNom(departement.getLibelle()).size() == 0) {
                                            filiere.setDepartement(departement);
                                            iDepartementService.addDepartement(departement);
                                        } else {
                                            filiere.setDepartement(iDepartementService.findDepartementByNom(departement.getLibelle()).get(0));
                                        }
                                        iFiliereService.addFiliere(filiere);
                                    }
                                }

                                if (cell.getColumnIndex() == 2) {
                                    if (!cell.getStringCellValue().equals("")) {
                                        module = new Module();
                                        module.setElementDeModules(new ArrayList<>());
                                        module.setLibelle(cell.getStringCellValue());
                                        assert classe != null;
                                        classe.getModules().add(module);
                                    }
                                    assert classe != null;
                                    classe.setFiliere(filiere);
                                    iClasseService.addClasse(classe, filiere.getId());
                                }

                                if (cell.getColumnIndex() == 4) {
                                    if (!cell.getStringCellValue().equals("")) {
                                        element = new ElementDeModule();
                                        element.setLibelle(cell.getStringCellValue());
                                        Enseignant e = null;

                                        if (!cell.getRow().getCell(5).getStringCellValue().equals("")) {
                                            e = new Enseignant();
                                            e.setNom(cell.getRow().getCell(5).getStringCellValue().split("_")[0]);
                                            e.setPrenom(cell.getRow().getCell(5).getStringCellValue().split("_")[1]);

                                            if (iEnseignantService.findEnseignantByNom(e.getNom()).size() != 0) {
                                                element.setEnseignant(iEnseignantService.findEnseignantByNom(e.getNom()).get(0));
                                            } else {
                                                isImported = false;
                                                System.out.println("le prof n'existe pas");
                                            }
                                        }

                                        iModuleService.addModule(module);
                                        element.setVolumeHoraire((int) cell.getRow().getCell(6).getNumericCellValue());
                                        element.setModule(module);
                                        iElementDeModuleService.addElementDeModule(element);

                                        // 1. Calcule la somme des volumes horaires
                                        int totalVolume = module.getElementDeModules().stream()
                                                .mapToInt(ElementDeModule::getVolumeHoraire)
                                                .sum();

                                        // 2. Met à jour le module
                                        module.setVolumeHoraire(totalVolume);

                                        // 3. Sauvegarde les modifications
                                        iModuleService.addModule(module);

                                        assert module != null;
                                        module.getElementDeModules().add(element);
                                        module.setClasse(classe);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (EncryptedDocumentException | IOException e) {
            isImported = false;
            e.printStackTrace();
        }
        return isImported;
    }
}