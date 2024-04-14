package matching_engine;

import lombok.RequiredArgsConstructor;
import matching_engine.entity.OrderExecutionMessage;
import matching_engine.entity.enums.OrderStatus;
import matching_engine.entity.enums.OrderType;
import matching_engine.requests.InternalUpdateUserStockRequest;
import matching_engine.requests.NewStockTransactionRequest;
import matching_engine.requests.NewWalletTransactionRequest;
import matching_engine.util.RabbitMQConsumer;
import matching_engine.util.RabbitMQProducer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class test {

    private final RabbitMQProducer producer;
    @GetMapping(path = "/test")
    public void test_producer(){

        NewStockTransactionRequest childTX = NewStockTransactionRequest.builder().stockId(1L).
                isBuy(true).
                orderType(OrderType.LIMIT).
                quantity(10L).price(25L)
                .orderStatus(OrderStatus.COMPLETED)
                .username("test")
                .parent_stock_tx_id(99L)
                .walletTXid(null).stock_tx_id(null).build();

        NewWalletTransactionRequest walletTx = NewWalletTransactionRequest.builder().username("test").isDebit(true).amount(250L).build();
        InternalUpdateUserStockRequest updateUserStockRequest = new InternalUpdateUserStockRequest(1L, 10L, "test", true);
        OrderExecutionMessage message = new OrderExecutionMessage(childTX, walletTx, updateUserStockRequest, false);
        producer.sendMessage(message);
    }
}
