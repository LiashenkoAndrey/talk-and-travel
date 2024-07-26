package online.talkandtravel.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenCountryRequestDto {
    private Long userId;
    private String countryName;
    private String flagCode;
}
