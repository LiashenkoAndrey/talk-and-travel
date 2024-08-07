package online.talkandtravel.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.talkandtravel.model.dto.user.UserDtoShort;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IParticipantDto {
    private Long id;
    private UserDtoShort user;


}
