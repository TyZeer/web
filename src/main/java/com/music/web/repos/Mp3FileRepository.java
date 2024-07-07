package com.music.web.repos;


import com.music.web.models.Mp3File;
import org.springframework.data.jpa.repository.JpaRepository;


public interface Mp3FileRepository extends JpaRepository<Mp3File, Long> {
}
