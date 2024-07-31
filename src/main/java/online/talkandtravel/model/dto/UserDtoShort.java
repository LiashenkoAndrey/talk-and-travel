package online.talkandtravel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoShort {

//    public UserDtoShort(Long id, String userName, String userEmail) {
//        this.id = id;
//        this.userName = userName;
//        this.userEmail = userEmail;
//    }

    private Long id;
    private String userName;
    private String userEmail;
}
