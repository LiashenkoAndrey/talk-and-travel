package online.talkandtravel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDtoWithParticipantsAmountAndMessages {

    public CountryDtoWithParticipantsAmountAndMessages(Long id, String name, String flagCode) {
        this.id = id;
        this.name = name;
        this.flagCode = flagCode;
    }

    private Long id;
    private String name;
    private String flagCode;
    private List<MessageDto> groupMessages;
    private Long participantsAmount;
}
