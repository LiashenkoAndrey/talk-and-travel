package online.talkandtravel.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.auth.RegistrationConfirmationRequest;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@Component
public class TestAuthenticationService {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  public static final String AUTHORIZATION_HEADER = "Bearer %s";

  public Principal authenticateUser(User user) {
    CustomUserDetails userDetails = new CustomUserDetails(user);
    var authData = new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authData);
    return authData;
  }

  public void logout(String token) {
    try {
      performPost("/api/authentication/logout", AUTHORIZATION_HEADER.formatted(token), null)
          .andExpect(status().isOk());
    } catch (Exception e) {
      throw new RuntimeException("Logout failed", e);
    }
  }

  public void register(RegisterRequest request) {
    try {
      String requestBody = objectMapper.writeValueAsString(request);
      performPost("/api/authentication/register", null, requestBody)
          .andExpect(status().isAccepted());
    } catch (Exception e) {
      throw new RuntimeException("Registration failed", e);
    }
  }

  public void confirmUserRegistration(String token) {
    try {
      String requestBody = objectMapper.writeValueAsString(new RegistrationConfirmationRequest(token));
      performPost("/api/authentication/registration-confirmation", null, requestBody)
          .andExpect(status().isCreated());
    } catch (Exception e) {
      throw new RuntimeException("confirm User Registration failed", e);
    }
  }

  public String loginAndGetToken(String userEmail, String password) {
    try {
      String jsonContent =
          """
              {
                  "userEmail": "%s",
                  "password": "%s"
              }
              """
              .formatted(userEmail, password);

      // Perform login request and extract token from the response
      MvcResult result =
          mockMvc
              .perform(
                  post("/api/authentication/login")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonContent))
              .andExpect(status().isOk())
              .andReturn();

      // Parse the response body as JSON
      String responseBody = result.getResponse().getContentAsString();
      JsonNode jsonResponse = objectMapper.readTree(responseBody);

      // Extract the token from the JSON response
      String token = jsonResponse.get("token").asText();

      if (token == null || token.isEmpty()) {
        throw new RuntimeException("Failed to retrieve JWT token");
      }

      return token;
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve JWT token", e);
    }
  }

  private ResultActions performPost(String url, String authorizationHeader, String content) {
    try {
      var requestBuilder = post(url)
          .contentType(MediaType.APPLICATION_JSON);

      if (authorizationHeader != null) {
        requestBuilder.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
      }
      if (content != null) {
        requestBuilder.content(content);
      }

      return mockMvc.perform(requestBuilder);
    } catch (Exception e) {
      throw new RuntimeException("Request to " + url + " failed", e);
    }
  }
}
