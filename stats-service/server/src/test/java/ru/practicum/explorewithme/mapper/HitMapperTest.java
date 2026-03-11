package ru.practicum.explorewithme.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.model.Hit;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HitMapperTest {
    @Test
    public void hitDtoMapToHit() {
        LocalDateTime now = LocalDateTime.now();
        HitDto hitDto = HitDto.builder()
                .appName("name")
                .uri("uri")
                .ip("ip")
                .timestamp(now)
                .build();
        Hit hit = HitMapper.toHit(hitDto);

        assertEquals(hitDto.getAppName(), hit.getAppName());
        assertEquals(hitDto.getUri(), hit.getUri());
        assertEquals(hitDto.getIp(), hit.getIp());
        assertEquals(hitDto.getTimestamp(), hit.getTimestamp());
    }
}