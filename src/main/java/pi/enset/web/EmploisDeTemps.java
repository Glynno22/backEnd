package pi.enset.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pi.enset.entities.ElementDeModule;
import pi.enset.entities.Salle;
import pi.enset.services.IEmpliDeTempsService;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/emploisDeTemps")
@AllArgsConstructor
public class EmploisDeTemps {
    private final IEmpliDeTempsService empliDeTempsService;

    @GetMapping
    public List<Map<Long, List<ElementDeModule>>> getAllEmplois() {
        return empliDeTempsService.getAllEmplois();
    }

    @GetMapping("/{id}")
    public List<ElementDeModule> getEmploisByClasse(@PathVariable Long id) {
        return empliDeTempsService.getEmploisByClasse(id);
    }

    @GetMapping("/generate")
    public List<Map<Long, List<ElementDeModule>>> generateEmplois() {
       return empliDeTempsService.generateEmplois();
    }
    //getEmploiByProf
    @GetMapping("/prof/{id}")
    public  List<ElementDeModule>getEmploiByProf(@PathVariable Long id) {

        return empliDeTempsService.getEmploiByProf(id);
    }

    @GetMapping("/prof2/{id}")
    public  List<ElementDeModule>getEmploiByProf2(@PathVariable Long id) {

        return empliDeTempsService.getEmploiByProf2(id);
    }

    @GetMapping("/allElements")
    public List<ElementDeModule> getAllElementDeModules() {
        return empliDeTempsService.getAllElementDeModules();
    }

    @GetMapping("/periodes-libres")
    public ResponseEntity<List<String>> getPeriodesLibres(
            @RequestParam String jour,
            @RequestParam String salle) {

        List<String> periodesLibres = empliDeTempsService.getPeriodesLibres(jour, salle);
        return ResponseEntity.ok(periodesLibres);
    }

    // Endpoint pour les jours connus
    @GetMapping("/jours-disponibles")
    public ResponseEntity<List<DayOfWeek>> getJoursDisponibles() {
        List<DayOfWeek> jours = empliDeTempsService.getJoursDisponibles();
        return ResponseEntity.ok(jours);
    }

    // Endpoint pour les salles connues
    @GetMapping("/salles-disponibles")
    public ResponseEntity<List<Salle>> getSallesDisponibles() {
        List<Salle> salles = empliDeTempsService.getSallesDisponibles();
        return ResponseEntity.ok(salles);
    }



}
