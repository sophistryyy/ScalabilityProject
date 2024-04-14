package execution_service.utils;

import java.util.Map;

import org.mockito.internal.matchers.InstanceOf;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import execution_service.entity.OrderExecutionMessage;
import execution_service.entity.StockTransaction;
import execution_service.entity.enums.OrderStatus;
import execution_service.entity.enums.OrderType;
import execution_service.requests.AddStockToUserRequest;
import execution_service.requests.NewStockTransactionRequest;
import execution_service.requests.NewWalletTransactionRequest;
import execution_service.requests.UpdateWalletBalance;
import execution_service.response.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderExecutionService {
    private final WebClient.Builder webClientBuilder;
    public void execute(OrderExecutionMessage orderExecutionMessage) {
        try {
            NewWalletTransactionRequest walletTXR = orderExecutionMessage.getNewWalletTransaction();
            NewStockTransactionRequest stockTXR = orderExecutionMessage.getNewStockTransaction();
            AddStockToUserRequest addStockR = orderExecutionMessage.getAddStockToUserRequest();

            // Stock tx and wallet tx must have matching stock and wallet tx ids.
            // Because they are separate requests, we must obtain one of the two ids before creating the transactions.
            if (stockTXR.getWalletTXId() == null && walletTXR != null) {
                Response createWalletTXIdRes = createWalletTXId();
                Long walletTXId = Long.parseLong((String)createWalletTXIdRes.data());
                stockTXR.setWalletTxId(walletTXId);
                walletTXR.setWalletTXId(walletTXId);
            }
           
            Response createStockTXRes = createStockTXRequest(stockTXR);
            Long stockTXId = Long.parseLong((String)createStockTXRes.data());

            if (walletTXR != null) {
                walletTXR.setStockTXId(stockTXId); 
                
                Response createWalletTXRes = createWalletTXRequest(walletTXR);

                // Wallet updates for Market orders are done in matching engine
                if (!(stockTXR.getOrderType() == OrderType.MARKET && stockTXR.isBuy() == false)) {
                    Response updateWalletBalanceRes = updateWalletBalanceRequest(walletTXR);
                }
            }

            if (addStockR != null) {
                if (!(stockTXR.getOrderType() == OrderType.MARKET && stockTXR.isBuy() == false)) {
                    Response addStockToUserRes = addStockToUserRequest(addStockR);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Response createWalletTXId() {
        Response res = webClientBuilder.build().post().uri("http://wallet-service/internal/createWalletTransactionId").retrieve().bodyToMono(Response.class).block();
        return res;
    }
        
    private Response createStockTXRequest(NewStockTransactionRequest stockTXR) {
        Response res = webClientBuilder.build()
                        .post().uri("http://stock-service/internal/createStockTransaction")
                        .bodyValue(stockTXR).retrieve()
                        .bodyToMono(Response.class).block();
        return res;
    }

    private Response addStockToUserRequest(AddStockToUserRequest addStockR) {
        Response res = webClientBuilder.build()
                .post().uri("http://wallet-service/internal/addStockToUser")
                .bodyValue(addStockR).retrieve()
                .bodyToMono(Response.class).block();
        
        return res;
    }

    private Response createWalletTXRequest(NewWalletTransactionRequest walletTXR) {
        Response res = webClientBuilder.build()
                            .post().uri("http://wallet-service/internal/createWalletTransaction")
                            .bodyValue(walletTXR).retrieve()
                            .bodyToMono(Response.class).block();
        return res;
    }

    private Response updateWalletBalanceRequest(NewWalletTransactionRequest walletTXR) {
        Response res = webClientBuilder.build()
                .post().uri("http://wallet-service/internal/updateWalletBalance")
                .bodyValue(new UpdateWalletBalance(walletTXR.getUsername(), walletTXR.getAmount(), walletTXR.isDebit())).retrieve()
                .bodyToMono(Response.class).block(); 
        return res;
    }

}
