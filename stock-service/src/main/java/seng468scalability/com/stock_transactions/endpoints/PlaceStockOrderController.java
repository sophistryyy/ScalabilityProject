package seng468scalability.com.stock_transactions.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import seng468scalability.com.stock_transactions.request.PlaceStockOrderRequest;
import seng468scalability.com.response.Response;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "placeStockOrder")
public class PlaceStockOrderController {



    @PostMapping
    public Response placeStockOrder(@RequestBody PlaceStockOrderRequest req, @RequestHeader("X-Username") String username) {
        try {
            String message = null; //matchingEngineService.placeOrder(req, username);
            if(message != null)
            {
                return Response.error(message);
            }
            
            return Response.ok(null);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }



}
