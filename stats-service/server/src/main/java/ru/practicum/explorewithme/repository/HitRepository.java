package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.StatDto;
import ru.practicum.explorewithme.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Integer> {
    @Query("""
                    SELECT new ru.practicum.explorewithme.StatDto(h.appName, h.uri, COUNT(h.ip))
                    FROM Hit h
                    WHERE h.timestamp BETWEEN :from AND :to
                    AND (:uris IS NULL OR h.uri IN :uris)
                    GROUP BY h.appName, h.uri
                    ORDER BY COUNT(h.ip) DESC
            """)
    List<StatDto> findStats(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("uris") List<String> uris
    );

    @Query("""
                    SELECT new ru.practicum.explorewithme.StatDto(h.appName, h.uri, COUNT(DISTINCT h.ip))
                    FROM Hit h
                    WHERE h.timestamp BETWEEN :from AND :to
                    AND (:uris IS NULL OR h.uri IN :uris)
                    GROUP BY h.appName, h.uri
                    ORDER BY COUNT(h.ip) DESC
            """)
    List<StatDto> findUniqueStats(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("uris") List<String> uris
    );
}