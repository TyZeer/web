package com.music.web.controller;

import com.music.web.dto.Mp3FileDto;
import com.music.web.models.Mp3File;
import com.music.web.services.Mp3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired Mp3Service mp3Service;
    @Autowired
    public MethodsController(Mp3Service mp3Service) {
    }
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            var mp3 = mp3Service.saveMp3(file.getOriginalFilename(), file.getBytes());

            message = "Uploaded the file successfully: ";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }
    @PostMapping("/uploadMultiple")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        List<Mp3File> savedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            try {
                // Create the Mp3File object with content and save to database
                Mp3File mp3File = new Mp3File(filename, file.getBytes());
                savedFiles.add(mp3Service.saveMp3(mp3File.getFileName(), mp3File.getData()));
                mp3Service.saveAllMp3Files(savedFiles);
            } catch (IOException e) {
                e.printStackTrace();
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

    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadMp3(@PathVariable Long id) {
        return mp3Service.getMp3FileById(id)
                .map(mp3File -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + mp3File.getFileName() + "\"")
                        .body(new ByteArrayResource(mp3File.getData())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/download/shuffled")
    public ResponseEntity<InputStreamResource> downloadShuffled(@RequestParam int count) {
        List<Mp3File> mp3Files = mp3Service.getAllMp3Files();
        Collections.shuffle(mp3Files);
        if (count < mp3Files.size()) {
            mp3Files = mp3Files.subList(0, count);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (int i = 0; i < mp3Files.size(); i++) {
                Mp3File mp3File = mp3Files.get(i);
                String filename = "sf_" + i + "_" + mp3File.getFileName(); // Adjust filename here
                ZipEntry entry = new ZipEntry(filename);
                zos.putNextEntry(entry);
                zos.write(mp3File.getData());
                zos.closeEntry();
            }
            zos.finish();

            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"shuffled_files.zip\"")
                    .body(new InputStreamResource(resource.getInputStream()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/download/all")
    public ResponseEntity<InputStreamResource> downloadAll() {
        List<Mp3File> mp3Files = mp3Service.getAllMp3Files();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Mp3File mp3File : mp3Files) {
                ZipEntry entry = new ZipEntry(mp3File.getFileName());
                zos.putNextEntry(entry);
                zos.write(mp3File.getData());
                zos.closeEntry();
            }
            zos.finish();

            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"all_files.zip\"")
                    .body(new InputStreamResource(resource.getInputStream()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMp3(@PathVariable Long id) {
        try {
            mp3Service.deleteMp3ById(id);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Error deleting mp3 file.");
        }

    }
}

