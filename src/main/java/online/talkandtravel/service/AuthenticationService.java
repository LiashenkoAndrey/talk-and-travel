package online.talkandtravel.service;

import online.talkandtravel.model.User;
import online.talkandtravel.model.dto.AuthResponse;

import java.io.IOException;

public interface AuthenticationService {
    AuthResponse register(User user) throws IOException;

    AuthResponse login(User user);
}