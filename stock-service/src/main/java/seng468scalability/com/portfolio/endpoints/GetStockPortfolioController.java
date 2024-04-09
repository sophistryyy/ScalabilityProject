package seng468scalability.com.portfolio.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import seng468scalability.com.portfolio.entity.PortfolioEntry;
import seng468scalability.com.portfolio.repository.PortfolioRepository;
import seng468scalability.com.response.Response;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GetStockPortfolioController {

    private final PortfolioRepository portfolioRepository;

    @GetMapping("/getStockPortfolio")
    public Response getStockPortfolio(@RequestHeader("X-username") String username) {

        if(username == null || username.isEmpty()){
            return Response.error("Username not found.");
        }
        List<PortfolioEntry> entries = portfolioRepository.findAllByPortfolioEntryId_Username(username);
        List<Map<String, Object>> data = formatData(entries);

        return Response.ok(data);
    }

    private List<Map<String, Object>> formatData(List<PortfolioEntry> entries) {
        List<Map<String, Object>> data = new LinkedList<>();
        for (PortfolioEntry entry : entries) {
            Map<String, Object> entryMap =  new LinkedHashMap<>();
            entryMap.put("stock_id", entry.getPortfolioEntryId().getStockId());
            entryMap.put("stock_name", entry.getStockName());
            entryMap.put("quantity_owned", entry.getQuantity());
            data.add(entryMap);
        }

        return data;
    }
}
