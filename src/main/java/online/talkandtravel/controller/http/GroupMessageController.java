package online.talkandtravel.controller.http;

import online.talkandtravel.model.dto.GroupMessageDto;
import online.talkandtravel.model.dto.GroupMessageRequestDto;
import online.talkandtravel.model.dto.IMessageDto;
import online.talkandtravel.service.GroupMessageService;
import online.talkandtravel.util.constants.ApiPathConstants;
import online.talkandtravel.util.mapper.GroupMessageDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/group-messages")
@RequiredArgsConstructor
public class GroupMessageController {}/*
    private final GroupMessageService groupMessageService;
    private final GroupMessageDtoMapper groupMessageDtoMapper;

    @Operation(
            description = "This method finds all group messages within one country. "
                    + "The messages that were added last are displayed first."
    )
    @GetMapping("/{countryId}")
    public ResponseEntity<List<GroupMessageDto>> findByCountryIdOrderByCreationDateDesc(
            @PathVariable Long countryId) {
        List<GroupMessage> countryGroupMessagesByIdOrderByCreationDateDesc
                = groupMessageService.findByCountryIdOrderByCreationDateDesc(countryId);
        List<GroupMessageDto> groupGroupMessageDtos
                = countryGroupMessagesByIdOrderByCreationDateDesc.stream()
                .map(groupMessageDtoMapper::mapToDto)
                .toList();
        return ResponseEntity.ok().body(groupGroupMessageDtos);
    }

    @Operation(
            description = "Create GroupMessage"

    )
    @PostMapping
    public ResponseEntity<IMessageDto> create(@RequestBody GroupMessageRequestDto groupMessageRequestDto) {
        IMessageDto groupMessage = groupMessageService.saveAndReturnDto(groupMessageRequestDto);
        return ResponseEntity.ok().body(groupMessage);
    }
}
*/