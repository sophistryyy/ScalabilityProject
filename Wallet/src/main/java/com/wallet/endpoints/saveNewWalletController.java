package com.wallet.endpoints;

import com.wallet.models.entity.Wallet;
import com.wallet.models.request.NewWalletRequest;
import com.wallet.models.response.Response;
import com.wallet.repositories.WalletRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "saveNewWallet")
public class saveNewWalletController {
    private final WalletRepository walletRepository;

    public saveNewWalletController(WalletRepository walletRepository){
        this.walletRepository = walletRepository;
    }

    @PostMapping
    public Response saveNewWallet(@RequestBody NewWalletRequest req){
        String username = req.getUsername();
        try {
            walletRepository.saveNewWallet(new Wallet(username));
        }catch (Exception e){
            return Response.error(e.getMessage());
        }
        return Response.ok(null);
    }

}
