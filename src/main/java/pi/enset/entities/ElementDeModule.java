package pi.enset.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pi.enset.entities.enums.Periode;
import pi.enset.entities.enums.ProgressStatus;

import java.time.DayOfWeek;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementDeModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int volumeHoraire;
    private int heuresEffectuees; // Nouveau champ
    private int heuresRestantes; // Nouveau champ - calculé

    @Enumerated(EnumType.STRING)
    private ProgressStatus statutAvancement; // Nouveau champ

    private String libelle;

    private boolean actif = true;

    @Enumerated(EnumType.STRING)
    private DayOfWeek jour;

    @Enumerated(EnumType.STRING)
    private Periode periode;

    @ManyToOne
    private Salle salle;

    @ManyToOne
    private Module module;

    @ManyToOne
    private Enseignant enseignant;

    @PostLoad
    @PrePersist
    @PreUpdate
    public void calculateHeuresRestantes() {
        this.heuresRestantes = this.volumeHoraire - this.heuresEffectuees;
        updateStatutAvancement();
    }

    private void updateStatutAvancement() {
        if (heuresEffectuees <= 0) {
            this.statutAvancement = ProgressStatus.NOT_STARTED;
        } else if (heuresEffectuees >= volumeHoraire) {
            this.statutAvancement = ProgressStatus.COMPLETED;
        } else {
            this.statutAvancement = ProgressStatus.IN_PROGRESS;
        }
    }

}