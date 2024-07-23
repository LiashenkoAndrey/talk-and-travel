package online.talkandtravel.model.dto;

import online.talkandtravel.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarDto {
    private Long id;
    private User user;
    private byte[] content;
}
