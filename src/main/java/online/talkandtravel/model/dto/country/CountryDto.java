package online.talkandtravel.model.dto.country;

import java.util.List;
import online.talkandtravel.model.dto.chat.ChatDto;

public record CountryDto(String name, String flagCode, List<ChatDto> chats) {}
