package online.talkandtravel.repository;

import java.util.List;
import java.util.Optional;
import online.talkandtravel.model.entity.UserCountry;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link UserCountry} entities.
 *
 * <p>This interface provides methods to perform CRUD operations on {@code UserCountry} entities,
 * including:
 *
 * <ul>
 *   <li>Finding all user-country associations for a given user through {@link #findByUserId(Long)}.
 *   <li>Finding a specific user-country association by country name and user ID through {@link
 *       #findByCountryNameAndUserId(String, Long)}.
 * </ul>
 */
public interface UserCountryRepository extends JpaRepository<UserCountry, Long> {

  List<UserCountry> findByUserId(Long userId);

  Optional<UserCountry> findByCountryNameAndUserId(String countryName, Long userId);
}
