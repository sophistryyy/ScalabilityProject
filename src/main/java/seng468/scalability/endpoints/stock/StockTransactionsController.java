package seng468.scalability.endpoints.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seng468.scalability.matchingEngine.MatchingEngineOrdersRepository;
import seng468.scalability.matchingEngine.MatchingEngineService;
import seng468.scalability.models.Response;
import seng468.scalability.models.entity.StockOrder;

import java.util.List;

@RestController
@RequestMapping(path = "getStockTransactions")
public class StockTransactionsController {
    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    @Autowired
    public StockTransactionsController(MatchingEngineOrdersRepository matchingEngineOrdersRepository)
    {
        this.matchingEngineOrdersRepository = matchingEngineOrdersRepository;
    }
    @GetMapping
    public Response getStockTransactions()
    {
        try{
            String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            List<StockOrder> transLst = matchingEngineOrdersRepository.findAllByUsername(username);
            return Response.ok(transLst.toString());
        }catch(Exception e)
        {
            return Response.error(e.getMessage());
        }
    }
}
