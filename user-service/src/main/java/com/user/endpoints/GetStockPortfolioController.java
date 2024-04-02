package com.user.endpoints;

import com.user.entity.PortfolioEntry;
import com.user.repositories.PortfolioRepository;
import com.user.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
        List<PortfolioEntry> entries = portfolioRepository.findAllByUsername(username);

        List<Map<String, Object>> data = formatData(entries);

        return Response.ok(data);
    }

    private List<Map<String, Object>> formatData(List<PortfolioEntry> entries) {
        List<Map<String, Object>> data = new LinkedList<>();
        for (PortfolioEntry entry : entries) {
            Map<String, Object> entryMap =  new LinkedHashMap<>();
            entryMap.put("stock_id", entry.getStockId());
            entryMap.put("stock_name", entry.getStockName());
            entryMap.put("quantity_owned", entry.getQuantity());
            data.add(entryMap);
        }

        return data;
    }
}
