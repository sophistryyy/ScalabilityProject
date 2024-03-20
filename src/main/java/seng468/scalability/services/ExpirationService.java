package seng468.scalability.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import seng468.scalability.matchingEngine.MatchingEngineOrdersRepository;
import seng468.scalability.matchingEngine.MatchingEngineUtil;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.models.entity.WalletTX;
import seng468.scalability.models.entity.StockOrder.OrderStatus;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.WalletRepository;
import seng468.scalability.repositories.WalletTXRepository;

import java.time.LocalDateTime;
import java.util.LinkedList;

@Service
public class ExpirationService {
    private final int MINUTES_TO_EXPIRE = 15;
    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final WalletTXRepository walletTXRepository;
    private final MatchingEngineUtil matchingEngineUtil;

    @Autowired
    public ExpirationService(MatchingEngineOrdersRepository matchingEngineOrdersRepository, WalletTXRepository walletTXRepository,
                            MatchingEngineUtil matchingEngineUtil) {
        this.matchingEngineUtil = matchingEngineUtil;
        this.matchingEngineOrdersRepository = matchingEngineOrdersRepository;
        this.walletTXRepository = walletTXRepository;
    }

    @Async
    @Scheduled(fixedRate = 500)
    public void expireStocks() {
        LinkedList<StockOrder> stockOrders = matchingEngineOrdersRepository.getAllLimitOrders();
        for (StockOrder order : stockOrders)//contains only IN_PROGRESS and PARTIALLY FULFILLED ones
        {   
            LocalDateTime timeNow = LocalDateTime.now();
            LocalDateTime timestampExpireTime = order.getTimestamp().plusMinutes(MINUTES_TO_EXPIRE);

            boolean partialFulfilled = order.getOrderStatus() == StockOrder.OrderStatus.PARTIAL_FULFILLED;
            // Quick solve. Refactor later.
            if (order.getOrderStatus() == OrderStatus.COMPLETED || order.isExpired()) {
               continue; 
            }

            if (timeNow.isAfter(timestampExpireTime))//expired
            {
                order.setExpired(true);
                if (order.getIs_buy())//buy
                {
                    refundMoney(order, partialFulfilled);

                } else {///sell
                    matchingEngineUtil.saveToPortfolio(order, order.getTrueRemainingQuantity());
                    matchingEngineOrdersRepository.save(order);
                }

                if (!partialFulfilled) {
                    matchingEngineUtil.removeStockTransaction(order);
                }
            }else{//sorted by time
                break;  
            }
        }
    }


    public void refundMoney(StockOrder order, Boolean partialFulfilled) {
        WalletTX originalWalletTx = walletTXRepository.findByWalletTXId(order.getWalletTXid());
        Long toRefund = originalWalletTx.getAmount();
        if (partialFulfilled) {
            LinkedList<StockOrder> lstOfOrders = matchingEngineOrdersRepository.findAllParentChildTransactions(order.getStock_tx_id());
            for (StockOrder childOrder : lstOfOrders) {
                //childorder holds a price with how much it was bought for!
                toRefund -= childOrder.getPrice();
            }
        }
        if (toRefund > 0) {
            try {
                matchingEngineUtil.returnMoney(order.getStock_tx_id(), order.getUsername(), toRefund);
            } catch (Exception e) {
                ;
            }
        }
    }
}
