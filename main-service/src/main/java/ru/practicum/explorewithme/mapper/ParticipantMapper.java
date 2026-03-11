package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.model.Participant;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requestor.id")
    ParticipationRequestDto toParticipationRequestDto(Participant participant);

    List<ParticipationRequestDto> toParticipationRequestsDto(List<Participant> participants);
}