package pi.enset.services;

import pi.enset.entities.ElementDeModule;
import pi.enset.entities.Salle;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public interface IEmpliDeTempsService {
     List<Map<Long, List<ElementDeModule>>> getAllEmplois();
     List<ElementDeModule> getEmploisByClasse(Long id);
    List<Map<Long, List<ElementDeModule>>> generateEmplois();

    List<ElementDeModule> getEmploiByProf(Long id);
    List<ElementDeModule> getEmploiByProf2(Long id);

    List<ElementDeModule> getAllElementDeModules();
    void resetTrackingData();
    //SchoolTimetable generateTimetableWithTracking();

    public List<String> getPeriodesLibres(String jour, String salle) ;


    List<DayOfWeek> getJoursDisponibles();
    List<Salle> getSallesDisponibles();


}
