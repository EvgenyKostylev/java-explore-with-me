package ru.practicum.explorewithme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StatDtoTest {
    @Test
    public void statDtoSerializeWithCustomName() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        StatDto statDto = StatDto.builder()
                .appName("name")
                .uri("uri")
                .hitCount(1L)
                .build();

        String json = mapper.writeValueAsString(statDto);

        assertThat(json).contains("\"app\":\"name\"");
        assertThat(json).contains("\"hits\":1");
    }
}