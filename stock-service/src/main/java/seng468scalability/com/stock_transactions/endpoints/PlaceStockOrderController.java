package seng468scalability.com.stock_transactions.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import seng468scalability.com.stock_transactions.request.PlaceStockOrderRequest;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock_transactions.util.StockOrderUtil;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "placeStockOrder")
public class PlaceStockOrderController {

    private final StockOrderUtil util;

    @PostMapping
    public Response placeStockOrder(@RequestBody PlaceStockOrderRequest req, @RequestHeader("X-Username") String username) {
        try {
            if(username == null || username.isEmpty()){
                return Response.error("Invalid username.");
            }
            String errorMessage = util.basicVerifier(req); //matchingEngineService.placeOrder(req, username);
            if(errorMessage != null)
            {
                return Response.error(errorMessage);
            }
            
            return Response.ok(null);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }



}
