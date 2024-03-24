package com.wallet.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wallet.models.entity.Wallet;
import com.wallet.models.response.Response;
import com.wallet.repositories.WalletRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GetWalletBalanceController {

    @Autowired
    private WalletRepository walletRepository;

    @GetMapping("/getWalletBalance")
    public Response getWalletBalance() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

            Wallet wallet = walletRepository.findByUsername(username);
            Map<String, Object> data =  new HashMap<String, Object>();
            data.put("balance", wallet.getBalance());

            return Response.ok(data);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
