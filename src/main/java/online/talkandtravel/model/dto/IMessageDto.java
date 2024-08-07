package online.talkandtravel.model.dto;

import java.time.LocalDateTime;
import online.talkandtravel.model.dto.user.IUserDto;

public interface IMessageDto {
    Long getId();
    String getContent();
    LocalDateTime getCreationDate();
    IUserDto getUser();
}
