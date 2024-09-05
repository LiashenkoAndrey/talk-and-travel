package online.talkandtravel.service.event;

import online.talkandtravel.model.entity.UserOnlineStatus;

public interface UserEventService extends EventService {

  void updateUserOnlineStatus(UserOnlineStatus userOnlineStatus, Long userId);

}
