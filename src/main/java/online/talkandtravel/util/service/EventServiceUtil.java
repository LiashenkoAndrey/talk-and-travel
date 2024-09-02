package online.talkandtravel.util.service;

import java.security.Principal;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class EventServiceUtil {

  public static User getUserFromPrincipal(Principal principal) {
    CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    return customUserDetails.getUser();
  }

}
