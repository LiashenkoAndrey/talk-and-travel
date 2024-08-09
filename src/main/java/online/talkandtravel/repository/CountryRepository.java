package online.talkandtravel.repository;

import online.talkandtravel.model.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Country} entities.
 *
 * <p>This interface provides methods to perform CRUD operations on {@code Country} entities,
 * including:
 *
 * <ul>
 *   <li>Standard CRUD operations inherited from {@link JpaRepository}.
 *   <li>{@code countCountries()} - Returns the total count of {@code Country} entities.
 * </ul>
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, String> {

  @Query("SELECT COUNT(c) FROM Country c")
  long countCountries();
}
