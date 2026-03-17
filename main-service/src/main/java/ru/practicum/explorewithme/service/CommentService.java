package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.CommentFullDto;
import ru.practicum.explorewithme.dto.NewCommentDto;
import ru.practicum.explorewithme.model.Condition;

import java.util.List;

public interface CommentService {
    CommentFullDto getComment(int commId);

    List<CommentFullDto> getComments(
            List<Integer> users,
            List<Integer> events,
            Condition condition,
            int from,
            int size);

    CommentFullDto updateCommentCondition(int commId, Condition condition);

    List<CommentFullDto> getComments(int userId, List<Integer> events, int from, int size);

    CommentFullDto saveComment(int userId, int eventId, NewCommentDto newCommentDto);

    CommentFullDto updateComment(int userId, int commIdm, NewCommentDto newCommentDto);
}