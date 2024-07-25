package online.talkandtravel.controller.websocket;

import jakarta.persistence.EntityNotFoundException;
import online.talkandtravel.model.Country;
import online.talkandtravel.model.dto.*;
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
import org.springframework.web.bind.annotation.*;

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
     *  When user opens country (subscribed, unsubscribed) frontend sends message here
     *  If the user was not subscribed to the country - join user to country
     *  As a response it'll we send a country DTO to user to path /countries/{name}
     * @param dto country dto
     */
    @MessageMapping("/countries/{countryName}/open")
    public void open(@Payload OpenCountryRequestDto dto, @DestinationVariable String countryName) {
        log.info("Open a country {}", dto);
        Country country = countryRepo.findByName(countryName).orElseThrow(EntityNotFoundException::new);
        log.info("found country {}", country);
        Boolean isSubscribed = countryService.userIsSubscribed(countryName ,dto.getUserId());
        log.info("isSubscribed {}", isSubscribed);

        OpenCountryResponseDto responseDto = OpenCountryResponseDto.builder()
                .country(countryDtoMapper.mapToDto(country))
                .isSubscribed(isSubscribed)
                .build();
        log.info("response - {}", responseDto);
        simpMessagingTemplate.convertAndSend("/countries/" + countryName, responseDto);
    }

    /**
     * Joins a user to a country
     */
    @PostMapping("/countries/{countryName}/join")
    public ResponseEntity<?> join(@RequestBody Long userId, @PathVariable String countryName) {
        countryService.joinUserToCountry(userId, countryName);
        return ResponseEntity.ok().build();
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
