package online.talkandtravel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.talkandtravel.model.dto.country.CountryDtoWithParticipantsAmountAndMessages;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenCountryResponseDto {
    CountryDtoWithParticipantsAmountAndMessages country;
    Boolean isSubscribed;
}
