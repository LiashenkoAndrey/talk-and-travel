package online.talkandtravel.model.entity;

public enum UserOnlineStatus {
  ONLINE(true),
  OFFLINE(false);

  private final Boolean isOnline;

  public String isOnline() {
    return isOnline.toString();
  }

  UserOnlineStatus(Boolean isOnline) {
    this.isOnline = isOnline;
  }
}
