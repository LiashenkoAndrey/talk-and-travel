package online.talkandtravel.model.dto;

import online.talkandtravel.model.dto.avatar.AvatarFileDto;
import online.talkandtravel.model.entity.Avatar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoWithAvatarAndPassword {
    private Long id;
    private String userName;
    private String userEmail;
    private String password;
    private AvatarFileDto avatar;
    private String about;
}
