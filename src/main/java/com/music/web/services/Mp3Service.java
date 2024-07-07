package com.music.web.services;

import com.music.web.models.Mp3File;
import com.music.web.repos.Mp3FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<Mp3File> getMp3FileById(Long id) {
        return mp3FileRepository.findById(id);
    }

    public List<Mp3File> getShuffledMp3Files(int count) {
            List<Mp3File> allFiles = mp3FileRepository.findAll();
        Collections.shuffle(allFiles);
        return allFiles.stream().limit(count).collect(Collectors.toList());
    }

    public void deleteMp3ById(Long id) {
        mp3FileRepository.deleteById(id);
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