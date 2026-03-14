package ru.practicum.explorewithme.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.expection.ConflictException;
import ru.practicum.explorewithme.model.*;
import ru.practicum.explorewithme.model.Location;
import ru.practicum.explorewithme.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
@Transactional
public class ParticipantServiceImplTest {
    @Autowired
    private ParticipantService service;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User initiator;
    private User user;
    private Category category;
    private Event event;
    private Location location;

    @BeforeEach
    public void beforeEach() {
        participantRepository.deleteAll();
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        initiator = new User();
        initiator.setName("name");
        initiator.setEmail("initiatorEmail");
        initiator = userRepository.save(initiator);

        user = new User();
        user.setName("name");
        user.setEmail("userEmail");
        user = userRepository.save(user);

        category = new Category();
        category.setName("name");
        category = categoryRepository.save(category);

        location = new Location();
        location.setLat(BigDecimal.valueOf(55.754167));
        location.setLon(BigDecimal.valueOf(37.62));
        location = locationRepository.save(location);

        event = new Event();
        event.setAnnotation("annotation");
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("description");
        event.setInitiator(initiator);
        event.setEventDate(LocalDateTime.now().plusHours(1));
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setTitle("title");
        event = eventRepository.save(event);
    }

    @AfterAll
    public void afterAll() {
        participantRepository.deleteAll();
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getParticipantRequestsByInitiator() {
        Participant participant = new Participant();

        participant.setStatus(Status.PENDING);
        participant.setEvent(event);
        participant.setCreated(LocalDateTime.now());
        participant.setRequestor(user);
        participantRepository.save(participant);

        List<ParticipationRequestDto> participants = service.getParticipantRequests(initiator.getId(), event.getId());

        assertNotNull(participants);
        assertEquals(1, participants.size());
    }

    @Test
    public void updateRequestsParticipantEventStatusCONFIRMED() {
        Participant participant = new Participant();

        participant.setStatus(Status.PENDING);
        participant.setEvent(event);
        participant.setCreated(LocalDateTime.now());
        participant.setRequestor(user);
        participantRepository.save(participant);

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();

        request.setRequestIds(List.of(participant.getId()));
        request.setStatus(Status.CONFIRMED);

        EventRequestStatusUpdateResult result = service.updateRequestsParticipantEvent(
                initiator.getId(),
                event.getId(),
                request);

        assertNotNull(result);
        assertEquals(user.getId(), result.getConfirmedRequests().getFirst().getRequester());
    }

    @Test
    public void updateParticipationRequestWithStatusCONFIRMED() {
        Participant participant = new Participant();

        participant.setStatus(Status.CONFIRMED);
        participant.setEvent(event);
        participant.setCreated(LocalDateTime.now());
        participant.setRequestor(user);
        participantRepository.save(participant);

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();

        request.setRequestIds(List.of(participant.getId()));
        request.setStatus(Status.CONFIRMED);

        assertThrows(ConflictException.class, () -> service.updateRequestsParticipantEvent(
                initiator.getId(),
                event.getId(),
                request));
    }

    @Test
    public void updateRequestsParticipantEventStatusREJECTED() {
        Participant participant = new Participant();

        participant.setStatus(Status.PENDING);
        participant.setEvent(event);
        participant.setCreated(LocalDateTime.now());
        participant.setRequestor(user);
        participantRepository.save(participant);

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();

        request.setRequestIds(List.of(participant.getId()));
        request.setStatus(Status.REJECTED);

        EventRequestStatusUpdateResult result = service.updateRequestsParticipantEvent(
                initiator.getId(),
                event.getId(),
                request);

        assertNotNull(result);
        assertEquals(user.getId(), result.getRejectedRequests().getFirst().getRequester());
    }

    @Test
    public void updateRequestsParticipantEventWithParticipantLimit() {
        event.setParticipantLimit(1);
        event.setRequestModeration(true);
        event = eventRepository.save(event);

        Participant firstParticipant = new Participant();

        firstParticipant.setStatus(Status.CONFIRMED);
        firstParticipant.setEvent(event);
        firstParticipant.setCreated(LocalDateTime.now());
        firstParticipant.setRequestor(user);
        participantRepository.save(firstParticipant);

        Participant secondParticipant = new Participant();
        User secondUser = new User();

        secondUser.setName("name");
        secondUser.setEmail("secondUserEmail");
        secondUser = userRepository.save(secondUser);
        secondParticipant.setStatus(Status.PENDING);
        secondParticipant.setEvent(event);
        secondParticipant.setCreated(LocalDateTime.now());
        secondParticipant.setRequestor(secondUser);
        secondParticipant = participantRepository.save(secondParticipant);

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();

        request.setRequestIds(List.of(secondParticipant.getId()));
        request.setStatus(Status.CONFIRMED);
        assertThrows(ConflictException.class, () -> service.updateRequestsParticipantEvent(
                initiator.getId(),
                event.getId(),
                request));
    }

    @Test
    public void getParticipantRequestsByUser() {
        Participant participant = new Participant();

        participant.setStatus(Status.PENDING);
        participant.setEvent(event);
        participant.setCreated(LocalDateTime.now());
        participant.setRequestor(user);
        participantRepository.save(participant);

        List<ParticipationRequestDto> participants = service.getParticipantRequests(user.getId());

        assertNotNull(participants);
        assertEquals(1, participants.size());
    }

    @Test
    public void saveParticipantRequestToEventWithRequestModerationFALSE() {
        ParticipationRequestDto request = service.saveParticipantRequest(user.getId(), event.getId());

        assertEquals(user.getId(), request.getRequester());
        assertEquals(event.getId(), request.getEvent());
        assertEquals(Status.CONFIRMED, request.getStatus());
    }

    @Test
    public void saveParticipantRequestByInitiator() {
        assertThrows(ConflictException.class, () -> service.saveParticipantRequest(initiator.getId(), event.getId()));
    }

    @Test
    public void saveParticipantRequestOnEventWithStatePENDING() {
        event.setState(State.PENDING);
        event = eventRepository.save(event);

        assertThrows(ConflictException.class, () -> service.saveParticipantRequest(user.getId(), event.getId()));
    }

    @Test
    public void saveParticipantRequestOnEventWithParticipantLimit() {
        event.setParticipantLimit(1);
        event.setRequestModeration(true);
        event = eventRepository.save(event);

        Participant firstParticipant = new Participant();

        firstParticipant.setStatus(Status.CONFIRMED);
        firstParticipant.setEvent(event);
        firstParticipant.setCreated(LocalDateTime.now());
        firstParticipant.setRequestor(user);
        participantRepository.save(firstParticipant);

        User secondUser = new User();

        secondUser.setName("name");
        secondUser.setEmail("secondUserEmail");
        secondUser = userRepository.save(secondUser);

        int secondUserId = secondUser.getId();

        assertThrows(ConflictException.class, () -> service.saveParticipantRequest(secondUserId, event.getId()));
    }

    @Test
    public void saveParticipantRequestToEventWithRequestModerationTRUE() {
        event.setParticipantLimit(1);
        event.setRequestModeration(true);
        event = eventRepository.save(event);

        ParticipationRequestDto request = service.saveParticipantRequest(user.getId(), event.getId());

        assertEquals(user.getId(), request.getRequester());
        assertEquals(event.getId(), request.getEvent());
        assertEquals(Status.PENDING, request.getStatus());
    }

    @Test
    public void updateParticipantRequest() {
        Participant participant = new Participant();

        participant.setStatus(Status.CONFIRMED);
        participant.setEvent(event);
        participant.setCreated(LocalDateTime.now());
        participant.setRequestor(user);
        participant = participantRepository.save(participant);

        ParticipationRequestDto request = service.updateParticipantRequest(user.getId(), participant.getId());

        assertEquals(user.getId(), request.getRequester());
        assertEquals(event.getId(), request.getEvent());
        assertEquals(Status.CANCELED, request.getStatus());
    }
}