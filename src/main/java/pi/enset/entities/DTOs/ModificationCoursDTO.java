package pi.enset.entities.DTOs;

import lombok.Data;

@Data
public class ModificationCoursDTO {
    private String jour; // ex: "MONDAY"
    private String periode; // ex: "P1"
    private Long salleId;
}