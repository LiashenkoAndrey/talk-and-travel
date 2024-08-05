package online.talkandtravel.model.dto.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryInfoDto {
    private String name;
    private String flagCode;
}
