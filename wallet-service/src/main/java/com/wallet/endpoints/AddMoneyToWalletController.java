package com.wallet.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wallet.models.entity.Wallet;
import com.wallet.models.request.AddMoneyToWalletRequest;
import com.wallet.models.response.Response;
import com.wallet.repositories.WalletRepository;

@RestController
@RequiredArgsConstructor
public class AddMoneyToWalletController {

  private final WalletRepository walletRepository;

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