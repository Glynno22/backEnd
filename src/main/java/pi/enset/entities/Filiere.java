package pi.enset.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Filiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;

    private String codeFi;

    private int nombreSem;

    private String chefFiliere; // ?

    @OneToMany(mappedBy = "filiere")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Collection<Classe> classes = new ArrayList<>();

    @ManyToOne
    private Departement departement;

}
