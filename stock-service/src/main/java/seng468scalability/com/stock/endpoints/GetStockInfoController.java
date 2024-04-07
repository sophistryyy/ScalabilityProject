package seng468scalability.com.stock.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock.entity.Stock;
import seng468scalability.com.stock.repositories.StockRepository;
import seng468scalability.com.stock.request.StockInfoRequest;

import java.util.*;

@RequiredArgsConstructor
@RestController
public class GetStockInfoController {

    private final StockRepository stockRepository;

    @PostMapping(path = "/internal/getStockInfo")
    public Response getStockInfo(@RequestBody StockInfoRequest stockInfoRequest) {
        Stock stock = stockRepository.findStockById(stockInfoRequest.stockId());
        if (stock == null) {
            return Response.error("Invalid Stock Id");
        }

        return Response.ok(stock);
    }

}
