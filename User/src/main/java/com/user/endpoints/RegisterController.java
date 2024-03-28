package com.user.endpoints;

import com.user.models.request.NewWalletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import authentication.UserService;
import com.user.models.entity.User;
//import com.user.models.entity.Wallet;
import com.user.models.request.RegisterRequest;
import com.user.models.response.Response;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
//import com.user.repositories.WalletRepository;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RegisterController {
    private final UserService userService;

    private final WebClient.Builder webClientBuilder;

    @PostMapping(value = "/register", consumes = {"application/json"})
    public Response registerUser(@RequestBody RegisterRequest req) {
        try {
            User user = new User(req.getUser_name(), req.getPassword(), req.getName());
            userService.saveUser(user);
            NewWalletRequest newWalletRequest = new NewWalletRequest(req.getUser_name());

            //post request to save new wallet
            Mono<Response> walletResponseMono = webClientBuilder.build().post().uri("http://Wallet/saveNewWallet")
                    .bodyValue(new NewWalletRequest(req.getUser_name())).retrieve().bodyToMono(Response.class);
            walletResponseMono.subscribe(walletResponse -> {
                if (walletResponse == null || !walletResponse.success()) {
                    // Retry logic or handle the error
                    log.info("Error creating a new wallet (1). " + walletResponse.data());
                    System.out.println(walletResponse.data());
                }else{
                    log.info("New wallet is created succesfully for " + req.getUser_name());
                }
            }, error ->  log.info("Error creating a new wallet (2). " + error));

            log.info("User {} created", req.getUser_name());
            return Response.ok(null);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}