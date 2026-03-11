package ru.practicum.explorewithme.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.service.CategoryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {
    @Autowired
    private MockMvc mock;

    @MockBean
    private CategoryService service;

    @Test
    public void successfullyGetCategories() throws Exception {
        CategoryDto category = new CategoryDto();

        category.setId(1);
        category.setName("name");

        when(service.getCategories(anyInt(), anyInt())).thenReturn(List.of(category));

        mock.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("name"));
    }

    @Test
    public void successfullyGetCategory() throws Exception {
        CategoryDto category = new CategoryDto();

        category.setId(1);
        category.setName("name");

        when(service.getCategory(1)).thenReturn(category);

        mock.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"));
    }
}