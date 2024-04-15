package matching_engine.util;

import lombok.RequiredArgsConstructor;
import matching_engine.entity.OrderBook;
import matching_engine.entity.enums.OrderStatus;
import matching_engine.entity.enums.OrderType;
import matching_engine.requests.CancelOrderRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelOrderService {

    private final OrderBook orderBook;
    public void try_cancelling(CancelOrderRequest request){
        OrderType orderType = request.getOrderType();
        Boolean isBuy = request.getIsBuy();
        if(orderType == OrderType.LIMIT){
            if(isBuy){
                orderBook.removeBuyOrder(request.getStock_tx_id(), request.getStock_id());
            }else{
                orderBook.removeSellOrder(request.getStock_tx_id(), request.getStock_id());
            }
        }else{
            orderBook.removeMarketBuyOrder(request.getStock_tx_id(), request.getStock_id());
        }
    }

}
