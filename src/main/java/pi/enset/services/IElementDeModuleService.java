package pi.enset.services;

import pi.enset.entities.ElementDeModule;
import pi.enset.entities.enums.Periode;
import pi.enset.entities.enums.ProgressStatus;

import java.time.DayOfWeek;
import java.util.List;

public interface IElementDeModuleService {
    List<ElementDeModule> getElementDeModule();

    ElementDeModule addElementDeModule(ElementDeModule elementDeModule);

    String deleteElementDeModule(Long id);

    ElementDeModule getElementDeModuleById(Long id);

    ElementDeModule updateElementDeModule(Long id, ElementDeModule elementDeModule);

    List<ElementDeModule> getEmploisByClasse(Long classeId);

    // Nouvelles méthodes
    List<ElementDeModule> getElementsByModuleAndStatus(Long moduleId, ProgressStatus status);
    List<ElementDeModule> getElementsByClasseAndStatus(Long classeId, ProgressStatus status);

    ElementDeModule annulerCours(Long id);
    ElementDeModule modifierCours(Long id, DayOfWeek jour, Periode periode, String numSalle);


}
