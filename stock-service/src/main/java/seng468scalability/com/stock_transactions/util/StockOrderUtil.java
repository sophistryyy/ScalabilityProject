package seng468scalability.com.stock_transactions.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import seng468scalability.com.portfolio.entity.PortfolioEntry;
import seng468scalability.com.portfolio.entity.PortfolioEntryId;
import seng468scalability.com.portfolio.repository.PortfolioRepository;
import seng468scalability.com.response.Response;
import seng468scalability.com.stock.repositories.StockRepository;
import seng468scalability.com.stock_transactions.entity.NewWalletTransactionRequest;
import seng468scalability.com.stock_transactions.entity.StockTransaction;
import seng468scalability.com.stock_transactions.entity.UpdateWalletBalance;
import seng468scalability.com.stock_transactions.entity.enums.OrderStatus;
import seng468scalability.com.stock_transactions.entity.enums.OrderType;
import seng468scalability.com.stock_transactions.request.PlaceStockOrderRequest;

@RequiredArgsConstructor
@Service
public class StockOrderUtil {

    private final StockTxIdSequenceGenerator generator;
    private final StockRepository stockRepository;
    private final WebClient.Builder webClientBuilder;
    private final PortfolioRepository portfolioRepository;

    public StockTransaction createStockTX(Long stockTXId, Long stockId, Long parentStockTXId, Long walletTXId, boolean isBuy, OrderType orderType, Long quantity, Long price, OrderStatus orderStatus, String username){
        if (stockTXId == null) {
            stockTXId = generator.getSequenceNumber(StockTransaction.SEQUENCE_NAME);
        }
        return new StockTransaction(stockTXId, parentStockTXId, walletTXId, stockId, isBuy, orderType, quantity, price, orderStatus, username);
    }


    public String basicVerifier(PlaceStockOrderRequest req)
    {
        OrderType orderType;
        try {
            orderType = OrderType.valueOf(req.orderType());
        } catch (Exception e) {
            return "Incorrect value of order type";
        }
        if(req.is_buy() == null){
            return "Set 'is_buy' to true or false";
        }
        if (orderType == OrderType.MARKET && req.price() != null) {
            return "MARKET orders can't have price, set it to null.";
        }
        if (orderType == OrderType.LIMIT && (req.price() == null || req.price() <= 0)) {
            return "LIMIT orders' price has to be more than 0";
        }
        if(req.quantity() == null || req.quantity() <= 0){
            return "Please set quantity to more than 0";
        }
        if(req.stock_id() == null){
            return "Null stock id is not allowed";
        }
        Long stockId = req.stock_id();
        if (!stockRepository.existsById(stockId))
        {
            return "Invalid stock Id. It doesn't exist";
        }
        return null;
    }

    public String verifyIfEnough(StockTransaction order, String username)
    {
        if(order.is_buy() && (order.getOrderType() == OrderType.LIMIT))
        {

            Long toDeduct = order.getPrice() * order.getQuantity();
            try {
                Response walletResponse = webClientBuilder.build().post().uri("http://wallet-service/internal/updateWalletBalance")
                        .bodyValue(new UpdateWalletBalance(username, order.getPrice(), true)).retrieve().bodyToMono(Response.class).block();
                if (!walletResponse.success()) {
                    return walletResponse.data().toString();
                }
            }catch(Exception e)
            {
                return "User doesn't have enough money to cover this stock. ";
            }
            //money subtracted
            //create wallet transaction for LIMIT buy orders
            Response walletTXResponse = webClientBuilder.build()
                    .post()
                    .uri("http://wallet-service/internal/createWalletTransaction")
                    .bodyValue(new NewWalletTransactionRequest(null, username, order.getStock_tx_id(), true, toDeduct))
                    .retrieve()
                    .bodyToMono(Response.class).block();

            if(!walletTXResponse.success()){
                return "Failed to create a wallet transaction";
            }

            return walletTXResponse.data().toString();//wallet Tx id

        }

        else if(!order.is_buy())
        {

            return removeStockFromPortfolio(username, order.getStock_id(), order.getQuantity()); //might return error

        }

        return null;
    }

    public String removeStockFromPortfolio(String username, Long stockId, Long quantity ){
        try {
            PortfolioEntry entry = portfolioRepository.findByPortfolioEntryId(new PortfolioEntryId(stockId, username));
            if(entry == null){
                return "User does not have enough of available stock";
            }
            entry.removeQuantity(quantity); //throws exception

            if(entry.getQuantity() == 0){
                portfolioRepository.deleteByPortfolioEntryId_StockId(stockId);
            }else{
                portfolioRepository.save(entry);
            }
            return null;
        }catch(Exception e){
            return e.getMessage();
        }
    }

}
