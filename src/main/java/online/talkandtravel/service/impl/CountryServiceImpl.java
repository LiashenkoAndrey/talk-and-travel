package online.talkandtravel.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.CountryExistsException;
import online.talkandtravel.exception.UserAlreadySubscribedException;
import online.talkandtravel.model.Country;
import online.talkandtravel.model.Participant;
import online.talkandtravel.model.User;
import online.talkandtravel.model.dto.CountryDtoWithParticipantsAmountAndMessages;
import online.talkandtravel.model.dto.CountryWithUserDto;
import online.talkandtravel.model.dto.NewParticipantCountryDto;
import online.talkandtravel.model.dto.UserDtoWithAvatarAndPassword;
import online.talkandtravel.repository.CountryRepo;
import online.talkandtravel.repository.ParticipantRepository;
import online.talkandtravel.repository.UserRepo;
import online.talkandtravel.service.CountryService;
import online.talkandtravel.service.ParticipantService;
import online.talkandtravel.service.UserService;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Log4j2
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepo repository;
    private final UserService userService;
    private final ParticipantService participantService;
    private final UserRepo userRepo;
    private final ParticipantRepository participantRepository;

    @Override
    public Country save(Country country) {
        log.info("save country... {}", country);
        return repository.save(country);
    }

    @Override
    public Country findById(Long countryId) {
        return repository.findByIdCustom(countryId).orElseThrow(
                () -> new NoSuchElementException("Can not find Country by id " + countryId)
        );
    }

    @Override
    public CountryDtoWithParticipantsAmountAndMessages findByNameAndCreateIfNotExist(String name, Country country) {
        if (repository.existsByName(name)) {
            log.info("EXIST");
            return repository.findDtoByName(name);
        }
        log.info("Not exist, save...");
        save(country);
        return repository.findDtoByName(name);
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
        return repository.findCountriesByUserId(userId);
    }


    @Override
    public boolean userIsSubscribed(String countryName, Long userId) {
        log.info("userIsSubscribed countryId - {}, userId - {}", countryName, userId);
        return repository.isUserSubscribed(countryName, userId);
    }

    /**
     * Creates a new country
     * If country already exists - throw exception
     * @param country country dto
     * @return saved new country
     */
    @Override
    @Transactional
    public Country createAndSave(Country country) {
        ifCountryExistsThrowException(country);
        return repository.save(country);
    }

    /**
     * joins a user to a country
     * @param userId user id
     * @param countryName country entity
     */
    @Override
    @Transactional
    public void joinUserToCountry(Long userId, String countryName) {
        throwExceptionIfAlreadySubscribed(userId, countryName);
        log.info("joinUserToCountry userId - {}, country - {}", userId, countryName);
        Country country = repository.findByName(countryName).orElseThrow(EntityNotFoundException::new);
        Participant participant = Participant.builder()
                .user(userRepo.getReferenceById(userId))
                .countries(List.of(country))
                .build();
        Participant saved = participantService.save(participant);
        log.info("save ok - {}, {}", saved.getId(), saved.getCountries().size());

    }

    private void throwExceptionIfAlreadySubscribed(Long userId, String countryName) {
        Boolean isSubscribed = userIsSubscribed(countryName ,userId);
        log.info("isSubscribed {}", isSubscribed);
        if (isSubscribed) {
            throw new UserAlreadySubscribedException("User with id : "+ userId +
                    " already subscribed to a country with name " + countryName, "User already subscribed");
        }
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
        Set<UserDtoWithAvatarAndPassword> userDtoWithAvatarAndPasswords = mapParticipantsToUserDtos(country);
        return buildCountryWithUserDto(country, userDtoWithAvatarAndPasswords);
    }

    @Override
    public void addNewParticipantToCountry(NewParticipantCountryDto dto) {
        log.info("addNewParticipantToCountry - {}", dto);
        Optional<Participant> participantOptional = participantRepository.findByUserId(dto.getUserId());
        participantOptional.ifPresentOrElse((participant) -> {
            log.info("Participant exists - {}", participant.getId());
        },
                () -> {
            log.info("Participant not exists. create new record....");
            Participant participant = Participant.builder()
                    .user(userRepo.getReferenceById(dto.getUserId()))
                    .countries(List.of(repository.getReferenceById(dto.getId())))
                    .build();
            Participant saved = participantService.save(participant);
            log.info("save ok - {}, {}", saved.getId(), saved.getCountries().size());
        });
    }

    private CountryWithUserDto buildCountryWithUserDto(Country country, Set<UserDtoWithAvatarAndPassword> userDtoWithAvatarAndPasswords) {
        return CountryWithUserDto.builder()
                .id(country.getId())
                .name(country.getName())
                .flagCode(country.getFlagCode())
                .groupMessages(country.getGroupMessages())
                .participants(userDtoWithAvatarAndPasswords)
                .build();
    }

    private Set<UserDtoWithAvatarAndPassword> mapParticipantsToUserDtos(Country country) {
        return country.getParticipants().stream()
                .map(this::mapParticipantToUserDto)
                .collect(Collectors.toSet());
    }

    private UserDtoWithAvatarAndPassword mapParticipantToUserDto(Participant participant) {
        User user = participant.getUser();
        return UserDtoWithAvatarAndPassword.builder()
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
                .orElseGet(() -> participantService.createAndSave(user));
    }

    private Country getCountry(Long countryId) {
        return repository.findByIdCustom(countryId).orElseThrow(
                () -> new NoSuchElementException("The country does not exist yet.")
        );
    }

    private void ifCountryExistsThrowException(Country country) {
        var existingCountry = repository.findByName(country.getName());
        if (existingCountry.isPresent()) {
            throw new CountryExistsException("Country already exist");
        }
    }

    private void joinCountry(Country country, Participant participant) {
        log.info("joinCountry country - {}, participant - {}", country, participant.getCountries());
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
                .build();
    }
}
