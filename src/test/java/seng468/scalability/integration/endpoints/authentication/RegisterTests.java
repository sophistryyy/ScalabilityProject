package seng468.scalability.integration.endpoints.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import seng468.scalability.models.entity.User;
import seng468.scalability.models.entity.Wallet;
import seng468.scalability.repositories.UserRepository;
import seng468.scalability.repositories.WalletRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterTests {

    @Autowired
    private MockMvc mvc;

    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    } 

    @Test 
    public void testRegister() throws Exception {
        String requestBody = "{\"username\": \"VanguardETF\",\"password\": \"Vang@123\",\"name\": \"Vanguard Corp.\"}";
        MvcResult res = mvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andReturn();

        User foundUser = userRepository.findByUsername("VanguardETF");
        assertNotNull(foundUser);

        Wallet userWallet = walletRepository.findByUsername("VanguardETF");
        assertNotNull(userWallet);
    }

    @Test 
    public void testRegisterWithExistingUser() throws Exception {
        User user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");
        userRepository.save(user);
        
        String requestBody = "{\"username\": \"VanguardETF\",\"password\": \"Comp@124\",\"name\": \"Vanguard Ltd.\"}";
        MvcResult res = mvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data.Error").value("Username Already Exists"))
        .andReturn();

        User foundUser = userRepository.findByUsername("VanguardETF");
        assertNotNull(foundUser);
        assertEquals("Vanguard Corp.", foundUser.getName());

        Wallet userWallet = walletRepository.findByUsername("VanguardETF");
        assertNotNull(userWallet);
    }

    @Test
    public void testRegisterWithTwoUsers() throws Exception {
        User user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");
        userRepository.save(user);

        String requestBody = "{\"username\": \"FinanceGuru\",\"password\": \"Fguru@2024\",\"name\": \"The Finance Guru\"}";
        MvcResult res = mvc.perform(post("/register")
        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andReturn();

        User foundUser1 = userRepository.findByUsername("VanguardETF");
        assertNotNull(foundUser1);
        assertEquals("Vanguard Corp.", foundUser1.getName());
        
        User foundUser2 = userRepository.findByUsername("FinanceGuru");
        assertEquals("The Finance Guru", foundUser2.getName());
        assertNotNull(foundUser2);

        Wallet userWallet = walletRepository.findByUsername("FinanceGuru");
        assertNotNull(userWallet);
    }

}
