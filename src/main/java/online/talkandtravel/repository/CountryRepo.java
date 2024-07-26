package online.talkandtravel.repository;

import online.talkandtravel.model.Country;
import java.util.List;
import java.util.Optional;

import online.talkandtravel.model.dto.ICountryDto;
import online.talkandtravel.model.dto.IParticipantDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {


    @Query(value = """
    select
        count(*) > 0 as is_subscribed
        from participant_countries p join public.countries c on c.id = p.country_id
        where c.name = :countryName and p.participant_id = :userId
    """, nativeQuery = true)
    boolean isUserSubscribed(@Param("countryName") String countryName, @Param("userId") Long userId);

    boolean existsByName(String name);

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

    @Query("SELECT distinct NEW Country(c.id, c.name, c.flagCode) "
            + "FROM Participant p "
            + "left join p.countries c "
            + "WHERE p.user.id = :userId")
    List<Country> findCountriesByUserId(@Param("userId") Long userId);



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

    @Query("select new online.talkandtravel.model.dto.IParticipantDto(p.id, new online.talkandtravel.model.dto.UserDtoShort(u.id, u.userName, u.userEmail)) " +
            "from Country c " +
            "join c.participants p " +
            "join p.user u " +
            "where c.id = :chatId")
    List<IParticipantDto> findAllParticipantsByChatId(@Param("chatId") Long chatId);

    @Query("select c.name as name, c.id as id, c.flagCode as flagCode from Country c where c.id = :id")
    Optional<ICountryDto> findDtoById(Long id);
}
