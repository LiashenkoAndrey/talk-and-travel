package online.talkandtravel.service;

import online.talkandtravel.model.Participant;
import online.talkandtravel.model.User;

import java.util.Optional;

public interface ParticipantService {
    Participant save(Participant participant);

    Optional<Participant> findByUserIdAndCountryId(Long userId, Long countryId);

    Participant create(User user);

    String leaveCountry(Long userId, Long countryId);
}
