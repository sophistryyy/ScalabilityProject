package seng468.scalability.endpoints.wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.repositories.WalletRepository;

@RestController
public class GetWalletTransactions {

    @Autowired
    private WalletRepository walletRepository;
}
