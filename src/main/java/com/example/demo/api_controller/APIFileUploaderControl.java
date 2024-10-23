package com.example.demo.api_controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class APIFileUploaderControl {

    private static final Logger logger = LoggerFactory.getLogger(APIFileUploaderControl.class);

    @Value("${location}") // Path defined in application.properties
    private String locationfile;

    // Temporary storage to track received chunks (this can be in-memory or database)
    private ConcurrentHashMap<String, Integer> chunkTracker = new ConcurrentHashMap<>();

    @PostMapping("api/upload/chunks")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("fileName") String fileName,
            @RequestParam("chunk") int chunkNumber,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("fileData") MultipartFile fileData) {

        // Check if file data is empty
        if (fileData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be empty");
        }

        try {
            // Ensure the directory exists
            File directory = new File(locationfile);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create temp directory for storing chunks
            File tempDirectory = new File(locationfile + File.separator + "temp");
            if (!tempDirectory.exists()) {
                tempDirectory.mkdirs();
            }

            // Store chunk temporarily
            String tempFilePath = tempDirectory + File.separator + fileName + "_chunk_" + chunkNumber;
            File tempChunkFile = new File(tempFilePath);
            fileData.transferTo(tempChunkFile);

            logger.info("Chunk {} uploaded successfully for file: {}", chunkNumber, fileName);

            // Track uploaded chunk
            chunkTracker.put(fileName + "_chunk_" + chunkNumber, chunkNumber);

            // Check if all chunks are uploaded
            if (chunkTracker.size() == totalChunks) {
                logger.info("All chunks uploaded. Merging file: {}", fileName);
                mergeChunks(fileName, totalChunks, tempDirectory);
                chunkTracker.clear(); // Clear the tracker after merging
            }

            return ResponseEntity.ok("Chunk " + chunkNumber + " uploaded successfully for: " + fileName);

        } catch (IOException e) {
            logger.error("Chunk uploading failed for {}: {}", fileName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Chunk uploading failed: " + e.getMessage());
        }
    }

    private void mergeChunks(String fileName, int totalChunks, File tempDirectory) throws IOException {
        // Final file to be created
        File finalFile = new File(locationfile + File.separator + fileName);

        try (FileOutputStream fos = new FileOutputStream(finalFile, true)) {
            for (int i = 0; i < totalChunks; i++) {
                // Read each chunk and append to the final file
                File chunkFile = new File(tempDirectory + File.separator + fileName + "_chunk_" + i);
                Files.copy(chunkFile.toPath(), fos);
                chunkFile.delete(); // Delete chunk after appending
            }
        }

        logger.info("File {} successfully assembled from chunks.", fileName);
    }
}
