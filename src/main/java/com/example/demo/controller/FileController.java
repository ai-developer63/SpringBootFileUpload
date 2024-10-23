package com.example.demo.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@org.springframework.stereotype.Controller
public class FileController {

    @Value("${location}")
    String locationfile;
    
    
    @GetMapping("/fileupload")
    public String showUploadForm() {
        // Return the name of the HTML file (without the .html extension)
        return "fileuploading";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be empty");
        } else {
            try {
                // Create the directory if it doesn't exist
                File directory = new File(locationfile);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Create the file with the correct file path
                String filePath = locationfile + File.separator + file.getOriginalFilename();
                File realFile = new File(filePath);
                file.transferTo(realFile);
                
                return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File uploading failed: " + e.getMessage());
            }
        }
    }
    

    

    
}
