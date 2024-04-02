package seng468scalability.com.stock.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock.entity.Stock;
import seng468scalability.com.stock.repositories.StockRepository;
import seng468scalability.com.stock.request.CreateStockRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CreateStockController {

    private final StockRepository stockRepository;


    @PostMapping("/createStock")
    public Response createStock(@RequestBody CreateStockRequest req) {
        try {
            Stock newStock = new Stock(req.getStockName());
            stockRepository.saveNewStock(newStock);

            Map<String, Object> data =  new HashMap<String, Object>();
            data.put("stock_id", newStock.getId());

            return Response.ok(data);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
