package ru.practicum.explorewithme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.service.CategoryService;
import ru.practicum.explorewithme.service.CompilationService;
import ru.practicum.explorewithme.service.EventService;
import ru.practicum.explorewithme.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {
    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private EventService eventService;

    @MockBean
    private UserService userService;

    @MockBean
    private CompilationService compilationService;

    @Test
    public void successfullySaveCategory() throws Exception {
        NewCategoryDto request = new NewCategoryDto();

        request.setName("name");

        CategoryDto response = new CategoryDto();

        response.setId(1);
        response.setName("name");

        when(categoryService.saveCategory(any(NewCategoryDto.class))).thenReturn(response);

        mock.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    public void successfullyDeleteCategory() throws Exception {
        mock.perform(delete("/admin/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1);
    }

    @Test
    public void successfullyUpdateCategory() throws Exception {
        CategoryDto request = new CategoryDto();

        request.setName("newName");

        CategoryDto response = new CategoryDto();

        response.setId(1);
        response.setName("newName");

        when(categoryService.updateCategory(eq(1), any(CategoryDto.class))).thenReturn(response);

        mock.perform(patch(("/admin/categories/1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("newName"));
    }

    @Test
    public void successfullyGetUsers() throws Exception {
        UserDto user = new UserDto();

        user.setId(1);
        user.setName("name");

        when(userService.getUsers(eq(List.of(1)), anyInt(), anyInt())).thenReturn(List.of(user));

        mock.perform(get("/admin/users")
                        .param("ids", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("name"));
    }

    @Test
    public void successfullyCreateUser() throws Exception {
        NewUserRequest request = new NewUserRequest();

        request.setName("name");
        request.setEmail("usermail@mail.com");

        UserDto response = new UserDto();

        response.setId(1);
        response.setName("name");
        response.setEmail("usermail@mail.com");

        when(userService.saveUser(any(NewUserRequest.class))).thenReturn(response);

        mock.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    public void successfullyDeleteUser() throws Exception {
        mock.perform(delete("/admin/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1);
    }

    @Test
    public void successfullyGetEvents() throws Exception {
        EventFullDto event = new EventFullDto();

        event.setId(1);
        event.setAnnotation("annotation");

        when(eventService.getEvents(any(), any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(List.of(event));

        mock.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void successfullyUpdateEvent() throws Exception {
        UpdateEventAdminRequest request = new UpdateEventAdminRequest();

        EventFullDto response = new EventFullDto();

        response.setId(1);

        when(eventService.updateEvent(eq(1), any(UpdateEventAdminRequest.class))).thenReturn(response);

        mock.perform(patch(("/admin/events/1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void successfullySaveCompilation() throws Exception {
        NewCompilationDto request = new NewCompilationDto();

        request.setTitle("title");

        CompilationDto response = new CompilationDto();

        response.setId(1);
        response.setTitle("title");

        when(compilationService.saveCompilation(any(NewCompilationDto.class))).thenReturn(response);

        mock.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void successfullyDeleteCompilation() throws Exception {
        mock.perform(delete("/admin/compilations/1"))
                .andExpect(status().isNoContent());

        verify(compilationService).deleteCompilation(1);
    }

    @Test
    public void successfullyUpdateCompilation() throws Exception {
        UpdateCompilationRequest request = new UpdateCompilationRequest();

        CompilationDto response = new CompilationDto();

        response.setId(1);

        when(compilationService.updateCompilation(
                eq(1),
                any(UpdateCompilationRequest.class))).thenReturn(response);

        mock.perform(patch(("/admin/compilations/1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}