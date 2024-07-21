package com.gmail.smaglenko.talkandtravel.controller.websocket;

import com.gmail.smaglenko.talkandtravel.model.dto.CountryDto;
import com.gmail.smaglenko.talkandtravel.service.CountryService;
import com.gmail.smaglenko.talkandtravel.util.mapper.CountryDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class CountryWebSocketController {

    private final CountryService countryService;
    private final CountryDtoMapper countryDtoMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/countries/find-by-name/{countryName}")
    @SendTo("/countries/{countryName}")
    public ResponseEntity<CountryDto> findByName(@DestinationVariable String countryName) {
        var country = countryService.findByName(countryName);
        var countryDto = countryDtoMapper.mapToDto(country);
        return ResponseEntity.ok().body(countryDto);
    }

    @Operation(
            method = "websocket",
            description = "create a new country"
    )
    @MessageMapping("/country/create")
    @SendTo("/countries/{countryName}")
    public ResponseEntity<CountryDto> create(@Payload CountryDto dto) {
        if (dto.getName() == null) throw new IllegalArgumentException("A country name must be specified");
        log.info("Create a country {}", dto);
        var country = countryDtoMapper.mapToModel(dto);
        var newCountry = countryService.create(country, dto.getUserId());
        var countryDto = countryDtoMapper.mapToDto(newCountry);
        simpMessagingTemplate.convertAndSend("/countries/" + dto.getName(), countryDto);
        return ResponseEntity.ok().body(countryDto);
    }

    @MessageMapping("/countries/update/{countryName}")
    @SendTo("/countries/{countryName}")
    public ResponseEntity<CountryDto> update(@Payload CountryDto dto) {
        var country = countryService.update(dto.getId(), dto.getUserId());
        var countryDto = countryDtoMapper.mapToDto(country);
        return ResponseEntity.ok().body(countryDto);
    }
}
