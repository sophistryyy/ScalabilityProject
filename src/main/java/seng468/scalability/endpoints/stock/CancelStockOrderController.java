package seng468.scalability.endpoints.stock;

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

@RestController
@RequestMapping(path = "cancelStockTransaction")
public class CancelStockOrderController {

    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final WalletRepository walletRepository;
    private final PortfolioRepository portfolioRepository;
    private final MatchingEngineUtil matchingEngineUtil;
    @Autowired
    public CancelStockOrderController(MatchingEngineOrdersRepository matchingEngineOrdersRepository, WalletRepository walletRepository,
                                      PortfolioRepository portfolioRepository, MatchingEngineUtil matchingEngineUtil)
    {
        this.matchingEngineUtil = matchingEngineUtil;
        this.matchingEngineOrdersRepository = matchingEngineOrdersRepository;
        this.walletRepository = walletRepository;
        this.portfolioRepository = portfolioRepository;
    }
    @PostMapping
    public Response cancelStockTransaction(@RequestBody CancelOrderRequest req)
    {
        try {
            Integer stock_tx_id = req.getTransactionId();
            String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

            StockOrder foundOrder = matchingEngineOrdersRepository.findAllByUsernameAndStockTxId(stock_tx_id, username);
            if (foundOrder == null) {
                return Response.error("Transaction not found");
            }
            if (foundOrder.getOrderType() == StockOrder.OrderType.MARKET) {
                return Response.error("You can only cancel LIMIT orders");
            }
            if (foundOrder.getOrderStatus() == StockOrder.OrderStatus.IN_PROGRESS || foundOrder.getOrderStatus() == StockOrder.OrderStatus.PARTIAL_FULFILLED) {
                if (!foundOrder.getIs_buy())//return stocks
                {
                    matchingEngineUtil.saveToPortfolio(foundOrder, foundOrder.getTrueRemainingQuantity());
                } else//buy order
                {
                    matchingEngineUtil.returnMoney(foundOrder.getStock_tx_id(), foundOrder.getUsername(), foundOrder.getPrice() * foundOrder.getTrueRemainingQuantity());//change from 0, add logic to handle this
                }
                matchingEngineUtil.removeStockTransaction(foundOrder);
            } else {
                return Response.error("Can't cancel this order. It's either completed or expired");
            }
            return Response.ok(null);
        }catch(Exception e)
        {
            return Response.error("Couldn't return money.");
        }

    }
}
