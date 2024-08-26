package online.talkandtravel.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Component
public class TestAuthenticationService {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  public String loginAndGetToken(String userEmail, String password) throws Exception {
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
  }
}
