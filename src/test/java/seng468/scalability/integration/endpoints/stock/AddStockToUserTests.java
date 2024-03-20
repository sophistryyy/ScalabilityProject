package seng468.scalability.integration.endpoints.stock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import seng468.scalability.models.entity.PortfolioEntry;
import seng468.scalability.models.entity.Stock;
import seng468.scalability.models.entity.User;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AddStockToUserTests {
    
    @Autowired
    private MockMvc mvc;

    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    private String jwtToken;

    User user;
    
    @BeforeAll
    void setupBeforeAll() {
        user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");
        userRepository.save(user);

        jwtToken = jwtUtil.generateToken(user.getUsername());

        Stock stock1 = new Stock("Google");
        stockRepository.save(stock1);

        Stock stock2 = new Stock("Apple");
        stockRepository.save(stock2);
    } 

    @BeforeEach
    void setupBeforeEach() {
        portfolioRepository.deleteAll();
    }

    @AfterAll
    void breakdownAfterAll() {
        portfolioRepository.deleteAll();
        stockRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    @Test 
    public void testAddStockToUser() throws Exception {
        Long googleStockId = 1L;

        String requestBody = "{\"stock_id\": \"" + String.valueOf(googleStockId) + "\", \"quantity\": \"550\"}"; // Google stock ID should be 1
        MvcResult res = mvc.perform(post("/addStockToUser")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andReturn();

        PortfolioEntry entry = portfolioRepository.findEntryByStockIdAndUsername(googleStockId, user.getUsername());
        assertEquals(user.getUsername(), entry.getUsername());
        assertEquals(googleStockId, entry.getStockId());
        assertEquals(550, entry.getQuantity());
    }

    @Test
    public void testAddStockToUserWithInvalidStock() throws Exception {
        Long invalidStockId = 3L;

        String requestBody = "{\"stock_id\": \"" + String.valueOf(invalidStockId) + "\", \"quantity\": \"550\"}"; // Google stock ID should be 1
        MvcResult res = mvc.perform(post("/addStockToUser")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data.error").value("Invalid Stock Id"))
        .andReturn();

         PortfolioEntry entry = portfolioRepository.findEntryByStockIdAndUsername(invalidStockId, user.getUsername());
         assertNull(entry);
    }

    @Test
    public void testAddStockToUserWithExistingEntry() throws Exception {
        Long googleStockId = 1L;
        PortfolioEntry entry1 = new PortfolioEntry(googleStockId, "Google", user.getUsername(), 550);
        portfolioRepository.save(entry1);

        Long appleStockId = 2L;
        String requestBody = "{\"stock_id\": \"" + String.valueOf(appleStockId) + "\", \"quantity\": \"369\"}"; // Google stock ID should be 1
        MvcResult res = mvc.perform(post("/addStockToUser")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andReturn();

        PortfolioEntry entry = portfolioRepository.findEntryByStockIdAndUsername(appleStockId, user.getUsername());
        assertEquals(user.getUsername(), entry.getUsername());
        assertEquals(appleStockId, entry.getStockId());
        assertEquals(369, entry.getQuantity());
    }

    @Test
    public void testAddStockToUserAddQuantityToExistingEntry() throws Exception {
        Long googleStockId = 1L;
        PortfolioEntry entry1 = new PortfolioEntry(googleStockId, "Google", user.getUsername(), 550);
        portfolioRepository.save(entry1);

        String requestBody = "{\"stock_id\": \"" + String.valueOf(googleStockId) + "\", \"quantity\": \"50\"}"; // Google stock ID should be 1
        MvcResult res = mvc.perform(post("/addStockToUser")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andReturn();

        PortfolioEntry entry = portfolioRepository.findEntryByStockIdAndUsername(googleStockId, user.getUsername());
        assertEquals(user.getUsername(), entry.getUsername());
        assertEquals(googleStockId, entry.getStockId());
        assertEquals(600, entry.getQuantity());
    }

}
