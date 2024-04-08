package com.user.endpoints;

import com.user.models.entity.PortfolioEntry;
import com.user.models.request.RemoveStockRequest;
import com.user.models.response.Response;
import com.user.repositories.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalRemoveStockUserController {

    private final PortfolioRepository portfolioRepository;

    @PostMapping(path = "/internal/removeStockFromUser")
    public String removeStockFromUser(@RequestBody RemoveStockRequest req){
        try {
            PortfolioEntry entry = portfolioRepository.findEntryByStockIdAndUsername(req.stock_id(), req.username());
            if(entry == null){
                return "User does not have enough of available stock";
            }
            entry.removeQuantity(req.amount());
            if(entry.getQuantity() == 0){
                portfolioRepository.deleteByStockId(req.stock_id());
            }else{
                portfolioRepository.save(entry);
            }
            return null;
        }catch(Exception e){
            return e.getMessage();
        }

    }
}
