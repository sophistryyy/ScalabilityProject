package com.wallet.endpoints;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import com.wallet.models.entity.Wallet;
import com.wallet.models.request.AddMoneyToWalletRequest;
import com.wallet.models.response.Response;
import com.wallet.repositories.WalletRepository;

import java.net.http.HttpHeaders;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AddMoneyToWalletController {

  private final WalletRepository walletRepository;

  @PostMapping("/addMoneyToWallet")
  public Response addMoneyToWallet(@RequestBody AddMoneyToWalletRequest req, @RequestHeader("X-username") String username) {
    try {

      if(username == null || username.isEmpty()){
        return Response.error("Username not found.");
      }
      Wallet wallet = walletRepository.findByUsername(username);
      wallet.incrementBalance(req.getAmount());
      walletRepository.save(wallet);

      return Response.ok(null);
    } catch (Exception e) {
      return Response.error(e.getMessage());
    }
  }

}