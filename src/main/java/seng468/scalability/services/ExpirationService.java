package seng468.scalability.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import seng468.scalability.matchingEngine.MatchingEngineOrdersRepository;
import seng468.scalability.matchingEngine.MatchingEngineUtil;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.WalletRepository;

import java.time.LocalDateTime;
import java.util.LinkedList;

@Service
public class ExpirationService {
    private final MatchingEngineOrdersRepository matchingEngineOrdersRepository;
    private final WalletRepository walletRepository;
    private final PortfolioRepository portfolioRepository;
    private final MatchingEngineUtil matchingEngineUtil;

    @Autowired
    public ExpirationService(MatchingEngineOrdersRepository matchingEngineOrdersRepository, WalletRepository walletRepository,
                              PortfolioRepository portfolioRepository, MatchingEngineUtil matchingEngineUtil)
    {
        this.matchingEngineUtil = matchingEngineUtil;
        this.matchingEngineOrdersRepository = matchingEngineOrdersRepository;
        this.walletRepository = walletRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Async
    @Scheduled(fixedRate = 6000)//every minute
    public void expireStocks(){
        LinkedList<StockOrder> stockOrders = matchingEngineOrdersRepository.getAllLimitOrders();
        LocalDateTime timeNow = LocalDateTime.now();
        for(StockOrder order: stockOrders)
        {
            LocalDateTime timestampExpireTime = order.getTimestamp().plusMinutes(15);
            if(timestampExpireTime.isAfter(timeNow))//expired
            {
                boolean partialFulfilled = order.getOrderStatus() == StockOrder.OrderStatus.PARTIAL_FULFILLED;
                order.setExpired(true);
                if(order.getIs_buy())//buy
                {
                    if(partialFulfilled){
                        
                    }else{

                    }
                }else{///sell
                    if(partialFulfilled){

                    }else{

                    }
                }
            }else{
                break;
            }

        }
    }
}
