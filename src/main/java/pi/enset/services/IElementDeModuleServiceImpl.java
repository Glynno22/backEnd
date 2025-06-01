package pi.enset.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pi.enset.entities.ElementDeModule;
import pi.enset.entities.Salle;
import pi.enset.entities.enums.Periode;
import pi.enset.entities.enums.ProgressStatus;
import pi.enset.repository.ElementModuleRepository;
import pi.enset.repository.SalleRepository;

import java.time.DayOfWeek;
import java.util.List;

@Service
@AllArgsConstructor
public class IElementDeModuleServiceImpl implements IElementDeModuleService {
    private ElementModuleRepository elementModuleRepository;
    private SalleRepository salleRepository;

    @Override
    public List<ElementDeModule> getElementDeModule() {
        return elementModuleRepository.findAll();
    }

    @Override
    public ElementDeModule addElementDeModule(ElementDeModule elementDeModule) {
        return elementModuleRepository.save(elementDeModule);
    }

    @Override
    public String deleteElementDeModule(Long id) {
        try {
            getElementDeModuleById(id);
            elementModuleRepository.deleteById(id);
            return "La suppresion est bien effectuée";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public ElementDeModule getElementDeModuleById(Long id) {
        return elementModuleRepository.findById(id).orElseThrow(() -> new RuntimeException("L'element de module du numéro " + id + " n'existe pas!"));
    }

    @Override
    public ElementDeModule updateElementDeModule(Long id, ElementDeModule elementDeModule) {
        elementDeModule.setId(id);
        return elementModuleRepository.save(elementDeModule);
    }

    @Override
    public List<ElementDeModule> getEmploisByClasse(Long classeId) {
        return elementModuleRepository.getEmploisByClasse(classeId);
    }

    // Dans ElementDeModuleServiceImpl
    @Override
    public List<ElementDeModule> getElementsByModuleAndStatus(Long moduleId, ProgressStatus status) {
        return elementModuleRepository.findByModuleIdAndStatutAvancement(moduleId, status);
    }

    @Override
    public List<ElementDeModule> getElementsByClasseAndStatus(Long classeId, ProgressStatus status) {
        return elementModuleRepository.findByModuleClasseIdAndStatutAvancement(classeId, status);
    }

    @Override
    public ElementDeModule annulerCours(Long id) {
        ElementDeModule element = elementModuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));
        element.setActif(false);
        return elementModuleRepository.save(element);
    }

    @Override
    public ElementDeModule modifierCours(Long id, DayOfWeek jour, Periode periode, String numSalle) {
        ElementDeModule element = elementModuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));

        // Salle récupérée selon le numéro
        Salle salle = salleRepository.findByNumSalle(numSalle)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée"));

        element.setJour(jour);
        element.setPeriode(periode);
        element.setSalle(salle);
        return elementModuleRepository.save(element);
    }


}
