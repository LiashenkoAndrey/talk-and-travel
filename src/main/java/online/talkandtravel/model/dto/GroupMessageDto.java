package online.talkandtravel.model.dto;

import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageDto {
    private Long id;
    private String content;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private Country country;
    private User user;
}
