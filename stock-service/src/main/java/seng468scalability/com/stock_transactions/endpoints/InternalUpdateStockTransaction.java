package seng468scalability.com.stock_transactions.endpoints;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seng468scalability.com.response.Response;

@RequiredArgsConstructor
@RestController
public class InternalUpdateStockTransaction {
    
    @PostMapping("/internal/updateStockTransaction")
    public Response updateStockTransaction() {
        try {
            return Response.ok("");
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
