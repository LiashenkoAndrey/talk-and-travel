package online.talkandtravel.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.country.CountryNotFoundException;
import online.talkandtravel.model.dto.country.CountryDto;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.UserCountry;
import online.talkandtravel.repository.CountryRepository;
import online.talkandtravel.repository.UserCountryRepository;
import online.talkandtravel.service.CountryService;
import online.talkandtravel.util.mapper.CountryMapper;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

  private final CountryRepository countryRepository;
  private final UserCountryRepository userCountryRepository;
  private final CountryMapper countryMapper;

  @Override
  public List<CountryInfoDto> getAllCountriesInfo() {
    return countryRepository.findAll().stream().map(countryMapper::toCountryInfoDto).toList();
  }

  @Override
  public CountryDto findCountryByName(String countryName) {
    Country country = getCountry(countryName);
    return countryMapper.toCountryDto(country);
  }

  @Override
  public List<CountryInfoDto> findAllCountriesByUserId(Long userId) {
    List<UserCountry> userCountries = userCountryRepository.findByUserId(userId);

    return userCountries.stream().map(countryMapper::toCountryInfoDto).toList();
  }

  private Country getCountry(String countryName) {
    return countryRepository
        .findById(countryName)
        .orElseThrow(() -> new CountryNotFoundException(countryName));
  }
}
  /*
  /**
                                 * joins a user to a country
                                 *
                                 * @param userId user id
                                 * @param countryName country entity
                                 *//*
                                         @Override
                                         @Transactional
                                         public void joinUserToCountry(Long userId, String countryName) {
                                             throwExceptionIfAlreadySubscribed(userId, countryName);
                                             Country country = repository.findByName(countryName).orElseThrow(EntityNotFoundException::new);
                                             User user = userRepo.getReferenceById(userId);
                                             participantService.save(new Participant(List.of(country), user));
                                         }

                                         private void throwExceptionIfAlreadySubscribed(Long userId, String countryName) {
                                             Boolean isSubscribed = userIsSubscribed(countryName ,userId);
                                             log.info("isSubscribed {}", isSubscribed);
                                             if (isSubscribed) {
                                                 throw new UserAlreadyJoinTheChatException("User with id : "+ userId +
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
                                     */
