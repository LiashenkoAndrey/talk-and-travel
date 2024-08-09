package online.talkandtravel.model.dto.country;

import java.util.List;
import online.talkandtravel.model.dto.chat.ChatDto;
/**
 * Data Transfer Object (DTO) for representing a country.
 *
 * <ul>
 *   <li>{@code name} - Name of the country.</li>
 *   <li>{@code flagCode} - Code representing the flag of the country.</li>
 *   <li>{@code chats} - List of chats associated with the country.</li>
 * </ul>
 */

public record CountryDto(String name, String flagCode, List<ChatDto> chats) {}
