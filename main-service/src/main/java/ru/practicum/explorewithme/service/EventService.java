package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.dto.Sort;
import ru.practicum.explorewithme.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(int userId, int from, int size);

    EventFullDto saveEvent(int userId, NewEventDto newEventDto);

    EventFullDto getEventById(int userId, int eventId);

    EventFullDto updateEvent(int userId, int eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getEvents(
            List<Integer> users,
            List<State> states,
            List<Integer> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size);

    EventFullDto updateEvent(int eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getEvents(
            String text,
            List<Integer> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            Sort sort,
            int from,
            int size);

    EventFullDto getEvent(int eventId);

    Event getEventById(int eventId);

    Event getEventByInitiator(int userId, int eventId);
}