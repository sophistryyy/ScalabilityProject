package seng468.scalability.authentication;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import ch.qos.logback.classic.Logger;
import jakarta.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    Logger logger = (Logger) LoggerFactory.getLogger(SecurityConfig.class);
    @Autowired
    private JwtFilter jwtFilter;
    @Bean 
    public SecurityFilterChain securityFilerChain(HttpSecurity http) throws Exception {
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> 
               httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
 
        http
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/auth/register").permitAll()
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/greeting").authenticated()
                .requestMatchers("/**").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
        ).csrf((csrf) -> csrf.disable()).cors((cors) -> cors.disable()).httpBasic((httpBasic) -> httpBasic.disable())
        .exceptionHandling((exception) -> {System.out.println("ERROR");exception.accessDeniedPage("/error");})
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        //.exceptionHandling().authenticationEntryPoint((request, response, ex) -> { System.out.println("hey"); System.out.println(SecurityContextHolder.getContext().getAuthentication()); System.out.println(ex.getMessage()); System.out.println(request.getUserPrincipal().getName());})
        //.httpBasic(Customizer.withDefaults());
        //.formLogin(Customizer.withDefaults())
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
}