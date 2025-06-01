package pi.enset.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pi.enset.model.BotResponse;
import pi.enset.services.ChatbotService;
import pi.enset.services.MistralService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final MistralService mistralService;
    private final ChatbotService chatbotService;

    public ChatController(MistralService mistralService, ChatbotService chatbotService) {
        this.mistralService = mistralService;
        this.chatbotService = chatbotService;
    }

@PostMapping(produces = "application/json")
public ResponseEntity<BotResponse> chat(@RequestBody Map<String, String> payload) {
    String question = payload.get("question");

    // 1. Essayer de répondre via la logique de l'application
    String localAnswer = chatbotService.handleQuestion(question);

    String finalAnswer;
    if (localAnswer != null) {
        finalAnswer = localAnswer;
    } else {
        // 2. Sinon, demander à Mistral (fallback généraliste)
        finalAnswer = mistralService.askMistral(question);
    }

    String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    BotResponse botResponse = new BotResponse("bot", finalAnswer, time);

    return ResponseEntity.ok(botResponse);
}

}
