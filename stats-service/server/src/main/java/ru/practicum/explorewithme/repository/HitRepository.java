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
                    CASE
                        WHEN :unique = true THEN COUNT(DISTINCT h.ip)
                        ELSE COUNT(h)
                    END
                    )
                    FROM Hit h
                    WHERE h.timestamp BETWEEN :from AND :to
                    AND (:uris IS NULL OR h.uri IN :uris)
                    GROUP BY h.appName, h.uri
                    ORDER BY 3 DESC
            """)
    List<StatDto> findStats(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("uris") List<String> uris,
            @Param("unique") boolean unique
    );
}