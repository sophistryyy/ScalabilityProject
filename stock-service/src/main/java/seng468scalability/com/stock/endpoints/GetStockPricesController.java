package seng468scalability.com.stock.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import seng468scalability.com.response.Response;


@RestController
public class GetStockPricesController {
    @GetMapping(path="/getStockPrices")
    public Response getStockPrices() {
        return Response.error("not yet implemented");
    }
}
