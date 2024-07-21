package com.gmail.smaglenko.talkandtravel.controller.websocket;

import com.gmail.smaglenko.talkandtravel.model.dto.GroupMessageDto;
import com.gmail.smaglenko.talkandtravel.model.dto.GroupMessageRequestDto;
import com.gmail.smaglenko.talkandtravel.service.GroupMessageService;
import com.gmail.smaglenko.talkandtravel.util.mapper.GroupMessageDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class GroupMessageWebSocketController {
    private final GroupMessageService groupMessageService;
    private final GroupMessageDtoMapper groupMessageDtoMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/group-messages")
    public void create(@Payload GroupMessageRequestDto groupMessageRequestDto) {
        log.info("create a new message {}", groupMessageRequestDto);
        var groupMessage = groupMessageService.create(groupMessageRequestDto);
        var groupMessageDto = groupMessageDtoMapper.mapToDto(groupMessage);
        simpMessagingTemplate.convertAndSend("/countries/" + groupMessageRequestDto.getCountryId() + "/messages", groupMessageDto);
    }
}
