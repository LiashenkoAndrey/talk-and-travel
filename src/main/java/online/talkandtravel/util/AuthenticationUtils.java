package online.talkandtravel.util;

import java.security.Principal;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class AuthenticationUtils {

  public static User getUserFromPrincipal(Principal principal) {
    CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    return customUserDetails.getUser();
  }

}
