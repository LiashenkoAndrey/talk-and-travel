package online.talkandtravel.model.dto.avatar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import online.talkandtravel.model.entity.User;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarFileDto {
  private Long id;
  private byte[] content;
}
