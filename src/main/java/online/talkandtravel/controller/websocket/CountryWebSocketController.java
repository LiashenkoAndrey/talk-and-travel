package online.talkandtravel.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.CountryDtoWithParticipantsAmountAndMessages;
import online.talkandtravel.model.dto.NewParticipantCountryDto;
import online.talkandtravel.model.dto.OpenCountryRequestDto;
import online.talkandtravel.model.dto.OpenCountryResponseDto;
import online.talkandtravel.service.CountryService;
import online.talkandtravel.util.mapper.CountryDtoMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller class for serving  a chat via Websocket endpoints. Handles operations such as
 * retrieving, creating and updating chat.
 *
 * <p>Publish endpoints:
 * <ul>
 *   <li>WS /chat/countries/open: Retrieves a pageable list of all advertisements.
 *   <li>WS /chat/countries/update/{countryName}: Retrieves an advertisement by its unique identifier.
 * </ul>
 *
 * <p>Subscribe endpoints:
 * <ul>
 *   <li>WS /chat/countries/{countryName}: Retrieves a pageable list of all advertisements.
 * </ul>
 */
@RestController
@RequiredArgsConstructor
@Log4j2
public class CountryWebSocketController {
    private final CountryService countryService;
    private final CountryDtoMapper countryDtoMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * When user opens country (subscribed, unsubscribed) frontend sends message here
     * If the user was not subscribed to the country - join user to country
     * As a response it'll we send a country DTO to user to path /countries/{name}
     *
     * @param dto country dto
     */
    @MessageMapping("/countries/open")
    public void open(@RequestBody OpenCountryRequestDto dto) {
        log.info("Open a country {}", dto);
        CountryDtoWithParticipantsAmountAndMessages countryDto = countryService.findByNameAndCreateIfNotExist(dto.getCountryName(), dto );
        log.info("found country {}", countryDto);
        Boolean isSubscribed = countryService.userIsSubscribed(dto.getCountryName(), dto.getUserId());
        log.info("isSubscribed {}", isSubscribed);

        OpenCountryResponseDto responseDto = OpenCountryResponseDto.builder()
                .country(countryDto)
                .isSubscribed(isSubscribed)
                .build();
        log.info("response - {}", responseDto);
        sendCountryToSpecificUser(dto.getUserId(), dto.getCountryName(), responseDto);
    }


    private void sendCountryToSpecificUser(Long userId, String countryName, OpenCountryResponseDto responseDto) {
        simpMessagingTemplate.convertAndSend("/"+ userId +"/countries/" + countryName, responseDto);
    }

    /**
     * Updates country notifies all users that subscribed to path /countries/{countryName} that it was updated
     * @param dto country dto
     */
    //todo: rename path
    @MessageMapping("/countries/update/{countryName}")
    public void addNewParticipantToCountry(@Payload NewParticipantCountryDto dto, @DestinationVariable String countryName) {
        log.info("Update a country {} - {}", countryName, dto);
        countryService.addNewParticipantToCountry(dto);
    }
}
