package online.talkandtravel.service.impl;

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
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository repository;

    @Override
    public Participant save(Participant participant) {
        return repository.save(participant);
    }

    @Override
    public Optional<Participant> findByUserIdAndCountryId(Long userId, Long countryId) {
        return repository.findByUserIdAndCountryId(userId, countryId);
    }

    @Override
    public Participant create(User user) {
        var participant = createNewParticipant(user);
        return save(participant);
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

    private Participant createNewParticipant(User user) {
        return Participant.builder()
                .user(user)
                .countries(new ArrayList<>())
                .build();
    }
}
