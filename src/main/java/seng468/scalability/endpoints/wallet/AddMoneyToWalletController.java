package seng468.scalability.endpoints.wallet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import seng468.scalability.authentication.User;
import seng468.scalability.wallet.*;


@Controller
public class AddMoneyToWalletController {

  @GetMapping(path="/addMoneyToWallet")
  public @ResponseBody String addMoneyToWallet () {

    // Dummy controller
    // TODO get user from session and 'amount' from request body. Save objects to DB.
    User u = new User("bob86", "4321", "bob");
    Wallet w = new Wallet(u.getUsername(), 777);
    return Integer.toString(w.getBalance());
  }

}