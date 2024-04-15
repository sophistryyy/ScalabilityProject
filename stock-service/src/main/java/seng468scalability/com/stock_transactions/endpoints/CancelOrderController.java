package seng468scalability.com.stock_transactions.endpoints;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock_transactions.request.CancelOrderRequest;
import seng468scalability.com.stock_transactions.util.CancelOrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "cancelStockTransaction")
public class CancelOrderController {

    private final CancelOrderService cancelOrderService;


    @PostMapping
    public Response cancelStockTransaction(@RequestBody CancelOrderRequest req, @RequestHeader("X-Username") String username)
    {
        try {
            if(username == null){
                return Response.error("User not found");
            }
            String message = cancelOrderService.try_cancelling(req, username);

            if(message != null)
            {
                return Response.error(message);
            }
            return Response.ok(null);
        }catch(Exception e)
        {
            //return Response.error("Couldn't return money.");
            return Response.error(e.getMessage());
        }
    }


}
