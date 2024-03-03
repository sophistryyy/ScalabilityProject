package seng468.scalability.endpoints.frontend_stuff;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import seng468.scalability.models.response.Response;

import java.io.IOException;


@RestController
public class FrontendController {

    @GetMapping("/login-ui")
    public void redirectToLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/frontend/login.html");
    }

    
    @GetMapping("/register-ui")
    public void redirectToRegister(HttpServletResponse response) throws IOException {
        response.sendRedirect("/frontend/register.html");
    }

    @GetMapping("/dashboard-ui")
    public void redirectToDashboard(HttpServletResponse response) throws IOException {
        response.sendRedirect("/frontend/dashboard.html");
    }

    @GetMapping("/AddStock-ui")
    public void redirectToAddStock(HttpServletResponse response) throws IOException {
        response.sendRedirect("/frontend/AddStock.html");
    }

    @GetMapping("/getWalletTransactions-ui")
    public void redirectToWalletTransactions(HttpServletResponse response) throws IOException{
        response.sendRedirect("/frontend/getWalletTransactions");
    }
    @GetMapping("/getStockTransactions-ui")
    public void redirectToStockTransactions(HttpServletResponse response) throws IOException{
        response.sendRedirect("/frontend/getStockTransactions");
    }

  


        
        
    }