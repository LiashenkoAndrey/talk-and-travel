package online.talkandtravel.model.dto.country;

import java.util.List;
import online.talkandtravel.model.dto.chat.ChatInfoDto;

/**
 * Data Transfer Object (DTO) for representing a country.
 *
 * <ul>
 *   <li>{@code name} - Name of the country.
 *   <li>{@code flagCode} - Code representing the flag of the country.
 *   <li>{@code chats} - List of chats associated with the country.
 * </ul>
 */
public record CountryDto(String name, String flagCode, List<ChatInfoDto> chats) {}
