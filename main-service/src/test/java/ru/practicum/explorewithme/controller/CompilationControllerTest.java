package ru.practicum.explorewithme.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.CompilationDto;
import ru.practicum.explorewithme.service.CompilationService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompilationController.class)
public class CompilationControllerTest {
    @Autowired
    private MockMvc mock;

    @MockBean
    private CompilationService service;

    @Test
    public void successfullyGetCompilations() throws Exception {
        CompilationDto compilation = new CompilationDto();

        compilation.setId(1);
        compilation.setTitle("title");

        when(service.getCompilations(any(), anyInt(), anyInt())).thenReturn(List.of(compilation));

        mock.perform(get("/compilations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("title"));
    }

    @Test
    public void successfullyGetCompilation() throws Exception {
        CompilationDto compilation = new CompilationDto();

        compilation.setId(1);
        compilation.setTitle("title");

        when(service.getCompilation(1)).thenReturn(compilation);

        mock.perform(get("/compilations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("title"));
    }
}