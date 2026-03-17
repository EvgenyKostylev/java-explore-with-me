package ru.practicum.explorewithme.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.service.CommentService;
import ru.practicum.explorewithme.service.EventService;
import ru.practicum.explorewithme.service.ParticipantService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final EventService eventService;
    private final ParticipantService participantService;
    private final CommentService commentService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable int userId,
                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        return eventService.getEvents(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@PathVariable int userId,
                                  @RequestBody @Valid NewEventDto request) {
        return eventService.saveEvent(userId, request);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEvent(@PathVariable int userId, @PathVariable int eventId) {
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable int userId,
                                    @PathVariable int eventId,
                                    @RequestBody @Valid UpdateEventUserRequest request) {
        return eventService.updateEvent(userId, eventId, request);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipantRequests(@PathVariable int userId, @PathVariable int eventId) {
        return participantService.getParticipantRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsParticipantEvent(@PathVariable int userId,
                                                                         @PathVariable int eventId,
                                                                         @RequestBody
                                                                         @Valid
                                                                         EventRequestStatusUpdateRequest request) {
        return participantService.updateRequestsParticipantEvent(userId, eventId, request);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getParticipantRequests(@PathVariable int userId) {
        return participantService.getParticipantRequests(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveParticipantRequest(@PathVariable int userId,
                                                          @RequestParam(name = "eventId") int eventId) {
        return participantService.saveParticipantRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto updateParticipantRequest(@PathVariable int userId, @PathVariable int requestId) {
        return participantService.updateParticipantRequest(userId, requestId);
    }

    @GetMapping("/{userId}/comments")
    public List<CommentFullDto> getComments(@PathVariable int userId,
                                            @RequestParam(name = "events", required = false) List<Integer> events,
                                            @RequestParam(name = "from", defaultValue = "0") int from,
                                            @RequestParam(name = "size", defaultValue = "10") int size) {
        return commentService.getComments(userId, events, from, size);
    }

    @PostMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto saveComment(
            @PathVariable int userId,
            @PathVariable int eventId,
            @RequestBody @Valid NewCommentDto request) {
        return commentService.saveComment(userId, eventId, request);
    }

    @PatchMapping("/{userId}/comments/{commId}")
    public CommentFullDto updateComment(
            @PathVariable int userId,
            @PathVariable int commId,
            @RequestBody @Valid NewCommentDto request) {
        return commentService.updateComment(userId, commId, request);
    }
}