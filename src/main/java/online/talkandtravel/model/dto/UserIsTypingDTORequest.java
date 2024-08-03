package online.talkandtravel.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIsTypingDTORequest {

  @NotNull
  @Size(max = 100, message = "Max userName length is 100 symbols!")
  String userName;

  @NotNull
  Boolean userIsTexting;
}
