package seng468.scalability.integration.endpoints;

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

import seng468.scalability.authentication.UserRepository;
import seng468.scalability.models.Entity.User;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTests {

    @Autowired
    private MockMvc mvc;

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
        .andExpect(jsonPath("$.data").exists())
        .andReturn();
    }
}
