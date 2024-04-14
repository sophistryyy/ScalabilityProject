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
import seng468scalability.com.portfolio.request.InternalAddStockToUserRequest;
import seng468scalability.com.portfolio.request.InternalUpdateUserStockRequest;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock.entity.Stock;
import seng468scalability.com.stock.repositories.StockRepository;


@RequiredArgsConstructor
@RestController
public class InternalAddStockToUserController {
    

    private final PortfolioRepository portfolioRepository;
    private final StockRepository stockRepository;

    @PostMapping("/internal/updateUserStock")
    public Response addStockToUser(@RequestBody InternalUpdateUserStockRequest req) {
        try {

            
            Stock stock = stockRepository.findStockById(req.stockId());
            if (stock == null) {
                return Response.error("Invalid Stock Id");
            }

            PortfolioEntry entry = portfolioRepository.findByPortfolioEntryId(new PortfolioEntryId(req.stockId(), req.username()));

        
            if (req.add()) {
                entry.addQuantity(req.quantity());
            } else {
                if (req.quantity() > entry.getQuantity()) {
                    return Response.error("Removing more stock than available");
                }
                entry.removeQuantity(req.quantity());
            }

            portfolioRepository.save(entry);

            return Response.ok(null);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }


}