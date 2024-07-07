package com.music.web.controller;

import com.music.web.models.Mp3File;
import com.music.web.services.Mp3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
    public ResponseEntity<List<Mp3File>> downloadShuffledMp3(@RequestParam int count) {
        List<Mp3File> shuffledFiles = mp3Service.getShuffledMp3Files(count);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(shuffledFiles);
    }


    @GetMapping("/download/all")
    public ResponseEntity<List<Mp3File>> downloadAllMp3() {
        List<Mp3File> allFiles = mp3Service.getAllMp3Files();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allFiles);
    }

}
