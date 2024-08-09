package online.talkandtravel.model.dto.avatar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import online.talkandtravel.model.entity.User;
/**
 * Data Transfer Object (DTO) for representing an avatar file.
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the avatar file.</li>
 *   <li>{@code content} - Byte array representing the content of the avatar file.</li>
 * </ul>
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarFileDto {
  private Long id;
  private byte[] content;
}
