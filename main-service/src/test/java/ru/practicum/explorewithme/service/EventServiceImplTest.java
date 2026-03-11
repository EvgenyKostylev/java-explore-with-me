package ru.practicum.explorewithme.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.expection.ForbiddenException;
import ru.practicum.explorewithme.expection.NotFoundException;
import ru.practicum.explorewithme.model.*;
import ru.practicum.explorewithme.model.Location;
import ru.practicum.explorewithme.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
@Transactional
public class EventServiceImplTest {
    @Autowired
    private EventService service;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private StatsClient statsClient;

    private User user;
    private Category category;
    private Location location;

    @BeforeEach
    public void beforeEach() {
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("name");
        user.setEmail("email");
        user = userRepository.save(user);

        category = new Category();
        category.setName("name");
        category = categoryRepository.save(category);

        location = new Location();
        location.setLat(BigDecimal.valueOf(55.754167));
        location.setLon(BigDecimal.valueOf(37.62));
        location = locationRepository.save(location);

        when(statsClient.get(any(), any(), any(), any())).thenReturn(Collections.emptyList());
    }

    @AfterAll
    public void afterAll() {
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getEventsByInitiator() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(1));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        event = eventRepository.save(event);

        List<EventShortDto> events = service.getEvents(user.getId(), 0, 10);

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(event.getId(), events.getFirst().getId());
    }

    @Test
    public void saveEvent() {
        NewEventDto newEventDto = new NewEventDto();
        ru.practicum.explorewithme.dto.Location location = new ru.practicum.explorewithme.dto.Location();

        location.setLat(55.754167f);
        location.setLon(37.62f);

        newEventDto.setAnnotation("annotation");
        newEventDto.setCategory(category.getId());
        newEventDto.setDescription("description");
        newEventDto.setEventDate(LocalDateTime.now().plusHours(2));
        newEventDto.setLocation(location);
        newEventDto.setPaid(false);
        newEventDto.setParticipantLimit(0);
        newEventDto.setRequestModeration(false);
        newEventDto.setTitle("title");

        EventFullDto eventFullDto = service.saveEvent(user.getId(), newEventDto);

        assertEquals(newEventDto.getAnnotation(), eventFullDto.getAnnotation());
    }

    @Test
    public void saveEventWithNotRequirementDate() {
        NewEventDto newEventDto = new NewEventDto();
        ru.practicum.explorewithme.dto.Location location = new ru.practicum.explorewithme.dto.Location();

        location.setLat(55.754167f);
        location.setLon(37.62f);

        newEventDto.setAnnotation("annotation");
        newEventDto.setCategory(category.getId());
        newEventDto.setDescription("description");
        newEventDto.setEventDate(LocalDateTime.now());
        newEventDto.setLocation(location);
        newEventDto.setPaid(false);
        newEventDto.setParticipantLimit(0);
        newEventDto.setRequestModeration(false);
        newEventDto.setTitle("title");

        assertThrows(ForbiddenException.class, () -> service.saveEvent(user.getId(), newEventDto));
    }

    @Test
    public void getEventById() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(1));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        event = eventRepository.save(event);

        EventFullDto eventFullDto = service.getEventById(user.getId(), event.getId());

        assertEquals(event.getAnnotation(), eventFullDto.getAnnotation());
    }

    @Test
    public void getEventByIdByNotTheInitiator() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(1));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        event = eventRepository.save(event);

        int eventId = event.getId();

        assertThrows(NotFoundException.class, () -> service.getEventById(1000, eventId));
    }

    @Test
    public void updateEventByInitiator() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(3));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PENDING);
        event.setTitle("title");
        event = eventRepository.save(event);

        UpdateEventUserRequest updateEventUserRequest = new UpdateEventUserRequest();

        updateEventUserRequest.setAnnotation("newAnnotation");
        updateEventUserRequest.setStateAction(StateActionUser.SEND_TO_REVIEW);

        EventFullDto eventFullDto = service.updateEvent(user.getId(), event.getId(), updateEventUserRequest);

        assertEquals(event.getId(), eventFullDto.getId());
        assertEquals(updateEventUserRequest.getAnnotation(), eventFullDto.getAnnotation());
    }

    @Test
    public void updateEventByInitiatorWithStatePUBLISHED() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(3));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        event = eventRepository.save(event);

        int eventId = event.getId();
        UpdateEventUserRequest updateEventUserRequest = new UpdateEventUserRequest();

        updateEventUserRequest.setAnnotation("newAnnotation");
        updateEventUserRequest.setStateAction(StateActionUser.SEND_TO_REVIEW);
        assertThrows(ForbiddenException.class, () -> service.updateEvent(
                user.getId(),
                eventId,
                updateEventUserRequest));
    }

    @Test
    public void updateEventByInitiatorWithNotRequirementDate() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(3));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PENDING);
        event.setTitle("title");
        event = eventRepository.save(event);

        int eventId = event.getId();
        UpdateEventUserRequest updateEventUserRequest = new UpdateEventUserRequest();

        updateEventUserRequest.setAnnotation("newAnnotation");
        updateEventUserRequest.setStateAction(StateActionUser.SEND_TO_REVIEW);
        updateEventUserRequest.setEventDate(LocalDateTime.now());

        assertThrows(ForbiddenException.class, () -> service.updateEvent(
                user.getId(),
                eventId,
                updateEventUserRequest));
    }

    @Test
    public void updateEventByInitiatorWithNotRequirementDateEvent() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now());
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PENDING);
        event.setTitle("title");
        event = eventRepository.save(event);

        int eventId = event.getId();
        UpdateEventUserRequest updateEventUserRequest = new UpdateEventUserRequest();

        updateEventUserRequest.setAnnotation("newAnnotation");
        updateEventUserRequest.setStateAction(StateActionUser.SEND_TO_REVIEW);

        assertThrows(ForbiddenException.class, () -> service.updateEvent(
                user.getId(),
                eventId,
                updateEventUserRequest));
    }

    @Test
    public void getEventsByAdmin() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(1));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        event = eventRepository.save(event);

        List<EventFullDto> events = service.getEvents(
                List.of(user.getId()),
                null,
                null,
                null,
                null,
                0,
                10);

        assertEquals(1, events.size());
        assertEquals(event.getInitiator().getId(), events.getFirst().getInitiator().getId());
        assertEquals(event.getAnnotation(), events.getFirst().getAnnotation());
    }

    @Test
    public void updateEventByAdminActionPUBLISHED() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(2));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PENDING);
        event.setTitle("title");
        event = eventRepository.save(event);

        UpdateEventAdminRequest updateEventAdminRequest = new UpdateEventAdminRequest();

        updateEventAdminRequest.setAnnotation("newAnnotation");
        updateEventAdminRequest.setStateActionAdmin(StateActionAdmin.PUBLISH_EVENT);

        EventFullDto eventFullDto = service.updateEvent(event.getId(), updateEventAdminRequest);

        assertEquals(event.getId(), eventFullDto.getId());
        assertEquals(updateEventAdminRequest.getAnnotation(), eventFullDto.getAnnotation());
    }

    @Test
    public void updateEventByAdminActionPUBLISHEDWithNotRequirementDateEvent() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now());
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PENDING);
        event.setTitle("title");
        event = eventRepository.save(event);

        int eventId = event.getId();
        UpdateEventAdminRequest updateEventAdminRequest = new UpdateEventAdminRequest();

        updateEventAdminRequest.setAnnotation("newAnnotation");
        updateEventAdminRequest.setStateActionAdmin(StateActionAdmin.PUBLISH_EVENT);
        assertThrows(ForbiddenException.class, () -> service.updateEvent(eventId, updateEventAdminRequest));
    }

    @Test
    public void updateEventByAdminActionPUBLISHEDWithStatePUBLISHED() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now());
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        event = eventRepository.save(event);

        int eventId = event.getId();
        UpdateEventAdminRequest updateEventAdminRequest = new UpdateEventAdminRequest();

        updateEventAdminRequest.setAnnotation("newAnnotation");
        updateEventAdminRequest.setStateActionAdmin(StateActionAdmin.PUBLISH_EVENT);
        assertThrows(ForbiddenException.class, () -> service.updateEvent(eventId, updateEventAdminRequest));
    }

    @Test
    public void updateEventByAdminActionREJECTWithStatePUBLISHED() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(2));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        event = eventRepository.save(event);

        int eventId = event.getId();
        UpdateEventAdminRequest updateEventAdminRequest = new UpdateEventAdminRequest();

        updateEventAdminRequest.setAnnotation("newAnnotation");
        updateEventAdminRequest.setStateActionAdmin(StateActionAdmin.REJECT_EVENT);
        assertThrows(ForbiddenException.class, () -> service.updateEvent(eventId, updateEventAdminRequest));
    }

    @Test
    public void getEventsPublic() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(1));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        event = eventRepository.save(event);

        List<EventShortDto> events = service.getEvents(
                event.getAnnotation(),
                null,
                null,
                null,
                null,
                false,
                null,
                0,
                10);

        assertEquals(1, events.size());
        assertEquals(event.getInitiator().getId(), events.getFirst().getInitiator().getId());
        assertEquals(event.getAnnotation(), events.getFirst().getAnnotation());
    }

    @Test
    public void getEventsPublicSortByEVENTDATE() {
        Event firstEvent = new Event();
        firstEvent.setAnnotation("annotation");
        firstEvent.setCategory(category);
        firstEvent.setCreatedOn(LocalDateTime.now());
        firstEvent.setDescription("description");
        firstEvent.setInitiator(user);
        firstEvent.setEventDate(LocalDateTime.now().plusHours(1));
        firstEvent.setLocation(location);
        firstEvent.setPaid(false);
        firstEvent.setParticipantLimit(0);
        firstEvent.setRequestModeration(false);
        firstEvent.setState(State.PUBLISHED);
        firstEvent.setTitle("title");
        firstEvent = eventRepository.save(firstEvent);

        Event secondEvent = new Event();
        secondEvent.setAnnotation("annotation");
        secondEvent.setCategory(category);
        secondEvent.setCreatedOn(LocalDateTime.now());
        secondEvent.setDescription("description");
        secondEvent.setInitiator(user);
        secondEvent.setEventDate(LocalDateTime.now().plusHours(2));
        secondEvent.setLocation(location);
        secondEvent.setPaid(false);
        secondEvent.setParticipantLimit(0);
        secondEvent.setRequestModeration(false);
        secondEvent.setState(State.PUBLISHED);
        secondEvent.setTitle("title");
        secondEvent = eventRepository.save(secondEvent);

        List<EventShortDto> events = service.getEvents(
                null,
                null,
                null,
                null,
                null,
                false,
                Sort.EVENT_DATE,
                0,
                10);

        assertEquals(2, events.size());
        assertEquals(firstEvent.getId(), events.getFirst().getId());
        assertEquals(secondEvent.getId(), events.getLast().getId());
    }

    @Test
    public void getEvent() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(1));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        event = eventRepository.save(event);

        EventFullDto eventFullDto = service.getEvent(event.getId());

        assertEquals(event.getId(), eventFullDto.getId());
        assertEquals(event.getAnnotation(), eventFullDto.getAnnotation());
    }

    @Test
    public void getEventWithStatePENDING() {
        Event event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(user);
        event.setEventDate(LocalDateTime.now().plusHours(1));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PENDING);
        event.setTitle("title");
        event = eventRepository.save(event);

        int eventId = event.getId();

        assertThrows(NotFoundException.class, () -> service.getEvent(eventId));
    }

    @Test
    public void getNonExistingEvent() {
        assertThrows(NotFoundException.class, () -> service.getEvent(1000));
    }
}