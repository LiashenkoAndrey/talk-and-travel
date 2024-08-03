package online.talkandtravel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIsTypingDTOResponse {
  Long chatId;
  Long userId;
  String userName;
  Boolean userIsTexting;
}
