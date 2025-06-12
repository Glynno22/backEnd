package pi.enset.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pi.enset.entities.ElementDeModule;
import pi.enset.repository.ElementModuleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ElementModuleRepository elementDeModuleRepository;

    public String handleQuestion(String question) {
        question = question.toLowerCase();

        if (question.contains("bonjour") || question.contains("salut")) {
            return "Bonjour ! Je suis votre assistant pour l'emploi de temps. Posez-moi une question comme par exemple : \"ma filier est informatique 1. Quel est mon emploi de temps ?\" ";
        }

        if (question.contains("emploi du temps") || question.contains("planning")) {
            DayOfWeek today = LocalDate.now().getDayOfWeek();
            List<ElementDeModule> modules = elementDeModuleRepository.findByJour(today);

            if (modules.isEmpty()) {
                return "Aucune séance prévue pour aujourd'hui.";
            }

            StringBuilder response = new StringBuilder("Voici votre emploi de temps pour aujourd'hui :\n\n");
            for (ElementDeModule edm : modules) {
                response.append("- ").append(edm.getLibelle())
                        .append(" avec ").append(edm.getEnseignant().getNom())
                        .append(" en salle ").append(edm.getSalle().getNom())
                        .append(" (").append(edm.getPeriode()).append(")\n");
            }

            return response.toString();
        }

        // Pour toutes les autres questions, retourner null pour passer à Mistral
        return null;
    }
}