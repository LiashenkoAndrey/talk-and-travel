package online.talkandtravel.controller.websocket;

import jakarta.persistence.EntityNotFoundException;
import online.talkandtravel.model.Country;
import online.talkandtravel.model.dto.GroupMessageRequestDto;
import online.talkandtravel.model.dto.IMessageDto;
import online.talkandtravel.repository.CountryRepo;
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
public class GroupMessageWebSocketController{}/* {
    private final GroupMessageService groupMessageService;
    private final GroupMessageDtoMapper groupMessageDtoMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final CountryRepo countryRepo;

    @MessageMapping("/group-messages")
    public void create(@Payload GroupMessageRequestDto groupMessageRequestDto) {
        log.info("create a new message {}", groupMessageRequestDto);
        Country country = countryRepo.findById(groupMessageRequestDto.getCountryId())
                .orElseThrow(EntityNotFoundException::new);

        IMessageDto groupMessage = groupMessageService.saveAndReturnDto(groupMessageRequestDto);
        log.info("groupMessage - {}", groupMessage);
//        GroupMessageDto groupMessageDto = groupMessageDtoMapper.mapToDto(groupMessage);
//        log.info("groupMessageDto - {}",groupMessageDto);
        simpMessagingTemplate.convertAndSend("/countries/" + country.getName() + "/messages", groupMessage);
    }
}
*/