package com.gmail.smaglenko.talkandtravel.controller.websocket;

import com.gmail.smaglenko.talkandtravel.model.dto.GroupMessageDto;
import com.gmail.smaglenko.talkandtravel.model.dto.GroupMessageRequestDto;
import com.gmail.smaglenko.talkandtravel.service.GroupMessageService;
import com.gmail.smaglenko.talkandtravel.util.mapper.GroupMessageDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupMessageWebSocketController {

    private final GroupMessageService groupMessageService;
    private final GroupMessageDtoMapper groupMessageDtoMapper;

    @MessageMapping("/group-messages/{country-name}")
    @SendTo("/countries/{countryName}")
    public ResponseEntity<GroupMessageDto> create(@Payload GroupMessageRequestDto groupMessageRequestDto) {
        var groupMessage = groupMessageService.create(groupMessageRequestDto);
        var groupMessageDto = groupMessageDtoMapper.mapToDto(groupMessage);
        return ResponseEntity.ok().body(groupMessageDto);
    }
}
