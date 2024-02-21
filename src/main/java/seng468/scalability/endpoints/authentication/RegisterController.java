package seng468.scalability.endpoints.authentication;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.authentication.UserService;
import seng468.scalability.models.Response;
import seng468.scalability.models.entity.User;
import seng468.scalability.models.exceptions.UsernameExistsException;
import seng468.scalability.models.request.RegisterRequest;

@RestController
public class RegisterController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Response registerUser(@RequestBody RegisterRequest req) {
        User user = new User(req.getUsername(), req.getPassword(), req.getName());

        try {
            userService.saveUser(user);
            return Response.ok(null);
        } catch (UsernameExistsException e) {
            return Response.error(e.getMessage());
        }
    }
}
