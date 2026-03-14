package ru.practicum.explorewithme.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.service.EventService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerTest {
    @Autowired
    private MockMvc mock;

    @MockBean
    private EventService service;

    @MockBean
    private StatsClient statsClient;

    @Test
    public void successfullyGetEvents() throws Exception {
        EventShortDto event = new EventShortDto();

        event.setId(1);
        event.setAnnotation("annotation");

        when(service.getEvents(
                any(),
                any(),
                any(),
                any(),
                any(),
                anyBoolean(),
                any(),
                anyInt(),
                anyInt())).thenReturn(List.of(event));

        mock.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].annotation").value("annotation"));

        verify(statsClient).save(any());
    }

    @Test
    public void successfullyGetEvent() throws Exception {
        EventFullDto event = new EventFullDto();

        event.setId(1);
        event.setAnnotation("annotation");

        when(service.getEvent(1)).thenReturn(event);

        mock.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.annotation").value("annotation"));

        verify(statsClient).save(any());
    }
}