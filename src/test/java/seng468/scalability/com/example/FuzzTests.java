package seng468.scalability.com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;

import seng468.scalability.authentication.JwtUtil;
import seng468.scalability.models.entity.PortfolioEntry;
import seng468.scalability.models.entity.User;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.UserRepository;

//User user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @SpringBootTest(classes = Test.class)
@AutoConfigureMockMvc
public class FuzzTests {

    @Autowired
    private MockMvc mvc;

    //private User user;

    // @Autowired 
    // private UserRepository userRepository;

    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    private String jwtToken;
    private User user;

    @BeforeAll
    void setupBeforeAll(){
        user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");
        userRepository.save(user);
        jwtToken = jwtUtil.generateToken(user.getUsername());
    }

    @BeforeEach
    void setup() {
        //userRepository.deleteAll();
        portfolioRepository.deleteAll();

    } 

   @FuzzTest
    public void fuzzGetStockPortfolio(FuzzedDataProvider data) throws Exception {
            String input1 = data.consumeRemainingAsString();
            String input2 = data.consumeRemainingAsString();
            PortfolioEntry entry1 = new PortfolioEntry(1, input1, user.getUsername(), 550);
            portfolioRepository.save(entry1);
            PortfolioEntry entry2 = new PortfolioEntry(2, input2, user.getUsername(), 369);
            portfolioRepository.save(entry2);

            MvcResult res = mvc.perform(get("/getStockPortfolio")
            .contentType(MediaType.APPLICATION_JSON)
            .header("token", jwtToken)
            .content(""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray()) // Check if data is an array
            .andExpect(jsonPath("$.data[0].stock_id").value(1)) // Check first element
            .andExpect(jsonPath("$.data[0].stock_name").value(input1))
            .andExpect(jsonPath("$.data[0].quantity_owned").value(550))
            .andExpect(jsonPath("$.data[1].stock_id").value(2)) // Check second element
            .andExpect(jsonPath("$.data[1].stock_name").value(input2))
            .andExpect(jsonPath("$.data[1].quantity_owned").value(369))
            .andReturn();
        }

   @FuzzTest
   public void fuzzIncorrectLogin(FuzzedDataProvider data) throws Exception {
      String input1 = data.consumeRemainingAsString();
      String input2 = data.consumeRemainingAsString();
      String input3 = data.consumeRemainingAsString();
      String input4 = data.consumeRemainingAsString();

    //   User user = new User(input1, input2, input3);
      //userRepository.save(user);

      String requestBody = "{\"username\": \""+input1+"\",\"password\": \""+input4+"\",\"name\": \""+input3+"\"}";
      MvcResult res = mvc.perform(post("/login")
      .contentType(MediaType.APPLICATION_JSON).content(requestBody))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.data.Error").value("Bad credentials"))
      .andReturn();
      //assertEquals(input, SomeScheme.decode(SomeScheme.encode(input)));
   }

}