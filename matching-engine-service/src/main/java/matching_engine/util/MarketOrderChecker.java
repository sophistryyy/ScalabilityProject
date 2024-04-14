package matching_engine.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching_engine.requests.MarketOrderHandlerResponse;
import matching_engine.requests.UpdateWalletBalanced;
import matching_engine.response.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class MarketOrderChecker {
    private final WebClient.Builder webClientBuilder;

    public MarketOrderHandlerResponse checkIfMarketOrderHasEnough(String username, Long sellingPrice, Long sellingQuantity) {
        try {
            Response walletResponse = webClientBuilder.build().get().uri("http://wallet-service/getWalletBalance")
                    .header("X-Username", username)
                    .retrieve().bodyToMono(Response.class).block();
            if (!walletResponse.success() || !(walletResponse.data() instanceof Map<?, ?>)) {
                log.info("Error getting wallet balance from the Matching Engine. " + walletResponse.data());
                return new MarketOrderHandlerResponse(false,  null);
            }

            Integer walletBalance = ((Map<String, Integer>) walletResponse.data()).get("balance");
            if (walletBalance < sellingPrice) {//user doesn't have enough even to buy 1
                // buy order couldn't be matched
                return new MarketOrderHandlerResponse(false,  null);
            }
            Long buyerCanAffordQuantity = (long) Math.floor((double) walletBalance / sellingPrice);
            Long buyingStocks = 0L;
            if (buyerCanAffordQuantity < sellingQuantity)//buyer can afford less than asked
            {
                buyingStocks = buyerCanAffordQuantity;
            }//otherwise buyer can afford all
            else {
                buyingStocks = sellingQuantity;
            }
            Long toDeduct = sellingPrice * buyingStocks;
            Response walletDecrementResponse = webClientBuilder.build().post().uri("http://wallet-service/internal/updateWalletBalance")
                    .bodyValue(new UpdateWalletBalanced(username, toDeduct, true))
                    .retrieve().bodyToMono(Response.class).block();
            if (!walletDecrementResponse.success()) {
                log.info("Error decrementing wallet balance from the Matching Engine. " + walletResponse.data());
                return new MarketOrderHandlerResponse(false,  null);
            } else {
                //success
                return new MarketOrderHandlerResponse(true,  buyingStocks);
            }
        } catch (Exception e) {
            log.info("Error working with wallet balance from the Matching Engine. " + e.getMessage());
            return new MarketOrderHandlerResponse(false, null);
        }
    }
}
