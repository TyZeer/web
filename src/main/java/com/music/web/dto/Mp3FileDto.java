package com.music.web.dto;

public class Mp3FileDto {
    private Long id;
    private String fileName;

    // Constructors, getters, and setters
    public Mp3FileDto(Long id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
