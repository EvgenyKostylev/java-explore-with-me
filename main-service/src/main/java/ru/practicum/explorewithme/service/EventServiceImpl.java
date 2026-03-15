package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.StatDto;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.expection.BadRequestException;
import ru.practicum.explorewithme.expection.ForbiddenException;
import ru.practicum.explorewithme.expection.NotFoundException;
import ru.practicum.explorewithme.mapper.CommentMapper;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.mapper.LocationMapper;
import ru.practicum.explorewithme.model.*;
import ru.practicum.explorewithme.model.Location;
import ru.practicum.explorewithme.repository.CommentRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.LocationRepository;
import ru.practicum.explorewithme.repository.ParticipantRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final EventMapper eventMapper;
    private final CommentMapper commentMapper;
    private final StatsClient statsClient;
    private final CategoryService categoryService;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Override
    public List<EventShortDto> getEvents(int userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findEventsByInitiatorId(userId, pageable);

        log.info("Get events by initiator={}: {}", userId, events.size());

        return mapToEventsShortDto(events);
    }

    @Override
    public EventFullDto saveEvent(int userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException(String.format(
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s",
                    newEventDto.getEventDate()));
        }

        Category category = categoryService.getCategoryById(newEventDto.getCategory());
        User initiator = userService.getUserById(userId);
        Location location = locationRepository.save(locationMapper.toLocation(newEventDto.getLocation()));
        Event event = eventMapper.toEvent(newEventDto, category, initiator, location);

        eventRepository.save(event);

        log.info("Save event by user with id={}: {}", userId, event);

        return mapToEventFullDto(event);
    }

    @Override
    public EventFullDto getEventById(int userId, int eventId) {
        Event event = getEventByInitiator(userId, eventId);

        log.info("Get event by user with id={}: {}", userId, event);

        return mapToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(int userId, int eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = getEventByInitiator(userId, eventId);

        if (event.getState() == State.PUBLISHED) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }

        if (updateEventUserRequest.getEventDate() != null) {
            if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException(
                        "Cannot pending the event " +
                                "because the event start date is earlier than two hour after the start date");
            }
        } else if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(
                    "Cannot pending the event " +
                            "because the event start date is earlier than two hour after the start date");
        }

        log.info("Update event with id={} by user with id={}: {}", eventId, userId, event);

        return mapToEventFullDto(updateEventFromUserRequest(updateEventUserRequest, event));
    }

    @Override
    public List<EventFullDto> getEvents(
            List<Integer> users,
            List<State> states,
            List<Integer> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        if (users == null || users.isEmpty() || (users.size() == 1 && users.getFirst() == 0)) {
            users = null;
        }

        if (categories == null || categories.isEmpty() || (categories.size() == 1 && categories.getFirst() == 0)) {
            categories = null;
        }

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusMonths(1);
        }

        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusMonths(1);
        }

        List<Event> events = eventRepository.findEvents(users, states, categories, rangeStart, rangeEnd, pageable);

        log.info("Get events by admin: {}", events.size());

        return mapToEventsFullDto(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(int eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = getEventById(eventId);

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
                if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new ForbiddenException(
                            "Cannot publish the event " +
                                    "because the event start date is earlier than one hour after the publication date");
                }

                if (event.getState() != State.PENDING) {
                    throw new ForbiddenException(String.format(
                            "Cannot publish the event because it's not in the right state: %s",
                            event.getState()));
                }
            } else if (updateEventAdminRequest.getStateAction() == StateActionAdmin.REJECT_EVENT) {
                if (event.getState() == State.PUBLISHED) {
                    throw new ForbiddenException(String.format(
                            "Cannot reject the event because it's already in the state: %s",
                            event.getState()));
                }
            }
        }

        if (updateEventAdminRequest.getEventDate() != null
                && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event date must be in the future");
        }

        Event updatedEvent = updateEventFromAdminRequest(updateEventAdminRequest, event);

        log.info("Update event: {}", updatedEvent);

        return mapToEventFullDto(updatedEvent);
    }

    @Override
    public List<EventShortDto> getEvents(
            String text,
            List<Integer> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            Sort sort,
            int from,
            int size) {
        Pageable pageable;
        List<EventShortDto> eventsShortDto;

        if (text == null || text.isBlank() || "0".equals(text)) {
            text = "%";
        } else {
            text = "%" + text + "%";
        }

        if (categories == null || categories.isEmpty() || (categories.size() == 1 && categories.getFirst() == 0)) {
            categories = null;
        }

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("RangeStart must be before rangeEnd");
        }

        rangeStart = (rangeStart != null) ? rangeStart : LocalDateTime.now();

        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusMonths(1);
        }

        if (sort == null) {
            pageable = PageRequest.of(from / size, size);
            eventsShortDto = mapToEventsShortDto(eventRepository.findEventsByParametersPageable(
                    text,
                    categories,
                    paid,
                    rangeStart,
                    rangeEnd,
                    onlyAvailable,
                    pageable));
        } else {
            switch (sort) {
                case EVENT_DATE:
                    pageable = PageRequest.of(
                            from / size,
                            size,
                            org.springframework.data.domain.Sort.by("eventDate"));
                    eventsShortDto = mapToEventsShortDto(eventRepository.findEventsByParametersPageable(
                            text,
                            categories,
                            paid,
                            rangeStart,
                            rangeEnd,
                            onlyAvailable,
                            pageable));
                    break;
                case VIEWS:
                    eventsShortDto = mapToEventsShortDto(eventRepository.findEventsByParameters(
                            text,
                            categories,
                            paid,
                            rangeStart,
                            rangeEnd,
                            onlyAvailable));
                    eventsShortDto = eventsShortDto.stream()
                            .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                            .skip(from)
                            .limit(size).toList();
                    break;
                default:
                    pageable = PageRequest.of(from / size, size);
                    eventsShortDto = mapToEventsShortDto(eventRepository.findEventsByParametersPageable(
                            text,
                            categories,
                            paid,
                            rangeStart,
                            rangeEnd,
                            onlyAvailable,
                            pageable));
            }
        }

        log.info("Get events: {}", eventsShortDto.size());

        return eventsShortDto;
    }

    @Override
    public EventFullDto getEvent(int eventId) {
        Event event = getEventById(eventId);

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }

        log.info("Get event: {}", event);

        return mapToEventFullDto(event);
    }

    @Override
    public Event getEventById(int eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isEmpty()) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        } else {
            log.info("Find event with id={}", eventId);

            return eventOptional.get();
        }
    }

    @Override
    public Event getEventByInitiator(int userId, int eventId) {
        Event event = getEventById(eventId);

        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException(String.format("Event with id=%d was not found", event.getId()));
        } else {
            log.info("Find event with id={} by initiator with id={}", eventId, userId);

            return event;
        }
    }

    private Event updateEventFromUserRequest(UpdateEventUserRequest updateEventUserRequest, Event event) {
        eventMapper.updateEventFromUserRequest(updateEventUserRequest, event);

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(updateEventUserRequest.getCategory()));
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
            }
        }

        if (updateEventUserRequest.getLocation() != null) {
            Location location = locationRepository.save(
                    locationMapper.toLocation(updateEventUserRequest.getLocation()));

            event.setLocation(location);
        }

        return event;
    }

    private Event updateEventFromAdminRequest(UpdateEventAdminRequest updateEventAdminRequest, Event event) {
        eventMapper.updateEventFromAdminRequest(updateEventAdminRequest, event);

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(updateEventAdminRequest.getCategory()));
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(State.CANCELED);
                    break;
            }
        }

        if (updateEventAdminRequest.getLocation() != null) {
            Location location = locationRepository.save(
                    locationMapper.toLocation(updateEventAdminRequest.getLocation()));

            event.setLocation(location);
        }

        return event;
    }

    private Map<Integer, Integer> getEventViews(Integer eventId, LocalDateTime eventPublishedDate) {
        int views = 0;

        if (eventPublishedDate != null) {
            List<StatDto> statsDto = statsClient.get(
                    eventPublishedDate,
                    LocalDateTime.now(),
                    List.of("/events/" + eventId),
                    true);

            if (statsDto != null && !statsDto.isEmpty()) {
                views = statsDto.getFirst().getHitCount().intValue();
            }
        }

        return Map.of(eventId, views);
    }

    private Map<Integer, Integer> getEventsViewsMap(List<Integer> eventIds) {
        List<String> eventUris = eventIds.stream().map(id -> String.format("/events/%d", id)).toList();
        List<StatDto> statsDto = statsClient.get(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().plusMonths(1),
                eventUris,
                true);

        return statsDto.stream().collect(Collectors.toMap(
                stats -> Integer.parseInt(stats.getUri().split("/")[2]),
                stats -> stats.getHitCount().intValue()
        ));
    }

    private Map<Integer, Integer> getEventParticipants(Integer eventId) {
        return Map.of(eventId, participantRepository.countConfirmedParticipants(eventId).intValue());
    }

    private Map<Integer, Integer> getEventsParticipationsMap(List<Integer> eventIds) {
        return participantRepository.countConfirmedParticipants(eventIds).stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> ((Number) r[1]).intValue()));
    }

    private Map<Integer, List<Comment>> getEventComments(Integer eventId) {
        return commentRepository.findCommentsByEventIdAndCondition(eventId).stream()
                .collect(Collectors.groupingBy(comment -> comment.getEvent().getId()));
    }

    private Map<Integer, List<Comment>> getEventsCommentsMap(List<Integer> eventIds) {
        return commentRepository.findCommentsByEventIdsAndCondition(eventIds).stream()
                .collect(Collectors.groupingBy(comment -> comment.getEvent().getId()));
    }

    private EventFullDto mapToEventFullDto(Event event) {
        EventStatsContext eventStatsContext = new EventStatsContext(
                getEventParticipants(event.getId()),
                getEventViews(event.getId(), event.getPublishedOn()),
                getEventComments(event.getId()));

        return eventMapper.toEventFullDto(event, eventStatsContext, commentMapper);
    }

    private List<EventShortDto> mapToEventsShortDto(List<Event> events) {
        List<Integer> eventIds = events.stream().map(Event::getId).toList();
        EventStatsContext eventStatsContext = new EventStatsContext(
                getEventsParticipationsMap(eventIds),
                getEventsViewsMap(eventIds),
                null);

        return eventMapper.toEventsShortDto(events, eventStatsContext);
    }

    private List<EventFullDto> mapToEventsFullDto(List<Event> events) {
        List<Integer> eventIds = events.stream().map(Event::getId).toList();
        EventStatsContext eventStatsContext = new EventStatsContext(
                getEventsParticipationsMap(eventIds),
                getEventsViewsMap(eventIds),
                getEventsCommentsMap(eventIds));

        return eventMapper.toEventsFullDto(events, eventStatsContext, commentMapper);
    }
}