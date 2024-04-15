package com.wallet.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.models.request.InternalDeleteWalletTXRequest;
import com.wallet.models.response.Response;
import com.wallet.mongo.repository.WalletTXRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class InternalDeleteWalletTransaction {
    @Autowired
    WalletTXRepository walletTXRepository; 

    @PostMapping("/internal/deleteWalletTransaction")
    public Response deleteWalletTransaction(@RequestBody InternalDeleteWalletTXRequest req) {
        try {
            walletTXRepository.deleteByWalletTXId(req.walletTXId());
            return Response.ok("Success");
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
