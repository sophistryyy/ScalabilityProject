package com.user.endpoints;

import authentication.JwtUtil;
import com.user.models.request.LoginRequest;
import com.user.models.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {


    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Response loginUser(@RequestBody LoginRequest userReq) {
        try {
            Authentication authenticate = authenticationManager
            .authenticate(
                new UsernamePasswordAuthenticationToken(
                    userReq.getUsername(), userReq.getPassword() 
                )
            );
            UserDetails user = (UserDetails)authenticate.getPrincipal();
            String token = jwtUtil.generateToken(user.getUsername());

            Map<String, Object> data =  new HashMap<String, Object>();
            data.put("token", token);

            return Response.ok(data);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
