package online.talkandtravel.controller.http;

import online.talkandtravel.service.ParticipantService;
import online.talkandtravel.util.constants.ApiPathConstants;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/participants")
@RequiredArgsConstructor
public class ParticipantController{}/* {
    private final ParticipantService participantService;

    @Operation(
            description = "Leave country by user"
    )
    @PutMapping("/{userId}/leave-country/{countryId}")
    public ResponseEntity<String> leaveCountry(@PathVariable Long userId,
                                               @PathVariable Long countryId) {
        String response = participantService.leaveCountry(userId, countryId);
        return ResponseEntity.ok().body(response);
    }
}
*/