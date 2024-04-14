package execution_service.utils;

import java.util.Map;

import org.mockito.internal.matchers.InstanceOf;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import execution_service.entity.OrderExecutionMessage;
import execution_service.entity.StockTransaction;
import execution_service.entity.enums.OrderStatus;
import execution_service.requests.NewStockTransactionRequest;
import execution_service.requests.NewWalletTransactionRequest;
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
            if (stockTXR.getWalletTXId() == null) {
                Response walletTXIdRes = webClientBuilder.build().post().uri("http://wallet-service/internal/createWalletTransactionId").retrieve().bodyToMono(Response.class).block();
                Long walletTXId = Long.parseLong((String)walletTXIdRes.data());
                stockTXR.setWalletTxId(walletTXId);
            }
            
            Response createStockTXRes = webClientBuilder.build()
                            .post().uri("http://stock-service/internal/createStockTransaction")
                            .bodyValue(stockTXR).retrieve()
                            .bodyToMono(Response.class).block();
            Long stockTXId = Long.parseLong((String)createStockTXRes.data());

        
            // // Only COMPLETED need a new wallet transaction?
            if (walletTXR != null) {
                Long walletTXId = stockTXR.getWalletTXId();
                walletTXR.setWalletTXId(walletTXId);
                walletTXR.setWalletTXId(stockTXId); 
                Response createWalletTXRes = webClientBuilder.build()
                                 .post().uri("http://wallet-service/internal/createWalletTransaction")
                                 .bodyValue(walletTXR).retrieve()
                                 .bodyToMono(Response.class).block();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
