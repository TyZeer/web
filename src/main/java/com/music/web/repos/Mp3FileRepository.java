package com.music.web.repos;


import com.music.web.dto.Mp3FileDto;
import com.music.web.models.Mp3File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface Mp3FileRepository extends JpaRepository<Mp3File, Long> {

    @Query("SELECT new com.music.web.dto.Mp3FileDto(m.id, m.fileName) FROM Mp3File m")
    List<Mp3FileDto> findAllMp3Files();
}
