package ru.practicum.explorewithme.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.StatDto;
import ru.practicum.explorewithme.model.Hit;
import ru.practicum.explorewithme.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
@Transactional
public class StatsServiceImplTest {
    @Autowired
    private StatsService service;

    @Autowired
    private HitRepository repository;

    @BeforeEach
    public void beforeEach() {
        repository.deleteAll();
    }

    @AfterAll
    public void afterAll() {
        repository.deleteAll();
    }

    @Test
    public void saveHit() {
        HitDto hitDto = HitDto.builder()
                .appName("name")
                .uri("uri")
                .ip("ip")
                .timestamp(LocalDateTime.now())
                .build();

        service.save(hitDto);

        List<Hit> hits = repository.findAll();

        assertNotNull(hits);
        assertEquals(1, hits.size());
        assertEquals(hitDto.getAppName(), hits.getFirst().getAppName());
    }

    @Test
    public void getStats() {
        HitDto hitDto = HitDto.builder()
                .appName("name")
                .uri("uri")
                .ip("ip")
                .timestamp(LocalDateTime.now())
                .build();

        service.save(hitDto);

        List<StatDto> stats = service.get(null, null, List.of("uri"), false);

        assertNotNull(stats);
        assertEquals(1, stats.size());
        assertEquals(hitDto.getUri(), stats.getFirst().getUri());
    }

    @Test
    public void getStatWithUniqueTrue() {
        HitDto firstHitDto = HitDto.builder()
                .appName("name")
                .uri("uri")
                .ip("ip")
                .timestamp(LocalDateTime.now())
                .build();

        service.save(firstHitDto);

        HitDto secondHitDto = HitDto.builder()
                .appName("name")
                .uri("uri").ip("ip")
                .timestamp(LocalDateTime.now())
                .build();

        service.save(secondHitDto);

        List<StatDto> stats = service.get(null, null, List.of("uri"), true);

        assertNotNull(stats);
        assertEquals(1, stats.size());
    }
}