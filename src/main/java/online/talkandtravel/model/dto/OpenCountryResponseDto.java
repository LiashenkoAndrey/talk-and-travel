package online.talkandtravel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenCountryResponseDto {
    CountryDto country;
    Boolean isSubscribed;
}
