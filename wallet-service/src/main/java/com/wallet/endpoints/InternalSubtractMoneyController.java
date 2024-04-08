package com.wallet.endpoints;

import com.wallet.jpa.repository.WalletRepository;
import com.wallet.models.entity.Wallet;
import com.wallet.models.request.SubtractMoneyRequest;
import com.wallet.models.response.Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalSubtractMoneyController {

    private final WalletRepository walletRepository;

    @PostMapping("/internal/subtractMoneyFromWallet")
    @Transactional
    public Response decrementWallet(@RequestBody SubtractMoneyRequest req){
        try{
            Wallet wallet = walletRepository.findByUsername(req.username());
            if(wallet == null){
                return Response.error("No user found.");
            }
            wallet.decrementBalance(req.amount());
            //no error, so user has enough
            walletRepository.save(wallet);
            return Response.ok(null);
        }catch(Exception e){
            return Response.error(e.getMessage());
        }
    }
}
