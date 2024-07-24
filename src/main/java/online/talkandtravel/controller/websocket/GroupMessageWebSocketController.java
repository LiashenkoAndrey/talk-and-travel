package online.talkandtravel.controller.websocket;

import online.talkandtravel.model.dto.GroupMessageRequestDto;
import online.talkandtravel.model.dto.IMessageDto;
import online.talkandtravel.service.GroupMessageService;
import online.talkandtravel.util.mapper.GroupMessageDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
        IMessageDto groupMessage = groupMessageService.saveAndReturnDto(groupMessageRequestDto);
        log.info("groupMessage - {}", groupMessage);
//        GroupMessageDto groupMessageDto = groupMessageDtoMapper.mapToDto(groupMessage);
//        log.info("groupMessageDto - {}",groupMessageDto);
        simpMessagingTemplate.convertAndSend("/countries/" + groupMessageRequestDto.getCountryId() + "/messages", groupMessage);
    }
}
