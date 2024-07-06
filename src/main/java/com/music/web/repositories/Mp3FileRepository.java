package com.music.web.repositories;


import com.music.web.models.Mp3File;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface Mp3FileRepository extends ReactiveCrudRepository<Mp3File, Long> {
}
