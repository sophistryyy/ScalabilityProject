package com.user.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import authentication.UserService;
import com.user.models.entity.User;
//import com.user.models.entity.Wallet;
import com.user.models.request.RegisterRequest;
import com.user.models.response.Response;
//import com.user.repositories.WalletRepository;

@RestController
public class RegisterController {
    @Autowired
    private UserService userService;

    //@Autowired
    //private WalletRepository walletRepository;

    @PostMapping("/register")
    public Response registerUser(@RequestBody RegisterRequest req) {
        try {
            User user = new User(req.getUsername(), req.getPassword(), req.getName());
            userService.saveUser(user);
            //walletRepository.saveNewWallet(new Wallet(req.getUsername()));
            return Response.ok(null);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}