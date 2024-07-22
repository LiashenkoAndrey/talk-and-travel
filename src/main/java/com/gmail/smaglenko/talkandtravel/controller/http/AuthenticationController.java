package com.gmail.smaglenko.talkandtravel.controller.http;

import com.gmail.smaglenko.talkandtravel.model.dto.AuthResponse;
import com.gmail.smaglenko.talkandtravel.model.dto.UserDto;
import com.gmail.smaglenko.talkandtravel.service.AuthenticationService;
import com.gmail.smaglenko.talkandtravel.util.constants.ApiPathConstants;
import com.gmail.smaglenko.talkandtravel.util.mapper.UserDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/authentication")
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {
    private final AuthenticationService authService;
    private final UserDtoMapper mapper;

    @Operation(
            description = "Register a user."
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserDto dto) throws IOException {
        log.info("register - {}", dto);
        var user = mapper.mapToModel(dto);
        var authResponse = authService.register(user);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(
            description = "Log in a user."
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserDto dto) {
        log.info("Login - {}", dto);
        var user = mapper.mapToModel(dto);
        var authResponse = authService.login(user);
        return ResponseEntity.ok(authResponse);
    }
}
