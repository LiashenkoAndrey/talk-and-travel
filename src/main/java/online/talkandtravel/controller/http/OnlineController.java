package online.talkandtravel.controller.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.service.OnlineService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/v2")
public class OnlineController {

    private final OnlineService onlineService;

    /**
     * Provides an initial snapshot of all user's online statuses
     *
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    @GetMapping("/users/online")
    public Map<Long, Boolean> getAllUsersOnlineStatuses(@RequestParam(required = false) List<Long> usersIdList) {
        return (usersIdList != null && !usersIdList.isEmpty())
                ? onlineService.getAllUsersOnlineStatusesForUsersList(usersIdList)
                : onlineService.getAllUsersOnlineStatuses();
    }

    /**
     * Provides a current online status of a specific user
     *
     * @param userId user id
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    @GetMapping("/users/{userId}/online")
    public Boolean getUserOnlineStatusById(@PathVariable Long userId) {
        return onlineService.getUserOnlineStatusById(userId);
    }
}
