package ru.practicum.explorewithme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class HitDtoTest {
    @Test
    public void hitDtoSerializeWithCustomNameAndDateWithCorrectPattern() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
        HitDto hitDto = HitDto.builder()
                .appName("name")
                .uri("uri")
                .ip("ip")
                .timestamp(dateTime)
                .build();

        String json = mapper.writeValueAsString(hitDto);

        assertThat(json).contains("\"app\":\"name\"");
        assertThat(json).contains("\"timestamp\":\"2025-01-01 12:00:00\"");
    }
}