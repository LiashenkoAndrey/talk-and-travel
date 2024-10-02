package online.talkandtravel.model.dto.country;

public record CountryInfoWithUnreadMessagesDto(
    String name,
    String flagCode,
    Long unreadMessagesCount
) {
}
