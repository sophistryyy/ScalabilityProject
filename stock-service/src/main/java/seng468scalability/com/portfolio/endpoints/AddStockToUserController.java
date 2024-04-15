package seng468scalability.com.portfolio.endpoints;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import seng468scalability.com.portfolio.entity.PortfolioEntry;
import seng468scalability.com.portfolio.entity.PortfolioEntryId;
import seng468scalability.com.portfolio.repository.PortfolioRepository;
import seng468scalability.com.portfolio.request.AddStockToUserRequest;
import seng468scalability.com.portfolio.util.AddStockToUserService;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock.entity.Stock;
import seng468scalability.com.stock.repositories.StockRepository;

import java.util.LinkedHashMap;

@RequiredArgsConstructor
@RestController
@Slf4j
public class AddStockToUserController {
    

    private final AddStockToUserService service;

    @PostMapping("/addStockToUser")
    public Response addStockToUser(@RequestBody AddStockToUserRequest req, @RequestHeader("X-username") String username) {
       String message = service.addStockToUserService(req,username);
       if(message != null){
           return Response.error(message);
       }else{
           return Response.ok(null);
       }
    }


}

