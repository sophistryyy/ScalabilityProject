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
import seng468scalability.com.portfolio.request.InternalAddStockToUserRequest;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock.entity.Stock;
import seng468scalability.com.stock.repositories.StockRepository;


@RequiredArgsConstructor
@RestController
public class InternalAddStockToUserController {
    

    private final PortfolioRepository portfolioRepository;
    private final StockRepository stockRepository;

    @PostMapping("/internal/addStockToUser")
    public Response addStockToUser(@RequestBody InternalAddStockToUserRequest req) {
        if(req.stockId() == null || req.stockId() <= 0 || req.quantity() == null || req.quantity() <= 0){
            return Response.error("Invalid parameter. Either null, 0 or negative number");
        }
        Stock stock = stockRepository.findStockById(req.stockId());

        if (stock == null) {
            return Response.error("Invalid Stock Id");
        }

        PortfolioEntry entry = portfolioRepository.findByPortfolioEntryId(new PortfolioEntryId(req.stockId(), req.username()));
        if (entry == null) {
            String stockName = stock.getName();
            entry = new PortfolioEntry(new PortfolioEntryId(req.stockId(), req.username()), stockName, req.quantity());
        } else {
            entry.addQuantity(req.quantity());
        }

        portfolioRepository.save(entry);

        return Response.ok(null);
    }


}