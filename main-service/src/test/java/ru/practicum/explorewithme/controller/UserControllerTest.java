package ru.practicum.explorewithme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.service.CommentService;
import ru.practicum.explorewithme.service.EventService;
import ru.practicum.explorewithme.service.ParticipantService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mock;

    @MockBean
    private EventService eventService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private ParticipantService participantService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void successfullyGetEvents() throws Exception {
        EventShortDto event = new EventShortDto();

        event.setId(1);

        when(eventService.getEvents(eq(1), anyInt(), anyInt())).thenReturn(List.of(event));

        mock.perform(get("/users/1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void successfullyCreateEvent() throws Exception {
        Location location = new Location();

        location.setLat(32.32f);
        location.setLon(32.32f);

        NewEventDto request = new NewEventDto();

        request.setLocation(location);
        request.setEventDate(LocalDateTime.now());
        request.setCategory(1);
        request.setAnnotation("annotation for tested event");
        request.setTitle("title");
        request.setDescription("description for rested event");

        EventFullDto response = new EventFullDto();

        response.setId(1);

        when(eventService.saveEvent(eq(1), any(NewEventDto.class))).thenReturn(response);

        mock.perform(post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void successfullyGetEvent() throws Exception {
        EventFullDto event = new EventFullDto();

        event.setId(1);

        when(eventService.getEventById(1, 1)).thenReturn(event);

        mock.perform(get("/users/1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void successfullyUpdateEvent() throws Exception {
        UpdateEventUserRequest request = new UpdateEventUserRequest();
        EventFullDto response = new EventFullDto();

        response.setId(1);

        when(eventService.updateEvent(
                eq(1),
                eq(1),
                any(UpdateEventUserRequest.class))).thenReturn(response);

        mock.perform(patch("/users/1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void successfullyGetParticipantRequests() throws Exception {
        ParticipationRequestDto request = new ParticipationRequestDto();

        request.setId(1);

        when(participantService.getParticipantRequests(1, 1)).thenReturn(List.of(request));

        mock.perform(get("/users/1/events/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void successfullyParticipantRequestsUpdate() throws Exception {
        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        EventRequestStatusUpdateResult response = new EventRequestStatusUpdateResult();

        when(participantService.updateRequestsParticipantEvent(
                eq(1),
                eq(1),
                any(EventRequestStatusUpdateRequest.class))).thenReturn(response);

        mock.perform(patch("/users/1/events/1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void successfullyGetRequests() throws Exception {
        ParticipationRequestDto participationRequest = new ParticipationRequestDto();

        participationRequest.setId(1);

        when(participantService.getParticipantRequests(1)).thenReturn(List.of(participationRequest));

        mock.perform(get("/users/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void successfullySaveRequest() throws Exception {
        ParticipationRequestDto response = new ParticipationRequestDto();

        response.setId(1);

        when(participantService.saveParticipantRequest(1, 1)).thenReturn(response);

        mock.perform(post("/users/1/requests")
                        .param("eventId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void successfullyUpdateRequest() throws Exception {
        ParticipationRequestDto response = new ParticipationRequestDto();

        response.setId(1);

        when(participantService.updateParticipantRequest(1, 1)).thenReturn(response);

        mock.perform(patch("/users/1/requests/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void successfullyGetComments() throws Exception {
        CommentFullDto response = new CommentFullDto();

        response.setId(1);

        when(commentService.getComments(eq(1), any(), anyInt(), anyInt())).thenReturn(List.of(response));

        mock.perform(get("/users/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void successfullySaveComment() throws Exception {
        NewCommentDto request = new NewCommentDto();

        request.setComment("comment test save");

        CommentFullDto response = new CommentFullDto();

        response.setId(1);
        response.setComment("comment test save");

        when(commentService.saveComment(eq(1), eq(1), any(NewCommentDto.class))).thenReturn(response);

        mock.perform(post("/users/1/events/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comment").value("comment test save"));
    }

    @Test
    public void successfullyUpdateComment() throws Exception {
        NewCommentDto request = new NewCommentDto();

        request.setComment("comment test update");

        CommentFullDto response = new CommentFullDto();

        response.setId(1);
        response.setComment("comment test update");

        when(commentService.updateComment(eq(1), eq(1), any(NewCommentDto.class))).thenReturn(response);

        mock.perform(patch("/users/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comment").value("comment test update"));
    }
}