package online.talkandtravel.model.dto.user;

import online.talkandtravel.model.dto.avatar.AvatarFileDto;
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
