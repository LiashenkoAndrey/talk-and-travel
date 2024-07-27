package online.talkandtravel.controller.http;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/privacy-policy")
public class PrivacyPolicy {

    @GetMapping
    public String getPrivacyPolicy() {
        return "pp";
    }
}
