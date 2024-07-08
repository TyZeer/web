package com.music.web.controller;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    @PostMapping("/uploadZip")
    public ResponseEntity<String> uploadZip(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".zip")) {
            return ResponseEntity.badRequest().body("Invalid file. Please upload a zip file.");
        }

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".mp3")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    var byteData = baos.toByteArray();
                    mp3Service.saveMp3(zipEntry.getName(), byteData);
                }
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing zip file.");
        }

        return ResponseEntity.ok("Zip file processed and MP3 files saved successfully.");
    }

    @GetMapping("/list")
    @ResponseBody
    public List<Mp3File> listMp3Files() {
        return mp3Service.getAllMp3Files();
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
}

