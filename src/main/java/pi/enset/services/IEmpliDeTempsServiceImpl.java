package pi.enset.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pi.enset.GAlgo.GaAlgorithm;
import pi.enset.GAlgo.SchoolTimetable;
import pi.enset.entities.Classe;
import pi.enset.entities.ElementDeModule;
import pi.enset.entities.Enseignant;
import pi.enset.entities.Salle;
import pi.enset.entities.enums.NumeroSemester;
import pi.enset.entities.enums.Periode;
import pi.enset.entities.enums.ProgressStatus;
import pi.enset.repository.ElementModuleRepository;
import pi.enset.settings.DataFromDb;

import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class IEmpliDeTempsServiceImpl implements IEmpliDeTempsService {
    private final DataFromDb dataFromDb;
    private final IElementDeModuleService elementDeModuleService;
    private final IEnseignantService enseignantService;
    private final ElementModuleRepository repository;

    private final GaAlgorithm algorithm;

    @Override
    public List<Map<Long, List<ElementDeModule>>> getAllEmplois() {
        List<Map<Long, List<ElementDeModule>>> emplois = new ArrayList<>();
        dataFromDb.loadDataFromDatabase();
        // Retrieve all classes
        List<Classe> classes = DataFromDb.classes;
        for (Classe classe : classes) {
            Map<Long, List<ElementDeModule>> emploi = new HashMap<>();
            emploi.put(classe.getId(), elementDeModuleService.getEmploisByClasse(classe.getId()));
            emplois.add(emploi);
        }
        return emplois;
    }

    @Override
    public List<ElementDeModule> getEmploisByClasse(Long id) {
        return elementDeModuleService.getEmploisByClasse(id);
    }

    public void resetTrackingData() {
        List<ElementDeModule> allElements = elementDeModuleService.getElementDeModule();
        for (ElementDeModule element : allElements) {
            if (element.getStatutAvancement() != ProgressStatus.COMPLETED) {
                element.setHeuresEffectuees(0);
                element.setStatutAvancement(ProgressStatus.NOT_STARTED);
                elementDeModuleService.updateElementDeModule(element.getId(), element);
            }
        }
    }

    @Override
    public List<Map<Long, List<ElementDeModule>>> generateEmplois() {
        dataFromDb.loadDataFromDatabase();

        // 1. Génération avec suivi
        SchoolTimetable timetable = generateTimetableWithTracking();

        // 2. Validation des données
        validateTrackingData(timetable);

        // 3. Sauvegarde
        saveTimetableWithTracking(timetable);

        // 4. Construction réponse
        return buildResponse(timetable);
    }

    private void validateTrackingData(SchoolTimetable timetable) {
        timetable.getAllElements().forEach(element -> {
            if (element.getHeuresEffectuees() > element.getVolumeHoraire()) {
                throw new IllegalStateException("Heures effectuées > volume horaire pour l'élément " + element.getId());
            }
            element.calculateHeuresRestantes();
        });
    }

    private SchoolTimetable generateTimetableWithTracking() {
        Map<Long, ElementDeModule> existingElements = elementDeModuleService.getElementDeModule()
                .stream()
                .collect(Collectors.toMap(ElementDeModule::getId, Function.identity()));

        GaAlgorithm algorithm = new GaAlgorithm();
        SchoolTimetable newTimetable = algorithm.generateTimetable();

        for (ElementDeModule newElement : newTimetable.getAllElements()) {
            if (existingElements.containsKey(newElement.getId())) {
                ElementDeModule existing = existingElements.get(newElement.getId());
                // Copie des données de suivi
                newElement.setHeuresEffectuees(existing.getHeuresEffectuees());
                newElement.setStatutAvancement(existing.getStatutAvancement());
                // Force le recalcul
                newElement.calculateHeuresRestantes(); // Ajoutez cette ligne
            } else {
                newElement.setHeuresEffectuees(0);
                newElement.setStatutAvancement(ProgressStatus.NOT_STARTED);
                newElement.calculateHeuresRestantes(); // Ajoutez cette ligne
            }
        }
        return newTimetable;
    }

    private void saveTimetableWithTracking(SchoolTimetable timetable) {
        for (ElementDeModule element : timetable.getAllElements()) {
            try {
                // Force le recalcul avant sauvegarde
                element.calculateHeuresRestantes();

                if (element.getId() == null) {
                    elementDeModuleService.addElementDeModule(element);
                } else {
                    elementDeModuleService.updateElementDeModule(element.getId(), element);
                }
            } catch (Exception e) {
                log.error("Erreur lors de la sauvegarde de l'élément de module {}", element.getId(), e);
            }
        }
    }

    private List<Map<Long, List<ElementDeModule>>> buildResponse(SchoolTimetable timetable) {
        List<Map<Long, List<ElementDeModule>>> emplois = new ArrayList<>();

        for (int i = 0; i < timetable.getNumberOfClasses(); i++) {
            Classe classe = timetable.getClasses().get(i);
            Map<Long, List<ElementDeModule>> emploi = new HashMap<>();

            // Tri des éléments par jour et période pour une meilleure présentation
            List<ElementDeModule> elements = timetable.getTimetable(i).stream()
                    .sorted(Comparator.comparing(ElementDeModule::getJour)
                            .thenComparing(ElementDeModule::getPeriode))
                    .collect(Collectors.toList());

            emploi.put(classe.getId(), elements);
            emplois.add(emploi);
        }

        return emplois;
    }

    @Override
    public List<ElementDeModule> getEmploiByProf(Long id) {

        Enseignant enseignant = enseignantService.getEnseignantById(id);
        // show only  element de module of S1 ou S3 or S5

        List<ElementDeModule> elementDeModules = new ArrayList<>();
        for (ElementDeModule elementDeModule : enseignant.getElementDeModules()) {
            if (elementDeModule.getModule().getClasse().getSemestre().getNum()== NumeroSemester.S3 || elementDeModule.getModule().getClasse().getSemestre().getNum()== NumeroSemester.S5 || elementDeModule.getModule().getClasse().getSemestre().getNum()== NumeroSemester.S7 || elementDeModule.getModule().getClasse().getSemestre().getNum()== NumeroSemester.S1) {
                elementDeModules.add(elementDeModule);
            }
        }

        return elementDeModules;
    }

    @Override
    public List<ElementDeModule> getEmploiByProf2(Long id) {

        Enseignant enseignant = enseignantService.getEnseignantById(id);
        // show only  element de module of S1 ou S3 or S5

        List<ElementDeModule> elementDeModules = new ArrayList<>();
        for (ElementDeModule elementDeModule : enseignant.getElementDeModules()) {
            if (elementDeModule.getModule().getClasse().getSemestre().getNum()== NumeroSemester.S4 || elementDeModule.getModule().getClasse().getSemestre().getNum()== NumeroSemester.S6 || elementDeModule.getModule().getClasse().getSemestre().getNum()== NumeroSemester.S8 || elementDeModule.getModule().getClasse().getSemestre().getNum()== NumeroSemester.S2) {
                elementDeModules.add(elementDeModule);
            }
        }

        return elementDeModules;
    }

    @Override
    public List<ElementDeModule> getAllElementDeModules() {
        List<ElementDeModule> allElements = new ArrayList<>();
        dataFromDb.loadDataFromDatabase();

        for (Classe classe : DataFromDb.classes) {
            allElements.addAll(elementDeModuleService.getEmploisByClasse(classe.getId()));
        }

        return allElements;
    }

    @Override
    public List<DayOfWeek> getJoursDisponibles() {
        return repository.findAll().stream()
                .map(ElementDeModule::getJour)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Salle> getSallesDisponibles() {
        return repository.findAll().stream()
                .map(ElementDeModule::getSalle)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }


    @Override
    public List<String> getPeriodesLibres(String jour, String salle) {
        // 1. Convertir le jour en enum DayOfWeek
        DayOfWeek jourEnum = DayOfWeek.valueOf(jour.toUpperCase());

        // 2. Récupérer les périodes déjà occupées ce jour-là dans cette salle
        List<ElementDeModule> elements = repository
                .findByJourAndSalleNomAndActifTrue(jourEnum, salle);

        Set<Periode> periodesOccupees = elements.stream()
                .map(ElementDeModule::getPeriode)
                .collect(Collectors.toSet());

        // 3. Calculer les périodes libres
        List<String> periodesLibres = Arrays.stream(Periode.values())
                .filter(p -> !periodesOccupees.contains(p))
                .map(Enum::name)
                .collect(Collectors.toList());

        return periodesLibres;
    }



}
