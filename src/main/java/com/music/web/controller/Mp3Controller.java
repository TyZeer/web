package com.music.web.controller;

import com.music.web.models.Mp3File;
import com.music.web.services.Mp3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/mp3")
public class Mp3Controller {

    @Autowired
    public Mp3Controller(Mp3Service mp3Service) {
    }

    @GetMapping
    public String index() {
        return "index"; // Возвращает index.html из папки static
    }

//

}