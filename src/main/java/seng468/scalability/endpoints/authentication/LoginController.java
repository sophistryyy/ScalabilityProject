package seng468.scalability.endpoints.authentication;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.authentication.JwtUtil;
import seng468.scalability.models.Request.LoginRequest;

@RequestMapping("/auth")
@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired 
    private JwtUtil jwtUtil;

    @GetMapping("/login")
    public Map<String, Object> loginUser(@RequestBody LoginRequest userReq) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        try {
            Authentication authenticate = authenticationManager
            .authenticate(
                new UsernamePasswordAuthenticationToken(
                    userReq.getUsername(), userReq.getPassword() 
                )
            );

            UserDetails user = (UserDetails)authenticate.getPrincipal();
            String token = jwtUtil.generateToken(user.getUsername());

            Map<String, Object> data =  new LinkedHashMap<String, Object>();
            data.put("token", token);

            response.put("success", true);
            response.put("data", data);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("data", null);
            response.put("message", "Invalid Username or Password");
        }
        return response;
    }
}
