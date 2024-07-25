package online.talkandtravel.controller.websocket;

import jakarta.persistence.EntityNotFoundException;
import online.talkandtravel.model.Country;
import online.talkandtravel.model.dto.CountryDto;
import online.talkandtravel.model.dto.ICountryDto;
import online.talkandtravel.model.dto.NewParticipantCountryDto;
import online.talkandtravel.repository.CountryRepo;
import online.talkandtravel.service.CountryService;
import online.talkandtravel.util.mapper.CountryDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
@RequiredArgsConstructor
@Log4j2
public class CountryWebSocketController {
    private final CountryRepo countryRepo;
    private final CountryService countryService;
    private final CountryDtoMapper countryDtoMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    //todo: discuss a proper solution
    /**
     * finds a specified country and sends response to requested user
     * @param countryName name of country
     * @return CountryDto
     */
    @MessageMapping("/countries/find-by-name/{countryName}")
    @SendTo("/countries/{countryName}")
    public ResponseEntity<CountryDto> findByName(@DestinationVariable String countryName) {
        var country = countryService.findByName(countryName);
        var countryDto = countryDtoMapper.mapToDto(country);
        return ResponseEntity.ok().body(countryDto);
    }

    /**
     * creates a new country and notifies all users that subscribed to path /countries/{countryName}
     * @param dto country dto
     */
    @MessageMapping("/countries/create")
    public void create(@Payload CountryDto dto) {
        if (dto.getName() == null) throw new IllegalArgumentException("A country name must be specified");
        log.info("Create a country {}", dto);
        Country country = countryDtoMapper.mapToModel(dto);
        country.setParticipants(new HashSet<>());
        Country saved = countryService.createAndSave(country);
        log.info("saved country partisipants - {}", saved.getParticipants());
        log.info("saved country  - {}", saved);
        countryService.joinUserToCountry(dto.getUserId(), saved);
        ICountryDto sendDto = countryRepo.findDtoById(saved.getId()).orElseThrow(EntityNotFoundException::new);
        log.info("send country dto... data - {}", sendDto);
        notifyThatCountryWasCreatedOrUpdated(sendDto);
    }

    /**
     * updates country notifies all users that subscribed to path /countries/{countryName} that it was updated
     * @param dto country dto
     */
    //todo: rename path
    @MessageMapping("/countries/update/{countryName}")
    public void addNewParticipantToCountry(@Payload NewParticipantCountryDto dto, @DestinationVariable String countryName) {
        log.info("Update a country {} - {}", countryName, dto);
        countryService.addNewParticipantToCountry(dto);
//        var country = countryService.update(dto.getId(), dto.getUserId());
//        var countryDto = countryDtoMapper.mapToDto(country);
//        notifyThatCountryWasCreatedOrUpdated(countryDto);
    }

    private void notifyThatCountryWasCreatedOrUpdated(ICountryDto countryDto) {
        simpMessagingTemplate.convertAndSend("/countries/" + countryDto.getName(), countryDto);
    }
}
