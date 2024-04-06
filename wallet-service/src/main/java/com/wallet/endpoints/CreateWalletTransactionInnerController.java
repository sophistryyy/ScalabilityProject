package com.wallet.endpoints;

import com.wallet.models.entity.WalletTX;
import com.wallet.models.request.NewWalletTransactionRequest;
import com.wallet.models.response.Response;
import com.wallet.mongo.repository.WalletTXRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateWalletTransactionInnerController {

    private final WalletTXRepository walletTXRepository;

    @PostMapping( "/createWalletTransaction")
    public Response createWalletTransaction(@RequestBody NewWalletTransactionRequest req){
        try{
            WalletTX walletTx = new WalletTX(req.getUsername(), req.getStockTXId(), req.isDebit(), req.getAmount());
            walletTXRepository.save(walletTx);
        }catch(Exception e){
            return Response.error(e.getMessage());
        }
        return Response.ok(null);
    }
}
