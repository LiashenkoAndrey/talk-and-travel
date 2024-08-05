package online.talkandtravel.util;

import org.springframework.stereotype.Component;

@Component
public class CountryUtil {}/*

  @Autowired private ParticipantRepository participantRepository;

  @Autowired private CountryRepository countryRepo;

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