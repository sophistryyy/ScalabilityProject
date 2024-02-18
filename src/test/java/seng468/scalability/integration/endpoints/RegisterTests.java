package seng468.scalability.integration.endpoints;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterTests {

    @Autowired
    private MockMvc mvc;

    @Autowired 
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    } 

    @Test 
    public void testRegister() throws Exception {
        String requestBody = "{\"username\": \"VanguardETF\",\"password\": \"Vang@123\",\"name\": \"Vanguard Corp.\"}";
        MvcResult res = mvc.perform(get("/auth/register")
        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andReturn();

        User foundUser = userRepository.findByUsername("VanguardETF");
        assertNotNull(foundUser);
    }

    @Test 
    public void testRegisterWithExistingUser() throws Exception {
        User user = new User("VanguardETF", "Vang@123", "Vanguard Corp.");
        userRepository.save(user);
        
        String requestBody = "{\"username\": \"VanguardETF\",\"password\": \"Comp@124\",\"name\": \"Vanguard Ltd.\"}";
        MvcResult res = mvc.perform(get("/auth/register")
        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andExpect(jsonPath("$.message").value("Username Already Exists"))
        .andReturn();

        User foundUser = userRepository.findByUsername("VanguardETF");
        assertNotNull(foundUser);
        assertEquals("Vanguard Corp.", foundUser.getName());;
    }


}
