package com.gmail.smaglenko.talkandtravel.controller;

import com.gmail.smaglenko.talkandtravel.model.dto.UserDto;
import com.gmail.smaglenko.talkandtravel.service.AuthenticationService;
import com.gmail.smaglenko.talkandtravel.util.mapper.UserDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;
    private final UserDtoMapper mapper;

    @Operation(description = "Register a user.")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto dto) {
        UserDto user = mapper.mapToDto(
                authService.register(dto.getUserName(), dto.getUserEmail(), dto.getPassword())
        );
        return ResponseEntity.ok(user);
    }

    @Operation(description = "Log in a user.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto dto) {
        UserDto user = mapper.mapToDto(authService.login(dto.getUserEmail(), dto.getPassword()));
        return ResponseEntity.ok(user);
    }
}
