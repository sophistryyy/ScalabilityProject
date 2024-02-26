package seng468.scalability.endpoints.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.authentication.UserService;
import seng468.scalability.models.entity.User;
import seng468.scalability.models.entity.Wallet;
import seng468.scalability.models.exceptions.UsernameExistsException;
import seng468.scalability.models.request.RegisterRequest;
import seng468.scalability.models.response.Response;
import seng468.scalability.repositories.WalletRepository;

@RestController
public class RegisterController {
    @Autowired
    private UserService userService;

    @Autowired
    private WalletRepository walletRepository;

    @PostMapping("/register")
    public Response registerUser(@RequestBody RegisterRequest req) {
        try {
            User user = new User(req.getUsername(), req.getPassword(), req.getName());
            userService.saveUser(user);
            walletRepository.save(new Wallet(req.getUsername()));
            return Response.ok(null);
        } catch (UsernameExistsException e) {
            return Response.error(e.getMessage());
        }
    }
}
