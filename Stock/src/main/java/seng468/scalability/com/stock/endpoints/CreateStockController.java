package seng468.scalability.com.stock.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import seng468.scalability.com.stock.models.entity.Stock;

import seng468.scalability.com.stock.models.request.CreateStockRequest;
import seng468.scalability.com.response.Response;
import seng468.scalability.com.stock.repositories.StockRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CreateStockController {

    @Autowired
    private StockRepository stockRepository;


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
