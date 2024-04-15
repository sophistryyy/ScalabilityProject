package seng468scalability.com.portfolio.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seng468scalability.com.portfolio.entity.PortfolioEntry;
import seng468scalability.com.portfolio.entity.PortfolioEntryId;
import seng468scalability.com.portfolio.repository.PortfolioRepository;
import seng468scalability.com.portfolio.request.AddStockToUserRequest;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock.entity.Stock;
import seng468scalability.com.stock.repositories.StockRepository;

@Service
@RequiredArgsConstructor
public class AddStockToUserService {

    private final PortfolioRepository portfolioRepository;
    private final StockRepository stockRepository;

    public String addStockToUserService(AddStockToUserRequest req, String username){
        if(req.stockId() == null || req.stockId() <= 0 || req.quantity() == null || req.quantity() <= 0){
            return "Invalid parameter. Either null, 0 or negative number";
        }
        Stock stock = stockRepository.findStockById(req.stockId());

        if (stock == null) {
            return "Invalid Stock Id";
        }

        PortfolioEntry entry = portfolioRepository.findByPortfolioEntryId(new PortfolioEntryId(req.stockId(), username));
        if (entry == null) {
            String stockName = stock.getName();
            entry = new PortfolioEntry(new PortfolioEntryId(req.stockId(), username), stockName, req.quantity());
        } else {
            entry.addQuantity(req.quantity());
        }

        portfolioRepository.save(entry);
        return null;
    }
}
