//package com.wallet.models.entity;
package com.wallet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
//import org.junit.Test;

//AddMoneyToWallet
//saveNewWallet
///createWalletTransaction
//getWalletBalance
//getWalletTransactions

//package seng468.scalability.integration.endpoints.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.floatThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

//import seng468.scalability.authentication.JwtUtil;
//import seng468.scalability.models.entity.User;
//import seng468.scalability.wallet.models.entity;
import com.wallet.models.entity.Wallet;
import com.wallet.jpa.repository.WalletRepository;

//import com.wallet.mongo.repository.WalletRepository;
// import seng468.scalability.repositories.UserRepository;
// import seng468.scalability.repositories.WalletRepository;

//package com.programming.techie.springredditclone.repository;
 
//import com.programming.techie.springredditclone.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
 
import java.time.Instant;
 
import static org.assertj.core.api.Assertions.assertThat;
 
// @DataJpaTest
// @ActiveProfiles("test")
// public class UserRepositoryTestEmbedded {
 
//     @Autowired
//     private UserRepository userRepository;
 
//     @Test
//     public void shouldSaveUser() {
//         User user = new User(null, "test user", "secret password", "user@email.com", Instant.now(), true);
//         User savedUser = userRepository.save(user);
//         assertThat(savedUser).usingRecursiveComparison().ignoringFields("userId").isEqualTo(user);
//     }
 
// }

@DataJpaTest
@ActiveProfiles("test")
//@SpringBootTest
// @AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WalletApplicationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private WalletRepository walletRepository;

    // @Autowired 
    // private UserRepository userRepository;

    // @Autowired 
    // private JwtUtil jwtUtil;

    // private User user;

    private String jwtToken;
    
    @BeforeAll
    void setupBeforeAll() {
        //user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");
        //userRepository.save(user);

        //jwtToken = jwtUtil.generateToken("test-username");
    } 

    @BeforeEach
    void setupBeforeEach() {
        walletRepository.deleteAll();
    }


    @AfterAll
    void breakdownAfterAll() {
        //userRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    public void TestgetWalletBalanceTest() throws Exception {
        Wallet userWallet = new Wallet("test-username");
        userWallet.incrementBalance(10000L);
        walletRepository.save(userWallet);

        // MvcResult res = mvc.perform(get("/getWalletBalance")
        // .contentType(MediaType.APPLICATION_JSON)
        // .header("token", jwtToken)
        // .content(""))
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("$.success").value(true))
        // .andExpect(jsonPath("$.data.balance").value(10000)) // Check if data is an array
        // .andReturn();

        //assertEquals(res, 200);
    }

    @Test
    public void TestgetWalletBalanceWithTwoWalletsTest() throws Exception {
        Wallet userWallet1 = new Wallet("test-username");
        userWallet1.incrementBalance(10000L);
        walletRepository.save(userWallet1);

        Wallet userWallet2 = new Wallet("user2");
        userWallet2.incrementBalance(50L);
        walletRepository.save(userWallet2);

        // MvcResult res = mvc.perform(get("/getWalletBalance")
        // .contentType(MediaType.APPLICATION_JSON)
        // .header("token", jwtToken)
        // .content(""))
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("$.success").value(true))
        // .andExpect(jsonPath("$.data.balance").value(10000)) // Check if data is an array
        // .andReturn();

        // Wallet foundUserWallet1 = walletRepository.findByUsername("test-username");
        // assertEquals(10000, foundUserWallet1.getBalance());

        // Wallet foundUserWallet2 = walletRepository.findByUsername("user2");
        // assertEquals(50, foundUserWallet2.getBalance());
    }

}
