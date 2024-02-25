package seng468.scalability.endpoints.frontend_stuff;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;

@Controller
public class FrontendController {

    @GetMapping("/login-ui")
    public String loginUI() {
        // Redirects to the static login.html page inside /static/frontend/
        return "src/main/resources/static/frontend/login.html";
    }

    // Add more mappings here as needed for other pages
}