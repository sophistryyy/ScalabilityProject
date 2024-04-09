package seng468scalability.com.portfolio.endpoints;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import seng468scalability.com.portfolio.entity.PortfolioEntry;
import seng468scalability.com.portfolio.entity.PortfolioEntryId;
import seng468scalability.com.portfolio.repository.PortfolioRepository;
import seng468scalability.com.portfolio.request.AddStockToUserRequest;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock.entity.Stock;
import seng468scalability.com.stock.repositories.StockRepository;

import java.util.LinkedHashMap;

@RequiredArgsConstructor
@RestController
@Slf4j
public class AddStockToUserController {
    

    private final PortfolioRepository portfolioRepository;
    private final StockRepository stockRepository;

    @PostMapping("/addStockToUser")
    public Response addStockToUser(@RequestBody AddStockToUserRequest req, @RequestHeader("X-username") String username) {

        if(req.stockId() == null || req.stockId() <= 0 || req.quantity() == null || req.quantity() <= 0){
            return Response.error("Invalid parameter. Either null, 0 or negative number");
        }
        Stock stock = stockRepository.findStockById(req.stockId());

        if (stock == null) {
            return Response.error("Invalid Stock Id");
        }

        PortfolioEntry entry = portfolioRepository.findByPortfolioEntryId(new PortfolioEntryId(req.stockId(), username));
        if (entry == null) {
            String stockName = stock.getName();
            entry = new PortfolioEntry(new PortfolioEntryId(req.stockId(), username), stockName, req.quantity());
        } else {
            entry.addQuantity(req.quantity());
        }

        portfolioRepository.save(entry);

        return Response.ok(null);
    }


}
