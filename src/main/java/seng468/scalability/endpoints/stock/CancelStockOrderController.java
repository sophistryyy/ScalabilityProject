package seng468.scalability.endpoints.stock;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seng468.scalability.matchingEngine.MatchingEngineOrdersRepository;
import seng468.scalability.matchingEngine.MatchingEngineUtil;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.models.request.CancelOrderRequest;
import seng468.scalability.models.response.Response;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.WalletRepository;
import seng468.scalability.services.CancelOrderService;

@RestController
@RequestMapping(path = "cancelStockTransaction")
public class CancelStockOrderController {

    private final CancelOrderService cancelOrderService;
    @Autowired
    public CancelStockOrderController(CancelOrderService cancelOrderService)
    {
        this.cancelOrderService = cancelOrderService;
    }

    @PostMapping
    public Response cancelStockTransaction(@RequestBody CancelOrderRequest req)
    {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            String message = cancelOrderService.try_cancelling(req, username);

            if(message != null)
            {
                return Response.error(message);
            }
            return Response.ok(null);
        }catch(Exception e)
        {
            //return Response.error("Couldn't return money.");
            return Response.error(e.getMessage());
        }
    }


}
