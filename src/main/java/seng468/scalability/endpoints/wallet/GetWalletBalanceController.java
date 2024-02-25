package seng468.scalability.endpoints.wallet;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.models.entity.Wallet;
import seng468.scalability.models.response.Response;
import seng468.scalability.repositories.WalletRepository;

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
