package com.user.endpoints;

import com.user.models.request.StockInfoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.user.models.entity.PortfolioEntry;
import com.user.models.request.AddStockToUserRequest;
import com.user.models.response.Response;
import com.user.repositories.PortfolioRepository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;

@RequiredArgsConstructor
@RestController
@Slf4j
public class AddStockToUserController {
    

    private final WebClient.Builder webClientBuilder;
    private final PortfolioRepository portfolioRepository;

    @PostMapping("/addStockToUser")
    public Response addStockToUser(@RequestBody AddStockToUserRequest req, @RequestHeader("X-username") String username) {

        if(req.stockId() == null || req.stockId() <= 0 || req.quantity() == null || req.quantity() <= 0){
            return Response.error("Invalid parameter. Either null, 0 or negative number");
        }
        Response stockResponse = webClientBuilder.build().post().uri("http://stock-service/internal/getStockInfo")
                .bodyValue(new StockInfoRequest(req.stockId())).retrieve().bodyToMono(Response.class).block();//synchronous request

        if (stockResponse == null || !stockResponse.success() || !(stockResponse.data() instanceof LinkedHashMap<?, ?>)) {
            // Retry logic or handle the error
            return Response.error("Invalid Stock Id");
        }


        PortfolioEntry entry = portfolioRepository.findEntryByStockIdAndUsername(req.stockId(), username);
        if (entry == null) {
            LinkedHashMap<String, String> responseData = (LinkedHashMap<String, String>) stockResponse.data();//not sure if there is a better way to do this
            String stockName = responseData.get("name");

            entry = new PortfolioEntry(req.stockId(), stockName, username, req.quantity());
        } else {
            entry.addQuantity(req.quantity());
        }

        portfolioRepository.save(entry);

        return Response.ok(null);
    }


}
