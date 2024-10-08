package online.talkandtravel.controller.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.service.OnlineService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static online.talkandtravel.util.constants.ApiPathConstants.API_V2_BASE_PATH;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping(API_V2_BASE_PATH)
public class OnlineController {

    private final OnlineService onlineService;

    /**
     * Provides an initial snapshot of all user's online statuses
     *
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    @GetMapping("/users/online")
    public Map<Long, OnlineStatusDto> getAllUsersOnlineStatuses(@RequestParam(required = false) List<Long> usersIdList) {
        return onlineService.getAllUsersOnlineStatuses(usersIdList);
    }

    /**
     * Provides a current online status of a specific user
     *
     * @param userId user id
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    @GetMapping("/users/{userId}/online")
    public OnlineStatusDto getUserOnlineStatusById(@PathVariable Long userId) {
        return onlineService.getUserOnlineStatusById(userId);
    }
}
