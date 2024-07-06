package com.music.web.models;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("mp3_files")
@Getter
public class Mp3File {
    @Id
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
