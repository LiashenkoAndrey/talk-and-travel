package online.talkandtravel.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoBasic {

    private Long id;
    private String userName;
    private String userEmail;
    private String about;
}
