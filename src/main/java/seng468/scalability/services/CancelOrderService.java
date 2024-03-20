package seng468.scalability.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seng468.scalability.matchingEngine.MatchingEngineOrdersRepository;
import seng468.scalability.matchingEngine.MatchingEngineUtil;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.models.request.CancelOrderRequest;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.WalletRepository;

@Service
public class CancelOrderService {

    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final WalletRepository walletRepository;
    private final PortfolioRepository portfolioRepository;
    private final MatchingEngineUtil matchingEngineUtil;

    @Autowired
    public CancelOrderService(MatchingEngineOrdersRepository matchingEngineOrdersRepository, WalletRepository walletRepository,
                                      PortfolioRepository portfolioRepository, MatchingEngineUtil matchingEngineUtil)
    {
        this.matchingEngineUtil = matchingEngineUtil;
        this.matchingEngineOrdersRepository = matchingEngineOrdersRepository;
        this.walletRepository = walletRepository;
        this.portfolioRepository = portfolioRepository;
    }
    @Transactional
    public String try_cancelling(CancelOrderRequest req, String username) throws Exception
    {
        Long stock_tx_id = req.getTransactionId();

        StockOrder foundOrder = matchingEngineOrdersRepository.findByUsernameAndStockTxId(stock_tx_id, username);
        if (foundOrder == null) {
            return "Transaction not found";
        }
        if (foundOrder.getOrderType() == StockOrder.OrderType.MARKET) {
            return "You can only cancel LIMIT orders";
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
            return "Can't cancel this order. It's either completed or expired";
        }
        return null;
    }
}
