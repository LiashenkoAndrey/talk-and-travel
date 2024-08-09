package online.talkandtravel.controller.http;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class responsible for serving privacy policy and terms of service pages.
 *
 * <ul>
 *   <li>{@code getPrivacyPolicy} - Returns the view name for the privacy policy page.</li>
 *   <li>{@code getPublicTerms} - Returns the view name for the public terms of service page.</li>
 * </ul>
 */

@Controller
@RequestMapping("/")
public class PrivacyPolicyAndUseTerms {

    @GetMapping("/privacy-policy")
    public String getPrivacyPolicy() {
        return "pp";
    }

    @GetMapping("/public-terms-of-service")
    public String getPublicTerms() {
        return "use-terms";
    }
}
