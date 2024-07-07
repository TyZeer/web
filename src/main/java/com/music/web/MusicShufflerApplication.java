package com.music.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication()

public class MusicShufflerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicShufflerApplication.class, args);
	}

}
