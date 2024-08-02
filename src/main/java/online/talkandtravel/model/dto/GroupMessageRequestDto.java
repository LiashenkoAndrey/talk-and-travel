package online.talkandtravel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupMessageRequestDto {
    private String content;
    private Long senderId;
    private Long countryId;
}
