package seng468.scalability.integration.endpoints.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.floatThat;
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
public class AddMoneyToWalletTests {
    
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
    public void testAddMoneyToWallet() throws Exception {
        Wallet userWallet = new Wallet(user.getUsername());
        walletRepository.save(userWallet);

        String requestBody = "{\"amount\": 10000}";
        MvcResult res = mvc.perform(post("/addMoneyToWallet")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").doesNotExist()) // Check if data is an array
        .andReturn();

        Wallet foundWallet = walletRepository.findByUsername(user.getUsername());
        assertEquals(10000, foundWallet.getBalance());
    }

    @Test
    public void testAddMoneyToWalletAddToExistingBalance() throws Exception {
        Wallet userWallet = new Wallet(user.getUsername());
        userWallet.incrementBalance(50);
        walletRepository.save(userWallet);

        String requestBody = "{\"amount\": 10000}";
        MvcResult res = mvc.perform(post("/addMoneyToWallet")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").doesNotExist()) // Check if data is an array
        .andReturn();

        Wallet foundWallet = walletRepository.findByUsername(user.getUsername());
        assertEquals(10050, foundWallet.getBalance());
    }

    @Test
    public void testAddMoneyToWalletInvalidAmount() throws Exception {
        Wallet userWallet = new Wallet(user.getUsername());
        walletRepository.save(userWallet);

        String requestBody = "{\"amount\": -10000}";
        MvcResult res = mvc.perform(post("/addMoneyToWallet")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data.Error").value("Invalid Amount")) // Check if data is an array
        .andReturn();

        Wallet foundWallet = walletRepository.findByUsername(user.getUsername());
        assertEquals(0, foundWallet.getBalance());
    }

    @Test
    public void testAddMoneyToWalletInvalidJWT() throws Exception {
        String invalidJWT = "12345";

        Wallet userWallet = new Wallet(user.getUsername());
        walletRepository.save(userWallet);

        String requestBody = "{\"amount\": 10000}";
        MvcResult res = mvc.perform(post("/addMoneyToWallet")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", invalidJWT)
        .content(requestBody))
        .andExpect(status().isUnauthorized())
        .andReturn();

        Wallet foundWallet = walletRepository.findByUsername(user.getUsername());
        assertEquals(0, foundWallet.getBalance());
    }
}
