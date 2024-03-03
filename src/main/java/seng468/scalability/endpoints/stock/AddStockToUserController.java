package seng468.scalability.endpoints.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.models.entity.Stock;
import seng468.scalability.models.response.Response;
import seng468.scalability.models.entity.PortfolioEntry;
import seng468.scalability.models.request.AddStockToUserRequest;
import seng468.scalability.models.response.Response;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;

@RestController
public class AddStockToUserController {
    
    @Autowired
    StockRepository stockRepository;

    @Autowired
    PortfolioRepository portfolioRepository;

    @PostMapping("/addStockToUser")
    public Response addStockToUser(@RequestBody AddStockToUserRequest req) {

        Stock stock = stockRepository.findStockById(req.getStockId());
        if (stock == null) {
            return Response.error("Invalid Stock Id");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        PortfolioEntry entry = portfolioRepository.findEntryByStockIdAndUsername(req.getStockId(), username);
        if (entry == null) {
            String stockName = stock.getName();
            entry = new PortfolioEntry(req.getStockId(), stockName, username, req.getQuantity());
        } else {
            entry.addQuantity(req.getQuantity());
        }
        portfolioRepository.save(entry);

        return Response.ok(null);
    }
}
