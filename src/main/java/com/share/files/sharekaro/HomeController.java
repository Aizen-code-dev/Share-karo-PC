package com.share.files.sharekaro;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class HomeController {

    // ⚠️ Tip: Avoid OneDrive folder because it locks files — use a normal folder like below:
    private static final String UPLOAD_DIR = "C:/uploads/";

    @GetMapping("/")
    public String home() {
        return "index"; // loads index.html from static/templates folder
    }

    /** 📥 Upload from Mobile → PC **/
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            File destinationFile = new File(uploadDir, file.getOriginalFilename());
            file.transferTo(destinationFile);

            // make sure readable by both sides
            destinationFile.setReadable(true, false);
            destinationFile.setWritable(true, false);

            return ResponseEntity.ok("File uploaded successfully: " + destinationFile.getName());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    /** 📂 List files for download (PC → Mobile) **/
    @GetMapping("/files")
    @ResponseBody
    public String listFiles() {
        File folder = new File(UPLOAD_DIR);
        if (!folder.exists() || folder.listFiles() == null) {
            return "<h3>No files available.</h3>";
        }

        StringBuilder html = new StringBuilder(
                "<h2>Available Files to Download</h2><ul style='font-size:18px;'>");
        for (File file : folder.listFiles()) {
            html.append("<li><a href='/download?filename=")
                    .append(file.getName())
                    .append("'>")
                    .append(file.getName())
                    .append("</a></li>");
        }
        html.append("</ul>");
        return html.toString();
    }

    /** 📤 Download endpoint (PC → Mobile) **/
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) {
        File file = new File(UPLOAD_DIR + filename);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}
