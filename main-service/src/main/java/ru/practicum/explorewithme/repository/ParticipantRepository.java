package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.model.Participant;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    @Query("""
            SELECT p
            FROM Participant p
            WHERE p.event.id = :eventId
            AND p.status = ru.practicum.explorewithme.model.Status.PENDING
            """)
    List<Participant> findParticipantsByEventId(@Param("eventId") int eventId);

    @Query("""
            SELECT p
            FROM Participant p
            WHERE p.id IN :participantIds
            """)
    List<Participant> findParticipantsByIds(@Param("participantIds") List<Integer> participantIds);

    @Query("""
            SELECT p
            FROM Participant p
            WHERE p.event.id = :eventId
            AND p.status = ru.practicum.explorewithme.model.Status.PENDING
            """)
    List<Participant> findPendingParticipantsByEventId(@Param("eventId") int eventId);

    @Query("""
            SELECT COUNT(p)
            FROM Participant p
            WHERE p.event.id = :eventId
            AND p.status = ru.practicum.explorewithme.model.Status.CONFIRMED
            """)
    Long countConfirmedParticipants(@Param("eventId") Integer eventId);

    @Query("""
            SELECT p.event.id, COUNT(p)
            FROM Participant p
            WHERE p.event.id IN :eventIds
            AND p.status = ru.practicum.explorewithme.model.Status.CONFIRMED
            GROUP BY p.event.id
            """)
    List<Object[]> countConfirmedParticipants(@Param("eventIds") List<Integer> eventIds);

    List<Participant> findParticipantsByRequestorId(int requestorId);

    @Query("""
            SELECT COUNT(p)
            FROM Participant p
            WHERE p.event.id = :eventId
            AND p.status = ru.practicum.explorewithme.model.Status.CONFIRMED
            """)
    int countConfirmedParticipants(@Param("eventId") int eventId);

    Optional<Participant> findParticipantByIdAndRequestorId(int id, int requestorId);
}