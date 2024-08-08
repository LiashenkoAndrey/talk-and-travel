package online.talkandtravel.controller.http;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
