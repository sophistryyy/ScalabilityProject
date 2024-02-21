package seng468.scalability.endpoints.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seng468.scalability.matchingEngine.MatchingEngineService;
import seng468.scalability.models.Response;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.repositories.StockRepository;

import java.util.List;

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
    public Response placeStockOrder(@RequestBody StockOrder req) {
        try {

            int stock_id = req.getStockId();
            if (!stockRepository.existsById(stock_id)) {
                return Response.error("Invalid stock");
            }

            // Check if the user has enough of that stock (use Wallet)

            StockOrder order = matchingEngineService.placeOrder(req);
            return Response.ok(order.toString());
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

}
