package com.music.web.services;

import com.music.web.dto.Mp3FileDto;
import com.music.web.models.Mp3File;
import com.music.web.repos.Mp3FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class Mp3Service {

//    @Value("${mp3.storage.directory}")
    private String storageDirectory;
    public Mp3Service() throws IOException {
        this.storageDirectory = "./mp3-storage";
        Path path = Paths.get("./mp3-storage");
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
    }

    public void saveMp3(String filename, byte[] fileData) throws IOException {
        Path filePath = Paths.get(storageDirectory, filename);
        Files.write(filePath, fileData);
    }

    public List<Mp3FileDto> getAllMp3FDto() throws IOException {
        try (Stream<Path> paths = Files.list(Paths.get(storageDirectory))) {
            return paths.filter(Files::isRegularFile)
                    .map(path -> new Mp3FileDto(null, path.getFileName().toString()))
                    .collect(Collectors.toList());
        }
    }

    public Path getFilePath(String fileName) {
        return Paths.get(storageDirectory, fileName);
    }

    public List<String> getShuffledFiles() throws IOException {
        List<String> fileNames = getAllFileNames();
        Collections.shuffle(fileNames);
        return fileNames;
    }

    public List<String> getAllFileNames() throws IOException {
        try (Stream<Path> paths = Files.list(Paths.get(storageDirectory))) {
            return paths.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        }
    }

    public void deleteMp3() throws IOException {
        Path directory = Paths.get(storageDirectory);
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Invalid directory: " + storageDirectory);
        }

        Files.list(directory)
                .filter(file -> file.toString().endsWith(".mp3"))
                .forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        System.err.println("Failed to delete file: " + file);
                        e.printStackTrace();
                    }
                });
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