package online.talkandtravel.repository;

import online.talkandtravel.model.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Event} entities.
 *
 * <p>This interface provides methods to perform CRUD operations on {@code Event} entities,
 * including:
 *
 * <ul>
 *   <li>Standard CRUD operations inherited from {@link JpaRepository}.
 * </ul>
 */
public interface EventRepository extends JpaRepository<Event, Long> {}
