package online.talkandtravel.model.entity;

public enum UserOnlineStatus {
  ONLINE(true),
  OFFLINE(false);

  private final Boolean isOnline;

  public Boolean isOnline() {
    return isOnline;
  }

  public static UserOnlineStatus ofStatus(Boolean b) {
    if (b) return UserOnlineStatus.ONLINE;
    return UserOnlineStatus.OFFLINE;
  }

  UserOnlineStatus(Boolean isOnline) {
    this.isOnline = isOnline;
  }
}
