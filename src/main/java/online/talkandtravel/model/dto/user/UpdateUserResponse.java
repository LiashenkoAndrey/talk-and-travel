package online.talkandtravel.model.dto.user;

public record UpdateUserResponse(
    String userName,
    String userEmail,
    String about
) {

}
