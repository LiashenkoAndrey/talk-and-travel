package online.talkandtravel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository{}
/*
extends JpaRepository<Participant, Long> {
    @Query("SELECT p "
            + "FROM Participant p "
            + "JOIN p.countries c "
            + "WHERE p.user.id = :userId "
            + "AND c.id = :countryId")
    Optional<Participant> findByUserIdAndCountryId(@Param("userId") Long userId, @Param("countryId") Long countryId);

    Optional<Participant> findByUserId(Long userId);
}
*/
