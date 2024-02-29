package seng468.scalability.integration.endpoints.stock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
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
import seng468.scalability.models.entity.Stock;
import seng468.scalability.models.entity.User;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateStockTests {
    
    @Autowired
    private MockMvc mvc;

    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;
    
    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        stockRepository.deleteAll();
    } 

    @AfterAll
    void breakdownAfterAll() {
        stockRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test 
    public void testCreateStock() throws Exception {
        User user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());

        String requestBody = "{\"stock_name\": \"Google\"}";
        MvcResult res = mvc.perform(post("/createStock")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", token)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.stock_id").isNumber())
        .andReturn();
    }

    @Test 
    public void testCreateExistingStock() throws Exception {
        Stock stock = new Stock("Google");
        stockRepository.save(stock);

        User user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());

        String requestBody = "{\"stock_name\": \"Google\"}";
        MvcResult res = mvc.perform(post("/createStock")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", token)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data.error").value("Stock Already Exists"))
        .andReturn();
    }

    @Test 
    public void testCreateSecondStock() throws Exception {

        Stock stock = new Stock("Google");
        stockRepository.save(stock);

        User user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());

        String requestBody = "{\"stock_name\": \"Apple\"}";
        MvcResult res = mvc.perform(post("/createStock")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", token)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.stock_id").isNumber())
        .andReturn();
    }
}
