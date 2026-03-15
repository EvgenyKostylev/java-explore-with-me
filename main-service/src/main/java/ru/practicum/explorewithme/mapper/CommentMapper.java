package ru.practicum.explorewithme.mapper;

import org.mapstruct.*;
import ru.practicum.explorewithme.dto.CommentFullDto;
import ru.practicum.explorewithme.dto.CommentShortDto;
import ru.practicum.explorewithme.dto.NewCommentDto;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commentator", source = "user")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "condition", constant = "PENDING")
    @Mapping(target = "commentDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "editedOn", ignore = true)
    Comment toComment(NewCommentDto newCommentDto, User user, Event event);

    @Named("toShort")
    @Mapping(
            source = "commentator",
            target = "commentator",
            qualifiedByName = "toShort"
    )
    @Mapping(source = "event.id", target = "eventId")
    CommentShortDto toCommentShortDto(Comment comment);

    @IterableMapping(qualifiedByName = "toShort")
    List<CommentShortDto> toCommentsShortDto(List<Comment> comments);

    @Named("toFull")
    @Mapping(source = "event.id", target = "eventId")
    CommentFullDto toCommentFullDto(Comment comment);

    @IterableMapping(qualifiedByName = "toFull")
    List<CommentFullDto> toCommentsFullDto(List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commentator", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "condition", constant = "EDITED")
    @Mapping(target = "commentDate", ignore = true)
    @Mapping(target = "editedOn", expression = "java(java.time.LocalDateTime.now())")
    void updateComment(NewCommentDto newCommentDto, @MappingTarget Comment comment);
}