package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.expection.ConflictException;
import ru.practicum.explorewithme.expection.NotFoundException;
import ru.practicum.explorewithme.mapper.ParticipantMapper;
import ru.practicum.explorewithme.model.*;
import ru.practicum.explorewithme.repository.ParticipantRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository repository;
    private final EventService eventService;
    private final UserService userService;
    private final ParticipantMapper mapper;

    @Override
    public List<ParticipationRequestDto> getParticipantRequests(int userId, int eventId) {
        eventService.getEventByInitiator(userId, eventId);

        List<Participant> participants = repository.findParticipantsByEventId(eventId);

        log.info(
                "Get participant requests for event with id={} by user with id={}: {}",
                eventId,
                userId,
                participants.size());

        return mapper.toParticipationRequestsDto(participants);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsParticipantEvent(
            int userId,
            int eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = eventService.getEventByInitiator(userId, eventId);
        List<Participant> participants = repository.findParticipantsByIds(
                eventRequestStatusUpdateRequest.getRequestIds());
        Map<Integer, Integer> confirmedRequestsMap = repository.countConfirmedParticipants(List.of(eventId)).stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> ((Number) r[1]).intValue()));
        int confirmedRequestsCount = confirmedRequestsMap.getOrDefault(eventId, 0);
        Status newStatus = eventRequestStatusUpdateRequest.getStatus();

        List<Participant> confirmed = new ArrayList<>();
        List<Participant> rejected = new ArrayList<>();

        for (Participant p : participants) {
            if (p.getStatus() != Status.PENDING) {
                throw new ConflictException("Only requests in PENDING status can be updated");
            }

            if (newStatus == Status.REJECTED) {
                p.setStatus(Status.REJECTED);
                rejected.add(p);
                continue;
            }

            if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
                p.setStatus(Status.CONFIRMED);
                confirmed.add(p);
                continue;
            }

            if (confirmedRequestsCount >= event.getParticipantLimit()) {
                throw new ConflictException("The participant limit has been reached");
            }

            p.setStatus(Status.CONFIRMED);
            confirmed.add(p);
            confirmedRequestsCount++;
        }

        if (confirmedRequestsCount >= event.getParticipantLimit()) {
            List<Participant> pendingRequests = repository.findPendingParticipantsByEventId(eventId);

            for (Participant p : pendingRequests) {
                p.setStatus(Status.REJECTED);
                rejected.add(p);
            }
        }

        repository.saveAll(participants);

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();

        eventRequestStatusUpdateResult.setConfirmedRequests(mapper.toParticipationRequestsDto(confirmed));
        eventRequestStatusUpdateResult.setRejectedRequests(mapper.toParticipationRequestsDto(rejected));

        log.info(
                "Update requests for event with id={} by user with id={}: confirmed: {}, rejected: {}",
                eventId,
                userId,
                eventRequestStatusUpdateResult.getConfirmedRequests().size(),
                eventRequestStatusUpdateResult.getRejectedRequests().size());

        return eventRequestStatusUpdateResult;
    }

    @Override
    public List<ParticipationRequestDto> getParticipantRequests(int userId) {
        userService.getUserById(userId);

        List<Participant> requests = repository.findParticipantsByRequestorId(userId);

        log.info("Get participant requests for user with id={}: {}", userId, requests.size());

        return mapper.toParticipationRequestsDto(requests);
    }

    @Override
    public ParticipationRequestDto saveParticipantRequest(int userId, int eventId) {
        User requestor = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        if (event.getInitiator().getId() == userId) {
            throw new ConflictException(
                    "The event initiator cannot submit a participation request for their own event");
        }

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Participation in an unpublished event is not allowed");
        }

        if (event.getParticipantLimit() != 0
                && repository.countConfirmedParticipants(eventId) >= event.getParticipantLimit()) {
            throw new ConflictException("The participation request limit for this event has been reached");
        }

        Participant participant = Participant.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requestor(requestor).build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participant.setStatus(Status.CONFIRMED);
        } else {
            participant.setStatus(Status.PENDING);
        }

        participant = repository.save(participant);

        log.info("Save participant request {}", participant);

        return mapper.toParticipationRequestDto(participant);
    }

    @Override
    @Transactional
    public ParticipationRequestDto updateParticipantRequest(int userId, int requestId) {
        Participant participant = repository.findParticipantByIdAndRequestorId(
                requestId,
                userId).orElseThrow(() -> new NotFoundException(String.format(
                "Request with id=%d was not found",
                requestId)));

        participant.setStatus(Status.REJECTED);

        log.info("Participant request with id={} has been rejected", requestId);

        return mapper.toParticipationRequestDto(participant);
    }
}