package online.talkandtravel.model.entity;

public enum UserOnlineStatus {
  ONLINE(true),
  OFFLINE(false);

  private final Boolean isOnline;

  public Boolean isOnline() {
    return isOnline;
  }

  UserOnlineStatus(Boolean isOnline) {
    this.isOnline = isOnline;
  }
}
