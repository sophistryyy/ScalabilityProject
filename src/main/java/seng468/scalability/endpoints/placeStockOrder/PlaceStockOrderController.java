package seng468.scalability.endpoints.placeStockOrder;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seng468.scalability.matchingEngine.MatchingEngineService;
import seng468.scalability.models.entity.StockOrder;

@RestController
@RequestMapping(path = "placeStockOrder")
public class PlaceStockOrderController {
    private final MatchingEngineService matchingEngineService;

    @Autowired
    public PlaceStockOrderController(final MatchingEngineService matchingEngineService) {this.matchingEngineService = matchingEngineService;}

    @PostMapping
    public void placeStockOrder(@RequestBody StockOrder req)
    {
        matchingEngineService.test();

    }
}
