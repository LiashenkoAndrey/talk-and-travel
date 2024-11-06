package online.talkandtravel.model.dto.avatar;


public record AvatarDto (
    String image50x50,
    String image256x256
) {

  public AvatarDto() {
    this(null, null);
  }
}
