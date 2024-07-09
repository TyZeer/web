package com.music.web.services;

import com.music.web.dto.Mp3FileDto;
import com.music.web.models.Mp3File;
import com.music.web.repos.Mp3FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class Mp3Service {

    private final Mp3FileRepository mp3FileRepository;

    public Mp3Service(Mp3FileRepository mp3FileRepository) {
        this.mp3FileRepository = mp3FileRepository;
    }

    public Mp3File saveMp3(String filename, byte[] fileData) {
        Mp3File mp3File = new Mp3File(filename, fileData);
        return mp3FileRepository.save(mp3File);
    }

    public List<Mp3File> getAllMp3Files() {
        return  mp3FileRepository.findAll();
    }
    public List<Mp3FileDto> getAllMp3FDto() {
        return mp3FileRepository.findAllMp3Files();
    }

    public Optional<Mp3File> getMp3FileById(Long id) {
        return mp3FileRepository.findById(id);
    }

    public List<Mp3File> getShuffledMp3Files(int count) {
            List<Mp3File> allFiles = mp3FileRepository.findAll();
        Collections.shuffle(allFiles);
        return allFiles.stream().limit(count).collect(Collectors.toList());
    }
    @Transactional
    public void saveAllMp3Files(List<Mp3File> mp3Files) {
        mp3FileRepository.saveAll(mp3Files);
    }

    public void deleteMp3ById(Long id) {
        mp3FileRepository.deleteById(id);
    }

    @Transactional
    public void processZipFile(InputStream zipInputStream) throws IOException {
        List<Mp3File> mp3Files = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                try {
                    // Try reading the file name in UTF-8 encoding
                    fileName = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
                } catch (Exception e) {
                    System.out.println("Error converting file name: " + fileName);
                    e.printStackTrace();
                }
                System.out.println("Processing file: " + fileName);

                if (!zipEntry.isDirectory() && fileName.endsWith(".mp3")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    Mp3File mp3File = new Mp3File();
                    mp3File.setFileName(fileName);
                    mp3File.setData(baos.toByteArray());
                    mp3Files.add(mp3File);
                }
            }
        }
        mp3FileRepository.saveAll(mp3Files);
    }
}
//@Autowired private final Mp3FileRepository mp3FileRepository;
////@Autowired private final MP3FileWebSocketHandler webSocketHandler;
//
//
//public Mp3Service(Mp3FileRepository mp3FileRepository, MP3FileWebSocketHandler webSocketHandler) {
//    this.mp3FileRepository = mp3FileRepository;
//    this.webSocketHandler = webSocketHandler;
//}
//
//public Mono<Mp3File> saveMp3(String filename, byte[] fileData) {
//    Mp3File mp3File = new Mp3File(filename, fileData);
//    return mp3FileRepository.save(mp3File)
//            .doOnSuccess(savedFile -> webSocketHandler.notifyFileListUpdated());
//}
//
//public Flux<Mp3File> getAllMp3Files() {
//    return mp3FileRepository.findAll();
//}
//
//public Mono<Mp3File> getMp3FileById(Long id) {
//    return mp3FileRepository.findById(id);
//}
//
//public Mono<List<Mp3File>> getShuffledMp3Files(int count) {
//    return mp3FileRepository.findAll()
//            .collectList()
//            .doOnNext(Collections::shuffle)
//            .map(list -> list.subList(0, Math.min(count, list.size())));
//}
//public Mono<Void> deleteMp3ById(Long id){
//    return  mp3FileRepository.deleteById(id);
//}