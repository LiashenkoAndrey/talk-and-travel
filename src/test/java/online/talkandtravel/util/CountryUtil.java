package online.talkandtravel.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import online.talkandtravel.model.Country;
import online.talkandtravel.model.User;
import online.talkandtravel.repository.CountryRepo;
import online.talkandtravel.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CountryUtil {}/*

  @Autowired private ParticipantRepository participantRepository;

  @Autowired private CountryRepo countryRepo;

  @PersistenceContext private EntityManager em;

  public void saveParticipant(User user, Long countryId) {
    Participant participant = participantRepository.save(new Participant(user));

    em.createNativeQuery(
            "insert into public.participant_countries(country_id, participant_id) values (:country_id, :participant_id)")
        .setParameter("country_id", countryId)
        .setParameter("participant_id", participant.getId())
        .executeUpdate();
  }

  public Country createCountry(String name, String flagCode) {

    return countryRepo
        .findByName(name)
        .orElse(countryRepo.save(Country.builder().name(name).flagCode(flagCode).build()));
  }

  public void deleteAllCountries() {
    countryRepo.deleteAll();
  }
}
*/