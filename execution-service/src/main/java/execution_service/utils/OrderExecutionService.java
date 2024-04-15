package execution_service.utils;

import java.util.Map;

import execution_service.requests.*;
import org.mockito.internal.matchers.InstanceOf;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import execution_service.entity.OrderExecutionMessage;
import execution_service.entity.StockTransaction;
import execution_service.entity.enums.OrderStatus;
import execution_service.entity.enums.OrderType;
import execution_service.response.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderExecutionService {
    private final WebClient.Builder webClientBuilder;
    public void execute(OrderExecutionMessage orderExecutionMessage) {
        try {
            if (orderExecutionMessage.isExpired()) {
                handleExpiredTransactions(orderExecutionMessage);
            } else {
                executeOrders(orderExecutionMessage);
            }



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void executeOrders(OrderExecutionMessage orderExecutionMessage) {
        NewWalletTransactionRequest walletTXR = orderExecutionMessage.getNewWalletTransaction();
        NewStockTransactionRequest stockTXR = orderExecutionMessage.getNewStockTransaction();
        InternalUpdateUserStockRequest updateUserStockRequest = orderExecutionMessage.getUpdateUserStockRequest();
        
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
                if (!stockTXR.isBuy()) {
                    Response updateWalletBalanceRes = updateWalletBalanceRequest(walletTXR);
                }
            }

            if (updateUserStockRequest != null) {
                if (stockTXR.isBuy()) {
                    Response updateUserStockRes = updateUserStockRequest(updateUserStockRequest);
                }
            }

            if(stockTXR.getOrderStatus() == OrderStatus.COMPLETED && stockTXR.getPrice() != null && !stockTXR.isBuy()){
                updateStockPrice(stockTXR.getStockId(), stockTXR.getPrice());
            }
    }

    private void handleExpiredTransactions(OrderExecutionMessage orderExecutionMessage) {
        NewWalletTransactionRequest walletTXR = orderExecutionMessage.getNewWalletTransaction();
        NewStockTransactionRequest stockTXR = orderExecutionMessage.getNewStockTransaction();
        InternalUpdateUserStockRequest updateUserStockRequest = orderExecutionMessage.getUpdateUserStockRequest();

        // Check if stocktx inprogress. Delete if so.
        if (stockTXR.getOrderStatus() == OrderStatus.IN_PROGRESS) {
            Response res = webClientBuilder.build()
                            .post().uri("http://stock-service/internal/deleteStockTransaction")
                            .bodyValue(new InternalDeleteStockTXRequest(stockTXR.getStock_tx_id())).retrieve()
                            .bodyToMono(Response.class).block();
        }

        if (walletTXR != null) {
            Response updateWalletBalanceRes = updateWalletBalanceRequest(walletTXR);

            Response deleteWalletTransactionRes = webClientBuilder.build()
                            .post().uri("http://wallet-service/internal/deleteWalletTransaction")
                            .bodyValue(new InternalDeleteWalletTXRequest(walletTXR.getWalletTXId())).retrieve()
                            .bodyToMono(Response.class).block();
        }  

        if (updateUserStockRequest != null) {
            Response updateUserStockRes = updateUserStockRequest(updateUserStockRequest);
        }
    }

    private Response createWalletTXId() {
        Response res = webClientBuilder.build().post().uri("http://wallet-service/internal/createWalletTransactionId").retrieve().
                bodyToMono(Response.class).block();
        return res;
    }

    private void updateStockPrice(Long stockId, Long price){
        webClientBuilder.build().post().uri("http://stock-service/internal/updateStockPrices").
                bodyValue(new UpdateStockPricesRequest(stockId, price)).retrieve().bodyToMono(Void.class).block();
    }
        
    private Response createStockTXRequest(NewStockTransactionRequest stockTXR) {
        Response res = webClientBuilder.build()
                        .post().uri("http://stock-service/internal/createStockTransaction")
                        .bodyValue(stockTXR).retrieve()
                        .bodyToMono(Response.class).block();
        return res;
    }

    private Response updateUserStockRequest(InternalUpdateUserStockRequest updateUserStockRequest) {
        Response res = webClientBuilder.build()
                .post().uri("http://stock-service/internal/updateUserStock")
                .bodyValue(updateUserStockRequest).retrieve()
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