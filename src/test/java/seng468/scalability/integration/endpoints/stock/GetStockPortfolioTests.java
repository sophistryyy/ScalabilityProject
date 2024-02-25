package seng468.scalability.integration.endpoints.stock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import seng468.scalability.models.entity.User;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetStockPortfolioTests {
    
    @Autowired
    private MockMvc mvc;

    @Autowired 
    private JwtUtil jwtUtil;

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
    } 

    @BeforeEach
    void setUpBeforeEach() {
        portfolioRepository.deleteAll();
    }
    
    @Test 
    public void testGetStockPortfolio() throws Exception {
        PortfolioEntry entry1 = new PortfolioEntry(1, "Google", user.getUsername(), 550);
        portfolioRepository.save(entry1);
        PortfolioEntry entry2 = new PortfolioEntry(2, "Apple", user.getUsername(), 369);
        portfolioRepository.save(entry2);

        MvcResult res = mvc.perform(get("/getStockPortfolio")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray()) // Check if data is an array
        .andExpect(jsonPath("$.data[0].stock_id").value(1)) // Check first element
        .andExpect(jsonPath("$.data[0].stock_name").value("Google"))
        .andExpect(jsonPath("$.data[0].quantity_owned").value(550))
        .andExpect(jsonPath("$.data[1].stock_id").value(2)) // Check second element
        .andExpect(jsonPath("$.data[1].stock_name").value("Apple"))
        .andExpect(jsonPath("$.data[1].quantity_owned").value(369))
        .andReturn();
    }

    @Test
    public void testGetStockPortfolioEmpty() throws Exception {
        MvcResult res = mvc.perform(get("/getStockPortfolio")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data").isEmpty())
        .andReturn();
    }
}
