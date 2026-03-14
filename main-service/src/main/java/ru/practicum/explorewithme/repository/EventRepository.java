package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.model.State;
import ru.practicum.explorewithme.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findEventsByInitiatorId(int userId, Pageable pageable);

    boolean existsByCategoryId(int categoryId);

    @Query("""
            SELECT e
            FROM Event e
            WHERE (:users IS NULL OR e.initiator.id IN :users)
            AND (:states IS NULL OR e.state IN :states)
            AND (:categories IS NULL OR e.category.id IN :categories)
            AND (e.eventDate >= :rangeStart)
            AND (e.eventDate <= :rangeEnd)
            """)
    List<Event> findEvents(
            @Param("users") List<Integer> users,
            @Param("states") List<State> states,
            @Param("categories") List<Integer> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    @Query("""
            SELECT e
            FROM Event e
            WHERE e.state = ru.practicum.explorewithme.model.State.PUBLISHED
            AND (
                        :text IS NULL
                                    OR LOWER(e.annotation) LIKE LOWER(:text) OR LOWER(e.description) LIKE LOWER(:text)
                                                            )
            AND (:categories IS NULL OR e.category.id IN :categories)
            AND (:paid IS NULL OR e.paid = :paid)
            AND e.eventDate >= :rangeStart
            AND (e.eventDate <= :rangeEnd)
            AND (
            :onlyAvailable = false OR e.participantLimit = 0
            OR (
            SELECT COUNT(p)
            FROM Participant p
            WHERE p.event.id = e.id
            AND p.status = ru.practicum.explorewithme.model.Status.CONFIRMED
            ) < e.participantLimit
            )
            """)
    List<Event> findEventsByParametersPageable(
            @Param("text") String text,
            @Param("categories") List<Integer> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            boolean onlyAvailable,
            Pageable pageable);

    @Query("""
            SELECT e
            FROM Event e
            WHERE e.state = ru.practicum.explorewithme.model.State.PUBLISHED
            AND (
                        :text IS NULL
                                    OR LOWER(e.annotation) LIKE LOWER(:text) OR LOWER(e.description) LIKE LOWER(:text)
                                                            )
            AND (:categories IS NULL OR e.category.id IN :categories)
            AND (:paid IS NULL OR e.paid = :paid)
            AND e.eventDate >= :rangeStart
            AND (e.eventDate <= :rangeEnd)
            AND (
            :onlyAvailable = false OR e.participantLimit = 0
            OR (
            SELECT COUNT(p)
            FROM Participant p
            WHERE p.event.id = e.id
            AND p.status = ru.practicum.explorewithme.model.Status.CONFIRMED
            ) < e.participantLimit
            )
            """)
    List<Event> findEventsByParameters(
            @Param("text") String text,
            @Param("categories") List<Integer> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") boolean onlyAvailable);
}