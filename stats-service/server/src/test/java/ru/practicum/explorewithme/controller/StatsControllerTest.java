package ru.practicum.explorewithme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.HitDto;
import ru.practicum.explorewithme.StatDto;
import ru.practicum.explorewithme.service.StatsServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
public class StatsControllerTest {
    @Autowired
    private MockMvc mock;

    @MockBean
    private StatsServiceImpl service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void hitSuccessfulSave() throws Exception {
        HitDto request = HitDto.builder()
                .appName("name")
                .uri("uri")
                .ip("ip")
                .timestamp(LocalDateTime.now())
                .build();

        doNothing().when(service).save(any(HitDto.class));

        mock.perform(
                        post("/hit")
                                .content(mapper.writeValueAsString(request))
                                .contentType("application/json"))
                .andExpect(status()
                        .isCreated());
    }

    @Test
    public void statsGetWhereUriGetAndGetAll() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        List<StatDto> response = List.of(StatDto.builder()
                        .appName("name")
                        .uri("/get")
                        .hitCount(1)
                        .build(),
                StatDto.builder()
                        .appName("name")
                        .uri("/getAll")
                        .hitCount(2)
                        .build());

        when(service.get(now, now, List.of("/get", "/getAll"), false)).thenReturn(response);

        mock.perform(
                        get("/stats")
                                .param("uris", "/get", "/getAll")
                                .param("from", now.toString())
                                .param("to", now.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()")
                        .value(response.size()));
    }
}