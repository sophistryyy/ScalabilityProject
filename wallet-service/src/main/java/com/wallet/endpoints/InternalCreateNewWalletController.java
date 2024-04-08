package com.wallet.endpoints;

import com.wallet.models.entity.Wallet;
import com.wallet.models.request.NewWalletRequest;
import com.wallet.models.response.Response;
import com.wallet.jpa.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequiredArgsConstructor
public class InternalCreateNewWalletController {
    private final WalletRepository walletRepository;

    @PostMapping("/internal/saveNewWallet")
    public Response saveNewWallet(@RequestBody NewWalletRequest req){
        String username = req.getUsername();
        try {
            walletRepository.saveNewWallet(new Wallet(username));
        }catch (Exception e){
            return Response.error("Couldn't create a new wallet " + e.getMessage());
        }
        return Response.ok(null);
    }

}
