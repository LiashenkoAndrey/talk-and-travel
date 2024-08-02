package online.talkandtravel.model.dto;

import java.time.LocalDateTime;

public interface IMessageDto {
    Long getId();
    String getContent();
    LocalDateTime getCreationDate();
    IUserDto getUser();
}
