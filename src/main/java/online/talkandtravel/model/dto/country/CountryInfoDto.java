package online.talkandtravel.model.dto.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Data Transfer Object (DTO) for representing basic country information.
 *
 * <ul>
 *   <li>{@code name} - Name of the country.</li>
 *   <li>{@code flagCode} - Code representing the flag of the country.</li>
 * </ul>
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryInfoDto {
    private String name;
    private String flagCode;
}
