package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipantService {
    List<ParticipationRequestDto> getParticipantRequests(int userId, int eventId);

    EventRequestStatusUpdateResult updateRequestsParticipantEvent(
            int userId,
            int eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> getParticipantRequests(int userId);

    ParticipationRequestDto saveParticipantRequest(int userId, int eventId);

    ParticipationRequestDto updateParticipantRequest(int userId, int requestId);
}