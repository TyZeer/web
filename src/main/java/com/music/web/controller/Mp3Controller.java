package com.music.web.controller;


import com.music.web.services.Mp3Service;
import com.music.web.models.Mp3File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/mp3")
public class Mp3Controller {

    private final Mp3Service mp3Service;

    @Autowired
    public Mp3Controller(Mp3Service mp3Service) {
        this.mp3Service = mp3Service;
    }

//    @GetMapping
//    public String index() {
//        return "index"; // Возвращает index.html из папки static
//    }

    @PostMapping("/upload")
    public Mono<ResponseEntity<String>> uploadMp3(@RequestParam("file") MultipartFile file) throws IOException {
        return mp3Service.saveMp3(file.getOriginalFilename(), file.getBytes())
                .map(savedFile -> ResponseEntity.ok("File uploaded successfully: " + savedFile.getFileName()));
    }

    @GetMapping("/list")
    @ResponseBody
    public Flux<Mp3File> listMp3Files() {
        return mp3Service.getAllMp3Files();
    }

    @GetMapping("/download/{id}")
    public Mono<ResponseEntity<ByteArrayResource>> downloadMp3(@PathVariable Long id) {
        return mp3Service.getMp3FileById(id)
                .map(mp3File -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + mp3File.getFileName() + "\"")
                        .body(new ByteArrayResource(mp3File.getData())));
    }

    @GetMapping("/download/shuffled")
    public Mono<ResponseEntity<List<Mp3File>>> downloadShuffledMp3(@RequestParam int count) {
        return mp3Service.getShuffledMp3Files(count)
                .map(shuffledFiles -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(shuffledFiles));
    }
}

