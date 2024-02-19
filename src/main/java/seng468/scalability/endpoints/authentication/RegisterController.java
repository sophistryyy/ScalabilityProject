package seng468.scalability.endpoints.authentication;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.authentication.UserService;
import seng468.scalability.models.Entity.User;
import seng468.scalability.models.Exceptions.UsernameExistsException;
import seng468.scalability.models.Request.RegisterRequest;

@RestController
public class RegisterController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, Object> registerUser(@RequestBody RegisterRequest req) {
        User user = new User(req.getUsername(), req.getPassword(), req.getName());

        Map<String, Object> response = new LinkedHashMap<String, Object>();
        try {
            userService.saveUser(user);
            response.put("success", true);
            response.put("data", null);
        } catch (UsernameExistsException e) {
            response.put("success", false);
            response.put("data", null);
            response.put("message", "Username Already Exists");
        }
        
        return response;
    }
}
