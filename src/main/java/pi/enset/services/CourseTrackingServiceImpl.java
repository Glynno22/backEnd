package pi.enset.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pi.enset.entities.ElementDeModule;
import pi.enset.entities.enums.ProgressStatus;
import pi.enset.repository.ElementModuleRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@AllArgsConstructor
public class CourseTrackingServiceImpl implements ICourseTrackingService {

    private final ElementModuleRepository elementDeModuleRepository;
    private final IElementDeModuleService elementDeModuleService;

    @Override
    public ElementDeModule updateCourseProgress(Long elementId, int heuresAjoutees) {
        ElementDeModule element = elementDeModuleRepository.findById(elementId)
                .orElseThrow(() -> new EntityNotFoundException("Element de module non trouvé"));

        if (element.getHeuresEffectuees() + heuresAjoutees > element.getVolumeHoraire()) {
            throw new IllegalArgumentException("Le nombre d'heures ajoutées dépasse le volume horaire total");
        }

        element.setHeuresEffectuees(element.getHeuresEffectuees() + heuresAjoutees);
        return elementDeModuleRepository.save(element);
    }

    @Override
    public ElementDeModule setCourseStatus(Long elementId, ProgressStatus status) {
        ElementDeModule element = elementDeModuleRepository.findById(elementId)
                .orElseThrow(() -> new EntityNotFoundException("Element de module non trouvé"));

        element.setStatutAvancement(status);

        // Si on marque comme complété, on met les heures effectuées = volume horaire
        if (status == ProgressStatus.COMPLETED) {
            element.setHeuresEffectuees(element.getVolumeHoraire());
        }
        // Si on marque comme non commencé, on remet à zéro
        else if (status == ProgressStatus.NOT_STARTED) {
            element.setHeuresEffectuees(0);
        }

        return elementDeModuleRepository.save(element);
    }

    @Override
    public List<ElementDeModule> getCoursesByStatus(ProgressStatus status) {
        return elementDeModuleRepository.findByStatutAvancement(status);
    }

    @Override
    public List<ElementDeModule> getCoursesByTeacherAndStatus(Long teacherId, ProgressStatus status) {
        return elementDeModuleRepository.findByEnseignantIdAndStatutAvancement(teacherId, status);
    }

    @Override
    public ElementDeModule resetCourseProgress(Long elementId) {
        ElementDeModule element = elementDeModuleRepository.findById(elementId)
                .orElseThrow(() -> new EntityNotFoundException("Element de module non trouvé"));

        element.setHeuresEffectuees(0);
        element.setStatutAvancement(ProgressStatus.NOT_STARTED);
        return elementDeModuleRepository.save(element);
    }
}