package online.talkandtravel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIsTypingDTO {

  public UserIsTypingDTO(String userName, Boolean isTexting) {
    this.userName = userName;
    this.isTexting = isTexting;
  }

  Long chatId;
  Long userId;
  String userName;
  Boolean isTexting;
}
