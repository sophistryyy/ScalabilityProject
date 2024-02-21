package seng468.scalability.endpoints.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.models.Response;
import seng468.scalability.models.entity.PortfolioEntry;
import seng468.scalability.models.entity.User;
import seng468.scalability.models.request.AddStockToUserRequest;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.repositories.UserRepository;

@RestController
public class AddStockToUser {
    @Autowired
    UserRepository userRepository;
   
    @Autowired
    StockRepository stockRepository;

    @Autowired
    PortfolioRepository portfolioRepository;

    @PostMapping("/addStockToUser")
    public Response addStockToUser(@RequestBody AddStockToUserRequest req) {
        if (stockRepository.findStockById(req.getStockId()) == null) {
            return Response.error("Invalid Stock Id");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        PortfolioEntry entry = portfolioRepository.findEntryByStockIdAndUsername(req.getStockId(), username);
        if (entry == null) {
            entry = new PortfolioEntry(req.getStockId(), username, req.getQuantity());
        } else {
            entry.addQuantity(req.getQuantity());
        }
        portfolioRepository.save(entry);

        return Response.ok(null);
    }
}
