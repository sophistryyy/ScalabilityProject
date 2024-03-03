package seng468.scalability.integration.endpoints.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import seng468.scalability.authentication.JwtUtil;
import seng468.scalability.models.entity.User;
import seng468.scalability.models.entity.Wallet;
import seng468.scalability.repositories.UserRepository;
import seng468.scalability.repositories.WalletRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetWalletBalanceTests {
   
    @Autowired
    private MockMvc mvc;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private JwtUtil jwtUtil;

    private User user;

    private String jwtToken;
    
    @BeforeAll
    void setupBeforeAll() {
        user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");
        userRepository.save(user);

        jwtToken = jwtUtil.generateToken(user.getUsername());
    } 
    
    @BeforeEach
    void setUpBeforeEach() {
        walletRepository.deleteAll();
    }

    @AfterAll
    void breakdownAfterAll() {
        userRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    public void getWalletBalanceTest() throws Exception {
        Wallet userWallet = new Wallet(user.getUsername());
        userWallet.incrementBalance(10000);
        walletRepository.save(userWallet);

        MvcResult res = mvc.perform(get("/getWalletBalance")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.balance").value(10000)) // Check if data is an array
        .andReturn();
    }

    @Test
    public void getWalletBalanceTestWithTwoWallets() throws Exception {
        Wallet userWallet1 = new Wallet(user.getUsername());
        userWallet1.incrementBalance(10000);
        walletRepository.save(userWallet1);

        Wallet userWallet2 = new Wallet("user2");
        userWallet2.incrementBalance(50);
        walletRepository.save(userWallet2);

        MvcResult res = mvc.perform(get("/getWalletBalance")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.balance").value(10000)) // Check if data is an array
        .andReturn();

        Wallet foundUserWallet1 = walletRepository.findByUsername(user.getUsername());
        assertEquals(10000, foundUserWallet1.getBalance());

        Wallet foundUserWallet2 = walletRepository.findByUsername("user2");
        assertEquals(50, foundUserWallet2.getBalance());
    }
}
