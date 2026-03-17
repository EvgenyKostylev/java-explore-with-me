package ru.practicum.explorewithme.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.CommentFullDto;
import ru.practicum.explorewithme.dto.NewCommentDto;
import ru.practicum.explorewithme.expection.ConflictException;
import ru.practicum.explorewithme.expection.ForbiddenException;
import ru.practicum.explorewithme.expection.NotFoundException;
import ru.practicum.explorewithme.mapper.CommentMapper;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.Condition;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.CommentRepository;
import ru.practicum.explorewithme.repository.ParticipantRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final ParticipantRepository participantRepository;

    private final UserService userService;

    private final EventService eventService;

    private final CommentMapper mapper;

    @Override
    public CommentFullDto getComment(int commId) {
        Comment comment = getCommentById(commId);

        return mapper.toCommentFullDto(comment);
    }

    @Override
    public List<CommentFullDto> getComments(
            List<Integer> commentators,
            List<Integer> events,
            Condition condition,
            int from,
            int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        if (commentators == null || commentators.isEmpty()
                || (commentators.size() == 1 && commentators.getFirst() == 0)) {
            commentators = null;
        }

        if (events == null || events.isEmpty() || (events.size() == 1 && events.getFirst() == 0)) {
            events = null;
        }

        List<Comment> comments = commentRepository.findCommentsByCommentatorIdsAndEventIdsAndCondition(
                commentators,
                events,
                condition,
                pageable);

        return mapper.toCommentsFullDto(comments);
    }

    @Override
    @Transactional
    public CommentFullDto updateCommentCondition(int commId, Condition condition) {
        Comment comment = getCommentById(commId);

        if (comment.getCondition() == Condition.PUBLISHED) {
            throw new ForbiddenException("Cannot update comment condition because it published");
        }

        if (condition != Condition.PUBLISHED && condition != Condition.CANCELED) {
            throw new ForbiddenException(String.format("Cannot update comment condition on %s", condition));
        }

        comment.setCondition(condition);

        return mapper.toCommentFullDto(comment);
    }

    @Override
    public List<CommentFullDto> getComments(int userId, List<Integer> events, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findCommentsByCommentatorIdAndEventIds(userId, events, pageable);

        return mapper.toCommentsFullDto(comments);
    }

    @Override
    public CommentFullDto saveComment(int userId, int eventId, NewCommentDto newCommentDto) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        if (event.getInitiator().getId() == userId) {
            throw new ConflictException(
                    "The event initiator cannot submit comment on their own event");
        }

        if (!participantRepository.existsByRequestorIdAndEventId(userId, eventId)) {
            throw new ConflictException("Cannot comment on an event you did not participate in");
        }

        Comment comment = mapper.toComment(newCommentDto, user, event);

        comment = commentRepository.save(comment);

        return mapper.toCommentFullDto(comment);
    }

    @Override
    @Transactional
    public CommentFullDto updateComment(int userId, int commId, NewCommentDto newCommentDto) {
        Comment comment = getCommentById(commId);

        if (comment.getCommentator().getId() != userId) {
            throw new NotFoundException(String.format("Comment with id=%d was not found", commId));
        }

        mapper.updateComment(newCommentDto, comment);

        return mapper.toCommentFullDto(comment);
    }

    private Comment getCommentById(int commId) {
        Optional<Comment> commentOptional = commentRepository.findById(commId);

        if (commentOptional.isEmpty()) {
            throw new NotFoundException(String.format("Comment with id=%d was not found", commId));
        }

        return commentOptional.get();
    }
}