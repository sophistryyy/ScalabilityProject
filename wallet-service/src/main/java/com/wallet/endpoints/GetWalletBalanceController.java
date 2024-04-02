package com.wallet.endpoints;

import com.wallet.models.entity.Wallet;
import com.wallet.models.response.Response;
import com.wallet.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GetWalletBalanceController {

    private final WalletRepository walletRepository;

    @GetMapping("/getWalletBalance")
    public Response getWalletBalance(@RequestHeader("X-username") String username) {
        try {
            if(username == null || username.isEmpty()){
                return Response.error("Username not found.");
            }
            Wallet wallet = walletRepository.findByUsername(username);
            Map<String, Object> data =  new HashMap<String, Object>();
            data.put("balance", wallet.getBalance());

            return Response.ok(data);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
