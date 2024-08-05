package online.talkandtravel.model.dto;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
