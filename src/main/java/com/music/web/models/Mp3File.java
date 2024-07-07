package com.music.web.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Data
@Entity
@NoArgsConstructor
@Getter
public class Mp3File {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileType;
    private byte[] data;

    public Mp3File(String filename, byte[] fileData) {
        this.fileName = filename;
        this.fileType =".mp3";
        this.data = fileData;
    }

}
