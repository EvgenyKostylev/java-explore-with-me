package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.StatDto;
import ru.practicum.explorewithme.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {
    @Query("""
                    SELECT new ru.practicum.explorewithme.StatDto(
                    h.appName, 
                    h.uri, 
                    COUNT(h)
                    ) 
                    FROM Hit h
                    WHERE (h.timestamp >= :from)
                    AND (h.timestamp <= :to)
                    AND (h.uri IN :uris)
                    GROUP BY h.appName, h.uri
            """)
    List<StatDto> findStatsByTimestampAndUris(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("uris") List<String> uris
    );

    @Query("""
                    SELECT new ru.practicum.explorewithme.StatDto(
                    h.appName, 
                    h.uri, 
                    COUNT(DISTINCT h.ip)
                    ) 
                    FROM Hit h
                    WHERE (h.timestamp >= :from)
                    AND (h.timestamp <= :to)
                    AND (h.uri IN :uris)
                    GROUP BY h.appName, h.uri
            """)
    List<StatDto> findStatsByTimestampAndUrisUnique(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("uris") List<String> uris
    );
}