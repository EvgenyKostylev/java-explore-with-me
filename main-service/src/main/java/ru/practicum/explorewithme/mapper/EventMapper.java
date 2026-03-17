package ru.practicum.explorewithme.mapper;

import org.mapstruct.*;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.model.*;
import ru.practicum.explorewithme.model.Location;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {
    @Named("toShortWithoutStats")
    @Mapping(
            source = "initiator",
            target = "initiator",
            qualifiedByName = "toShort"
    )
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventShortDto toEventShortDto(Event event);

    @Named("toShort")
    @Mapping(
            source = "initiator",
            target = "initiator",
            qualifiedByName = "toShort"
    )
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventShortDto toEventShortDto(Event event, @Context EventStatsContext eventStatsContext);

    @IterableMapping(qualifiedByName = "toShort")
    @Mapping(
            source = "initiator",
            target = "initiator",
            qualifiedByName = "toShort"
    )
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    List<EventShortDto> toEventsShortDto(List<Event> event, @Context EventStatsContext eventStatsContext);

    @Named("toFull")
    @Mapping(
            source = "initiator",
            target = "initiator",
            qualifiedByName = "toShort"
    )
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "comments", ignore = true)
    EventFullDto toEventFullDto(
            Event event,
            @Context EventStatsContext eventStatsContext,
            @Context CommentMapper commentMapper);

    @IterableMapping(qualifiedByName = "toFull")
    @Mapping(
            source = "initiator",
            target = "initiator",
            qualifiedByName = "toShort"
    )
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "comments", ignore = true)
    List<EventFullDto> toEventsFullDto(
            List<Event> events,
            @Context EventStatsContext eventStatsContext,
            @Context CommentMapper commentMapper);

    @AfterMapping
    default void setAdditionalParameters(
            Event event,
            @MappingTarget EventShortDto eventShortDto,
            @Context EventStatsContext eventStatsContext) {
        eventShortDto.setConfirmedRequests(
                eventStatsContext.getParticipants().getOrDefault(event.getId(),
                        0));
        eventShortDto.setViews(
                eventStatsContext.getViews().getOrDefault(event.getId(),
                        0));
    }

    @AfterMapping
    default void setAdditionalParameters(
            Event event,
            @MappingTarget EventFullDto eventFullDto,
            @Context EventStatsContext eventStatsContext,
            @Context CommentMapper commentMapper) {
        eventFullDto.setConfirmedRequests(
                eventStatsContext.getParticipants().getOrDefault(event.getId(),
                        0));
        eventFullDto.setViews(
                eventStatsContext.getViews().getOrDefault(event.getId(),
                        0));
        eventFullDto.setComments(
                commentMapper.toCommentsShortDto(eventStatsContext.getComments().getOrDefault(
                        event.getId(),
                        List.of()))
        );
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "location", ignore = true)
    void updateEventFromAdminRequest(UpdateEventAdminRequest updateEventAdminRequest, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "location", ignore = true)
    void updateEventFromUserRequest(UpdateEventUserRequest updateEventUserRequest, @MappingTarget Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "location", source = "location")
    Event toEvent(NewEventDto newEventDto, Category category, User initiator, Location location);
}