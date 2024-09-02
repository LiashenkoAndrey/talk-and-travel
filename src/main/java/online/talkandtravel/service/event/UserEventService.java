package online.talkandtravel.service.event;

import online.talkandtravel.model.enums.UserOnlineStatus;

public interface UserEventService extends EventService {

  void updateUserOnlineStatus(UserOnlineStatus userOnlineStatus, Long userId);

}
