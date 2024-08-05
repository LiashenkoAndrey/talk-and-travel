package online.talkandtravel.model.dto.country;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.talkandtravel.model.dto.UserDtoWithAvatarAndPassword;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryWithUserDto {
    private Long id;
    private String name;
    private String flagCode;
//    private List<GroupMessage> groupMessages;
    private Set<UserDtoWithAvatarAndPassword> participants;
}
