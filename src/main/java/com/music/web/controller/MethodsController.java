package com.music.web.controller;

import com.music.web.dto.Mp3FileDto;
import com.music.web.models.Mp3File;
import com.music.web.services.Mp3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/inner")
public class MethodsController {

    @Autowired
    Mp3Service mp3Service;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            mp3Service.saveMp3(file.getOriginalFilename(), file.getBytes());
            return ResponseEntity.status(HttpStatus.OK).body("Uploaded the file successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Could not upload the file. Error: " + e.getMessage());
        }
    }

    @PostMapping("/uploadMultiple")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        for (MultipartFile file : files) {
            try {
                mp3Service.saveMp3(file.getOriginalFilename(), file.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(500).build();
            }
        }
        return ResponseEntity.ok().body("Files uploaded successfully.");
    }

    @GetMapping("/list")
    @ResponseBody
    public List<Mp3FileDto> listMp3Files() {
        try {
            return mp3Service.getAllMp3FDto();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadMp3(@PathVariable String fileName) {
        try {
            Path filePath = mp3Service.getFilePath(fileName);
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/shuffled")
    public void downloadShuffled(HttpServletResponse response) {
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"shuffled_files.zip\"");

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            List<String> shuffledFiles = mp3Service.getShuffledFiles();
            for (String fileName : shuffledFiles) {
                Path filePath = mp3Service.getFilePath(fileName);

                // Add a random number to the beginning of the file name
                String randomFileName = addRandomNumberToFileName(fileName);
                zos.putNextEntry(new ZipEntry(randomFileName));
                Files.copy(filePath, zos);
                zos.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create ZIP file", e);
        }
    }

    private String addRandomNumberToFileName(String fileName) {
        int randomNumber = (int) (Math.random() * 10000); // Generates a random number between 0 and 9999
        return randomNumber + "_" + fileName;
    }



    @GetMapping("/download/all")
    public ResponseEntity<Resource> downloadAll() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            List<String> allFiles = mp3Service.getAllFileNames();
            for (String fileName : allFiles) {
                Path filePath = mp3Service.getFilePath(fileName);
                zos.putNextEntry(new ZipEntry(fileName));
                Files.copy(filePath, zos);
                zos.closeEntry();
            }
            zos.finish();

            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"all_files.zip\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAllMp3() {
        try {
            mp3Service.deleteMp3();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting mp3 file.");
        }
    }
}