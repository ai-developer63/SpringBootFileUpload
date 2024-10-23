package com.example.demo.api_controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.example.demo.storage.StorageFileNotFoundException;
import com.example.demo.storage.StorageService;

@RestController
public class FileUploadApiController {

	private final StorageService storageService;
	@Value("${baseUrl}")
	String baseUrl;
	// added inOrder to determine the File Type and notify Browser for inline
	String contentType = "application/octet-stream";

	@Autowired
	public FileUploadApiController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/api/{type}")
	public ResponseEntity<List<String>> FileWithItsType(@PathVariable("type") String type) throws IOException {
		List<String> files = storageService.FileWithItsType(type);
		List<String> filesLinks = new ArrayList<>();
		
		// Self converting list to final Url Pattern
		for (String fileName : files) {
			String converted = MvcUriComponentsBuilder
					.fromMethodName(FileUploadApiController.class, "serveFile",fileName)
					.build()
					.toUri()
					.toString();
			filesLinks.add(converted);
		}
		return ResponseEntity.ok(filesLinks);
	}

	// List all uploaded files with their download URLs
	@GetMapping("/api/files")
	public ResponseEntity<List<String>> listUploadedFiles() throws IOException {

		List<String> files = storageService.loadAll()
				.map(path -> MvcUriComponentsBuilder
						.fromMethodName(FileUploadApiController.class, "serveFile", path.getFileName().toString())
						.build().toUri().toString())
				.collect(Collectors.toList());

		return ResponseEntity.ok().body(files);
	}

	// Serve a file for download
	@GetMapping("/api/files/{filename:.+}")
	public ResponseEntity<Resource> serveFile(@PathVariable("filename") String filename) {

		Resource file = storageService.loadAsResource(filename);
		if (file == null) {
			return ResponseEntity.notFound().build();
		}

		// Default for unknown types
		try {
			contentType = Files.probeContentType(file.getFile().toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Log the content type and file path
		System.out.println("Serving file: " + filename + " with content type: " + contentType);
		try {
			System.out.println("Loading file from: " + file.getFile().getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"").body(file);// if
																														// you
																														// want
																														// to
																														// display
																														// content
																														// in
																														// Browser
																														// Always
																														// keep
																														// Contentdisposition
																														// into
																														// inline
																														// to
																														// download
																														// attachments
	}

	// GetfileByPrecision
	// Serve a file for download
	@GetMapping("/api/file/{filename:.+}")
	public DeferredResult<ResponseEntity<Resource>> serveFiles(@PathVariable("filename") String filename) {
		DeferredResult<ResponseEntity<Resource>> output = new DeferredResult<>(10000L,
				ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request Timeout"));

		// Load the file as a resource
		Resource file = storageService.loadAsResource(filename);

		if (file == null || !file.exists()) {
			// Log the error
			System.err.println("File not found: " + filename);
			output.setResult(ResponseEntity.notFound().build());
			return output;
		}

		// Log the content type and file path
		System.out.println("Serving file: " + filename + " with content type: " + contentType);
		try {
			System.out.println("Loading file from: " + file.getFile().getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Could not get absolute path for file: " + filename);
			e.printStackTrace(); // Consider using a logger instead
		}

		// Which type of Content Should be inline(Show in Browser) & attachment
		// (Download Able Content)
		// Determine content disposition in the asynchronous block
		CompletableFuture.runAsync(() -> {
			String contentDisposition; // Declare inside the block
			if (contentType.equals("application/pdf") || contentType.startsWith("image/")
					|| contentType.startsWith("video/")) {
				contentDisposition = "inline"; // For PDFs and images, allow inline display
			} else {
				contentDisposition = "attachment"; // For other types, force download
			}

			// Simulate file streaming or serving logic here
			try {
				Thread.sleep(5000); // Simulate time taken to serve file
				output.setResult(ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION,
								contentDisposition + "; filename=\"" + file.getFilename() + "\"")
						.body(file));
			} catch (InterruptedException e) {
				// If interrupted, it means the client disconnected
				System.err.println("File serving interrupted due to client disconnect: " + filename);
			}
		});

		return output;
	}

	// Handle file upload
	@PostMapping("/api/upload")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {

		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
		}

		storageService.store(file);
		return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
	}

	// Handle StorageFileNotFoundException
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<String> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
	}
}
