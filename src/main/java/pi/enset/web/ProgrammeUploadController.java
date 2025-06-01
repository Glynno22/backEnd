package pi.enset.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/programme")
@CrossOrigin(origins = "*")
public class ProgrammeUploadController {

    //@Value("${upload.directory}") // Vous pouvez définir ceci dans application.properties
    //private String uploadDirectory;

    private static final String RESOURCES_DIR = System.getProperty("user.dir") + "/src/main/resources/";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadProgrammePdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucun fichier reçu.");
        }

        try {
            // Créer le répertoire s'il n'existe pas
            Path uploadPath = Paths.get(RESOURCES_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Chemin complet du fichier
            Path filePath = uploadPath.resolve("programme.pdf");

            // Sauvegarder le fichier
            file.transferTo(filePath.toFile());

            return ResponseEntity.ok("Fichier programme.pdf enregistré avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body("Erreur lors de l'enregistrement du fichier : " + e.getMessage());
        }
    }
}