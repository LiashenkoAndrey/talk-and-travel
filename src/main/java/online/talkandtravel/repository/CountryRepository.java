package online.talkandtravel.repository;

import online.talkandtravel.model.Country;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    @Query("SELECT c "
            + "FROM Country c "
            + "LEFT JOIN FETCH c.groupMessages "
            + "LEFT JOIN FETCH c.participants "
            + "ORDER BY c.name")
    List<Country> findAllSortedByName();

    @Query("SELECT c "
            + "FROM Country c "
            + "LEFT JOIN FETCH c.groupMessages "
            + "LEFT JOIN FETCH c.participants "
            + "WHERE c.name = :name")
    Optional<Country> findByName(@Param("name") String name);

    @Query("SELECT COUNT (DISTINCT p.user.id) "
            + "FROM  Participant p "
            + "JOIN p.countries c "
            + "WHERE c.id = :countryId")
    Long countUsersInCountry(Long countryId);

    @Query("SELECT NEW Country(c.id, c.name, c.flagCode) "
            + "FROM Participant p "
            + "JOIN p.countries c "
            + "WHERE p.user.id = :userId")
    Optional<List<Country>> findCountriesByUserId(@Param("userId") Long userId);

    @Query("SELECT c "
            + "FROM Country c "
            + "LEFT JOIN FETCH c.groupMessages "
            + "LEFT JOIN FETCH c.participants "
            + "WHERE c.id = :id")
    Optional<Country> findByIdCustom(@Param("id") Long id);

    @Query("SELECT c "
            + "FROM Country c "
            + "LEFT JOIN FETCH c.groupMessages gm "
            + "LEFT JOIN FETCH c.participants p "
            + "LEFT JOIN FETCH p.user u "
            + "WHERE c.id = :id")
    Optional<Country> findByIdWithParticipants(@Param("id") Long id);
}
