package seng468.scalability.endpoints.wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.models.entity.User;
import seng468.scalability.models.entity.Wallet;
import seng468.scalability.models.request.AddMoneyToWalletRequest;
import seng468.scalability.models.response.Response;
import seng468.scalability.repositories.WalletRepository;

@RestController
public class AddMoneyToWalletController {

  @Autowired
  private WalletRepository walletRepository;

  @PostMapping("/addMoneyToWallet")
  public Response addMoneyToWallet(@RequestBody AddMoneyToWalletRequest req) {
     
    try {
      String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
      Wallet wallet = walletRepository.findByUsername(username);

      wallet.incrementBalance(req.getAmount());

      walletRepository.save(wallet);
    
      return Response.ok(null);
    } catch (Exception e) {
      return Response.error(e.getMessage());
    }
  }

}