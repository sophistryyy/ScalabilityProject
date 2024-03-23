package com.user.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.user.models.entity.PortfolioEntry;
import com.user.models.response.Response;
import com.user.repositories.PortfolioRepository;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class GetStockPortfolioController {

    @Autowired
    PortfolioRepository portfolioRepository;    

    @GetMapping("/getStockPortfolio")
    public Response getStockPortfolio() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
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
