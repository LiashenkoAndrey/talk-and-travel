package online.talkandtravel.controller.http;

import lombok.RequiredArgsConstructor;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/group-messages")
@RequiredArgsConstructor
public class GroupMessageController {} /*
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

}
*/
