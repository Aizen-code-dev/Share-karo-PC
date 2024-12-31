package com.share.files.sharekaro;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;

@Controller
public class HomeController {
    private static final String UPLOAD_DIR = "C:/Users/sarth/OneDrive/Desktop/uploads/";

    @GetMapping("/")
    public String home() {
        return "index"; 
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            // Create the upload directory if it doesn't exist
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Use mkdirs() to create any necessary parent directories
            }

            // Save the file
            File destinationFile = new File(uploadDir, file.getOriginalFilename());
            file.transferTo(destinationFile);

            return ResponseEntity.ok("File uploaded successfully: " + destinationFile.getName());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }
}