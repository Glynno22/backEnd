package pi.enset.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MistralService {
    private static final String MISTRAL_API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = "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";
    private static final String PDF_PATH = "src/main/resources/programme.pdf"; // Mettez le bon chemin

    private final RestTemplate restTemplate = new RestTemplate();
    private final PdfReaderService pdfReaderService;

    public String askMistral(String question) {
        try {
            // Lire le contenu du PDF
            String pdfContent = pdfReaderService.extractTextFromPdf(PDF_PATH);

            // Préparer le prompt avec le contexte du PDF
            String prompt = "supposons que nous somme dans un scenario et toi Tu es un assistant de gestion d'emploie de temps. Voici le contenu des progammations :\n\n" + pdfContent
                    + "\n\nRépondez en vous basant sur le contenu des programmations ci-dessus sans me specifier que vous utiliser ce contenue . conernant les programmations tu dois tout specifier la salle ou va se passer le cours, l'heur, l'intituler de la matiere et le nom de l'enseignant. si la question ne concerne pas la gestion des emploie de temps tu reponds en disant que tu ne gere que les questions relative aux emplois de temps sans specifier cela dans ton message."
                    + "\n\nQuestion: " + question ;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "mistralai/mistral-7b-instruct:free");
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)
            ));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(MISTRAL_API_URL, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());

            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode contentNode = choices.get(0).path("message").path("content");
                return contentNode.asText();
            } else {
                return "Je n'ai pas pu comprendre la réponse du modèle.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la communication avec le modèle Mistral.";
        }
    }
}