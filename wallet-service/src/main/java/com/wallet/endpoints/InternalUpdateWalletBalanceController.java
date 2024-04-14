package com.wallet.endpoints;

import com.wallet.jpa.repository.WalletRepository;
import com.wallet.models.entity.Wallet;
import com.wallet.models.request.UpdateWalletBalanced;
import com.wallet.models.response.Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalUpdateWalletBalanceController {

    private final WalletRepository walletRepository;

    @PostMapping("/internal/updateWalletBalance")
    @Transactional
    public Response updateWalletBalance(@RequestBody UpdateWalletBalanced req){
        try{
            Wallet wallet = walletRepository.findByUsername(req.getUsername());
            if(wallet == null){
                return Response.error("No user found.");
            }
            if (req.getIsDebit() == true) {
                wallet.decrementBalance(req.getAmount());
            } else {
                wallet.incrementBalance(req.getAmount());
            }
            System.out.println("TEST: " + wallet.getBalance());
            //no error, so user has enough
            walletRepository.save(wallet);
            return Response.ok(null);
        }catch(Exception e){
            return Response.error(e.getMessage());
        }
    }
}
