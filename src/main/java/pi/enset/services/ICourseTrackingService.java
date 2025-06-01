package pi.enset.services;

import pi.enset.entities.ElementDeModule;
import pi.enset.entities.enums.ProgressStatus;

import java.util.List;

public interface ICourseTrackingService {
    ElementDeModule updateCourseProgress(Long elementId, int heuresAjoutees);
    ElementDeModule setCourseStatus(Long elementId, ProgressStatus status);
    List<ElementDeModule> getCoursesByStatus(ProgressStatus status);
    List<ElementDeModule> getCoursesByTeacherAndStatus(Long teacherId, ProgressStatus status);
    ElementDeModule resetCourseProgress(Long elementId);
}