package seng468scalability.com.stock_transactions.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import seng468scalability.com.stock_transactions.entity.StockTransaction;
import seng468scalability.com.stock_transactions.entity.enums.OrderStatus;
import seng468scalability.com.stock_transactions.entity.enums.OrderType;
import seng468scalability.com.stock_transactions.repositories.StockTransactionsRepository;
import seng468scalability.com.stock_transactions.request.PlaceStockOrderRequest;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock_transactions.util.RabbitMQProducer;
import seng468scalability.com.stock_transactions.util.StockOrderUtil;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "placeStockOrder")
public class PlaceStockOrderController {

    private final StockOrderUtil stockUtil;
    private final StockTransactionsRepository stockTransactionsRepository;
    private final RabbitMQProducer producer;
    @PostMapping
    public Response placeStockOrder(@RequestBody PlaceStockOrderRequest req, @RequestHeader("X-Username") String username) {
        try {
            if(username == null || username.isEmpty()){
                return Response.error("Invalid username.");
            }
            String errorMessage = stockUtil.basicVerifier(req);
            if(errorMessage != null)
            {
                return Response.error(errorMessage);
            }
            StockTransaction order = stockUtil.createStockTX(null, req.stock_id(), null, null, req.is_buy(),
                    OrderType.valueOf(req.orderType()), req.quantity(), req.price(), OrderStatus.IN_PROGRESS, username);

            String message = stockUtil.verifyIfEnough(order, username);
            if(message != null)
            {
                //potentially holds wallet tx id as string
                try{
                    Long walletTXid = Long.parseLong(message);
                    order.setWalletTXid(walletTXid);
                }catch (Exception e){ //no digits,so it's an error message
                    return Response.error(message);
                }
            }
            stockTransactionsRepository.save(order);
            producer.sendMessage(order);
            return Response.ok(null);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }



}
