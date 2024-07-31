package online.talkandtravel.service.impl;

import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.Participant;
import online.talkandtravel.model.User;
import online.talkandtravel.repository.ParticipantRepository;
import online.talkandtravel.service.ParticipantService;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository repository;

    @Override
    public Participant save(Participant participant) {
       log.info("save new Participant - {}", participant);
        return repository.save(participant);
    }

    @Override
    public Optional<Participant> findByUserIdAndCountryId(Long userId, Long countryId) {
        return repository.findByUserIdAndCountryId(userId, countryId);
    }

    @Override
    public Participant createAndSave(User user) {
        return save(new Participant(user));
    }

    @Override
    @Transactional
    public String leaveCountry(Long userId, Long countryId) {
        var participant = repository.findByUserIdAndCountryId(userId, countryId)
                .orElseThrow(
                        () -> new NoSuchElementException("Can't find this participant!")
                );
        removeParticipantFromCountry(countryId, participant);
        repository.save(participant);
        String userName = participant.getUser().getUserName();
        return userName + " leave Country.";
    }

    private void removeParticipantFromCountry(Long countryId, Participant participant) {
        var country = participant.getCountries().stream()
                .filter(c -> c.getId().equals(countryId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Participant not found in country"));
        participant.getCountries().remove(country);
    }

}
