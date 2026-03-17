package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.Condition;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.commentator.id = :commentatorId
            AND (:events IS NULL OR c.event.id IN :events)
            """)
    List<Comment> findCommentsByCommentatorIdAndEventIds(
            @Param("commentatorId") Integer commentatorId,
            @Param("events") List<Integer> events,
            Pageable pageable);

    @Query("""
            SELECT c
            FROM Comment c
            WHERE (:commentators IS NULL OR c.commentator.id IN :commentators)
            AND (:events IS NULL OR c.event.id IN :events)
            AND (:condition IS NULL OR c.condition = :condition)
            """)
    List<Comment> findCommentsByCommentatorIdsAndEventIdsAndCondition(
            @Param("commentators") List<Integer> commentators,
            @Param("events") List<Integer> events,
            @Param("condition") Condition condition,
            Pageable pageable);

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.event.id = :eventId
            AND c.condition = ru.practicum.explorewithme.model.Condition.PUBLISHED
            """)
    List<Comment> findCommentsByEventIdAndCondition(@Param("eventId") int eventId);

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.event.id IN :events
            AND c.condition = ru.practicum.explorewithme.model.Condition.PUBLISHED
            """)
    List<Comment> findCommentsByEventIdsAndCondition(@Param("events") List<Integer> events);
}