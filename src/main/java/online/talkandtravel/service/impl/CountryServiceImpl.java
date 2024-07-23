package online.talkandtravel.service.impl;

import online.talkandtravel.model.Country;
import online.talkandtravel.model.Participant;
import online.talkandtravel.model.User;
import online.talkandtravel.model.dto.CountryWithUserDto;
import online.talkandtravel.model.dto.UserDto;
import online.talkandtravel.repository.CountryRepository;
import online.talkandtravel.service.CountryService;
import online.talkandtravel.service.ParticipantService;
import online.talkandtravel.service.UserService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository repository;
    private final UserService userService;
    private final ParticipantService participantService;

    @Override
    public Country save(Country country) {
        return repository.save(country);
    }

    @Override
    public Country findById(Long countryId) {
        return repository.findById(countryId).orElseThrow(
                () -> new NoSuchElementException("Can not find Country by id " + countryId)
        );
    }

    @Override
    @Transactional
    public Country findByName(String countryMame) {
        return repository.findByName(countryMame).orElseThrow(
                () -> new NoSuchElementException("Can not find Country by mane " + countryMame)
        );
    }

    @Override
    public List<Country> getAll() {
        return repository.findAllSortedByName();
    }

    @Override
    public Long countUsersInCountry(Long countryId) {
        return repository.countUsersInCountry(countryId);
    }

    @Override
    public List<Country> findAllCountriesByUser(Long userId) {
        return repository.findCountriesByUserId(userId).orElseThrow(
                () -> new NoSuchElementException("The User is not a participant of any Country")
        );
    }


    @Override
    @Transactional
    public Country create(Country country, Long userId) {
        ifCountryExistsThrowException(country);
        var user = userService.findById(userId);
        var participant = participantService.create(user);
        var newCountry = createNewCountry(country);
        joinCountry(newCountry, participant);
        return repository.save(newCountry);
    }

    @Override
    @Transactional
    public Country update(Long countryId, Long userId) {
        var country = getCountry(countryId);
        var participant = getParticipant(countryId, userId);
        joinCountry(country, participant);
        return repository.save(country);
    }

    @Override
    @Transactional
    public CountryWithUserDto findByIdWithParticipants(Long countryId) {
        Country country = findCountryByIdWithParticipants(countryId);
        Set<UserDto> userDtos = mapParticipantsToUserDtos(country);
        return buildCountryWithUserDto(country, userDtos);
    }

    private CountryWithUserDto buildCountryWithUserDto(Country country, Set<UserDto> userDtos) {
        return CountryWithUserDto.builder()
                .id(country.getId())
                .name(country.getName())
                .flagCode(country.getFlagCode())
                .groupMessages(country.getGroupMessages())
                .participants(userDtos)
                .build();
    }

    private Set<UserDto> mapParticipantsToUserDtos(Country country) {
        return country.getParticipants().stream()
                .map(this::mapParticipantToUserDto)
                .collect(Collectors.toSet());
    }

    private UserDto mapParticipantToUserDto(Participant participant) {
        User user = participant.getUser();
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .avatar(user.getAvatar())
                .about(user.getAbout())
                .build();
    }

    private Country findCountryByIdWithParticipants(Long countryId) {
        return repository.findByIdWithParticipants(countryId).orElseThrow(
                () -> new NoSuchElementException("Can not find Country by id " + countryId)
        );
    }


    private Participant getParticipant(Long countryId, Long userId) {
        var user = userService.findById(userId);
        return participantService.findByUserIdAndCountryId(userId, countryId)
                .orElseGet(() -> participantService.create(user));
    }

    private Country getCountry(Long countryId) {
        return repository.findById(countryId).orElseThrow(
                () -> new NoSuchElementException("The country does not exist yet.")
        );
    }

    private void ifCountryExistsThrowException(Country country) {
        var existingCountry = repository.findByName(country.getName());
        if (existingCountry.isPresent()) {
            throw new RuntimeException("Country already exist");
        }
    }

    private void joinCountry(Country country, Participant participant) {
        if (!country.getParticipants().contains(participant)) {
            country.getParticipants().add(participant);
        }
        if (!participant.getCountries().contains(country)) {
            participant.getCountries().add(country);
        }
    }

    private Country createNewCountry(Country country) {
        return Country.builder()
                .name(country.getName())
                .flagCode(country.getFlagCode())
                .groupMessages(new ArrayList<>())
                .participants(new HashSet<>())
                .build();
    }
}
