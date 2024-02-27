package seng468.scalability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import seng468.scalability.models.entity.User;
import seng468.scalability.repositories.UserRepository;

//User user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");


@SpringBootTest
@AutoConfigureMockMvc
public class FuzzTests {

    @Autowired
    private MockMvc mvc;

    //private User user;

    @Autowired 
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    } 

   
   @Test 
   public void testIncorrectLogin() throws Exception {
       User user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");
       userRepository.save(user);

       String requestBody = "{\"username\": \"VanguardETF\",\"password\": \"Vang@1234\",\"name\": \"Vanguard Corp.\"}";
       MvcResult res = mvc.perform(post("/login")
       .contentType(MediaType.APPLICATION_JSON).content(requestBody))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.success").value(false))
       .andExpect(jsonPath("$.data.Error").value("Bad credentials"))
       .andReturn();
   }

   @FuzzTest
   public void fuzzIncorrectLogin(FuzzedDataProvider data) throws Exception {
      String input1 = data.consumeRemainingAsString();
      String input2 = data.consumeRemainingAsString();
      String input3 = data.consumeRemainingAsString();
      String input4 = data.consumeRemainingAsString();

      User user = new User(input1, input2, input3);
      userRepository.save(user);

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