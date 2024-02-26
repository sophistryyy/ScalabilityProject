package seng468.scalability.endpoints.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seng468.scalability.matchingEngine.MatchingEngineService;
import seng468.scalability.models.response.Response;
import seng468.scalability.models.request.PlaceStockOrderRequest;
import seng468.scalability.repositories.StockRepository;

@RestController
@RequestMapping(path = "placeStockOrder")
public class PlaceStockOrderController {
    private final MatchingEngineService matchingEngineService;
    private final StockRepository stockRepository;

    @Autowired
    public PlaceStockOrderController(MatchingEngineService matchingEngineService, StockRepository stockRepository) {
        this.matchingEngineService = matchingEngineService;
        this.stockRepository = stockRepository;
    }


    @PostMapping
    public Response placeStockOrder(@RequestBody PlaceStockOrderRequest req) {
        try {

            int stock_id = req.getStock_id();
            if (!stockRepository.existsById(stock_id)) {return Response.error("Invalid stock");}

            String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            // Check if the user has enough of that stock (use Wallet)
            //remove the stock amount from StockPorfolio

            String message = matchingEngineService.placeOrder(req, username);
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
