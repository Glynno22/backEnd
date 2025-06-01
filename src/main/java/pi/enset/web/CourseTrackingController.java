package pi.enset.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pi.enset.entities.ElementDeModule;
import pi.enset.entities.enums.ProgressStatus;
import pi.enset.services.ICourseTrackingService;

import java.util.List;

@RestController
@RequestMapping("/api/course-tracking")
@AllArgsConstructor
@CrossOrigin("*")
public class CourseTrackingController {

    private final ICourseTrackingService courseTrackingService;

    @PutMapping("/{elementId}/add-hours")
    public ElementDeModule addCourseHours(@PathVariable Long elementId, @RequestParam int hours) {
        return courseTrackingService.updateCourseProgress(elementId, hours);
    }

    @PutMapping("/{elementId}/status")
    public ElementDeModule setCourseStatus(@PathVariable Long elementId, @RequestParam ProgressStatus status) {
        return courseTrackingService.setCourseStatus(elementId, status);
    }

    @GetMapping("/by-status/{status}")
    public List<ElementDeModule> getCoursesByStatus(@PathVariable ProgressStatus status) {
        return courseTrackingService.getCoursesByStatus(status);
    }

    @GetMapping("/teacher/{teacherId}/by-status/{status}")
    public List<ElementDeModule> getTeacherCoursesByStatus(
            @PathVariable Long teacherId,
            @PathVariable ProgressStatus status) {
        return courseTrackingService.getCoursesByTeacherAndStatus(teacherId, status);
    }

    @PutMapping("/{elementId}/reset")
    public ElementDeModule resetCourseProgress(@PathVariable Long elementId) {
        return courseTrackingService.resetCourseProgress(elementId);
    }
}