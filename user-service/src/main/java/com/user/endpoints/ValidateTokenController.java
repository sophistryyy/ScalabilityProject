package com.user.endpoints;

import authentication.JwtUtil;
import com.user.repositories.UserRepository;
import com.user.request.ValidateRequest;
import com.user.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ValidateTokenController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRespository;
    private final UserDetailsService userDetailsService;

    @PostMapping(value = "/validate")
    public Response validateToken(@RequestBody ValidateRequest validateRequest)
    {

        final String jwtToken = validateRequest.getToken();

        String username = null;

        if (jwtToken != null) {
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (Exception e) {

                Response.error("Error extracting username from token: " + e.getMessage());
            }
        }else{
            Response.error("Invalid token");
        }

        // Authenticate user is possible
        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                return Response.ok(userDetails.getUsername());
            }
        }

        return Response.error("Invalid credentials for validating.");
    }
}
