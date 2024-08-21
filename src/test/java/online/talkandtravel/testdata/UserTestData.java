package online.talkandtravel.testdata;

public final class UserTestData {

  private static final Long ADMIN_ID = 1L;

  private UserTestData() {
    throw new UnsupportedOperationException(
        "Utility UserTestData class cannot be instantiated");
  }

  public static Long getAdminId() {
    return ADMIN_ID;
  }
}
