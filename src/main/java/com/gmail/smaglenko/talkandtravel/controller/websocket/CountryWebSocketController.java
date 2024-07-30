package com.gmail.smaglenko.talkandtravel.controller.websocket;

import com.gmail.smaglenko.talkandtravel.model.dto.CountryDto;
import com.gmail.smaglenko.talkandtravel.service.CountryService;
import com.gmail.smaglenko.talkandtravel.util.mapper.CountryDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CountryWebSocketController {

  private final CountryService countryService;
  private final CountryDtoMapper countryDtoMapper;

  @MessageMapping("/countries/find-by-name/{countryName}")
  @SendTo("/countries/{countryName}")
  public ResponseEntity<CountryDto> findByName(@DestinationVariable String countryName) {
    var country = countryService.findByName(countryName);
    var countryDto = countryDtoMapper.mapToDto(country);
    return ResponseEntity.ok().body(countryDto);
  }

  @MessageMapping("/countries/create/{countryName}")
  @SendTo("/countries/{countryName}")
  public ResponseEntity<CountryDto> create(@Payload CountryDto dto) {
    var country = countryDtoMapper.mapToModel(dto);
    var newCountry = countryService.create(country, dto.getUserId());
    var countryDto = countryDtoMapper.mapToDto(newCountry);
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
