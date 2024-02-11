package seng468.scalability.endpoints.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.authentication.User;
import seng468.scalability.authentication.UserService;
import seng468.scalability.endpoints.ResponseSuccess;
import seng468.scalability.endpoints.authentication.utility.RegisterRequest;

@RequestMapping("/auth")
@RestController
public class RegisterController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public ResponseSuccess registerUser(@RequestBody RegisterRequest req) {
        User user = new User(req.getUsername(), req.getPassword(), req.getName());
        userService.saveUser(user);
        return new ResponseSuccess("true", null);
    }
}
