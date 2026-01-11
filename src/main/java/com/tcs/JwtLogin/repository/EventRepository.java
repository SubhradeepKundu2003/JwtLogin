package com.tcs.JwtLogin.repository;

import com.tcs.JwtLogin.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByHallIdAndEventDate(Long hallId, java.time.LocalDate eventDate);

    @Query("""
        SELECT DISTINCT e FROM Event e
        LEFT JOIN FETCH e.seats
        WHERE e.addedBy.id = :userId
        ORDER BY e.eventDate DESC
    """)
    List<Event> findAllByOrganizerWithSeats(Long userId);

    @Query("""
    SELECT e FROM Event e
    LEFT JOIN FETCH e.seats
    WHERE e.id = :eventId
      AND e.addedBy.id = :userId
""")
    Optional<Event> findByIdAndOrganizerWithSeats(Long eventId, Long userId);

    Optional<Event> findByIdAndAddedById(Long eventId, Long userId);

}
