package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("""
            SELECT u
            FROM User u
            WHERE (:ids IS NULL OR u.id IN :ids)
            """)
    List<User> findUsers(@Param("ids") List<Integer> ids, Pageable pageable);
}