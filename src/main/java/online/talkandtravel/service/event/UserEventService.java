package online.talkandtravel.service.event;

import online.talkandtravel.model.entity.UserOnlineStatus;

public interface UserEventService {

  void updateUserOnlineStatus(UserOnlineStatus userOnlineStatus, Long userId);

  void publishEvent(UserOnlineStatus isOnline, Long userId);
}
