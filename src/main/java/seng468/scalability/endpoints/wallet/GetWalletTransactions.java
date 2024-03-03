package seng468.scalability.endpoints.wallet;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import seng468.scalability.models.entity.WalletTX;
import seng468.scalability.models.response.Response;
import seng468.scalability.repositories.WalletTXRepository;

@RestController
public class GetWalletTransactions {

    @Autowired
    private WalletTXRepository walletTXRepository;

    @GetMapping("getWalletTransactions")
    public Response getWalletTransactions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        List<WalletTX> entries = walletTXRepository.findAllByUsername(username);
        List<Map<String, Object>> data = fromatData(entries);
        return Response.ok(data);
    }

    private List<Map<String, Object>> fromatData(List<WalletTX> entries) {
        List<Map<String, Object>> data = new LinkedList<>();
        for (WalletTX entry : entries) {
            Map<String, Object> entryMap =  new LinkedHashMap<>();

            entryMap.put("wallet_tx_id", entry.getWalletTXId());
            entryMap.put("stock_tx_id", entry.getStockTXId());
            entryMap.put("is_debit", entry.getIsDebit());
            entryMap.put("amount", entry.getAmount());
            entryMap.put("time_stamp", entry.getTimestamp());
            data.add(entryMap);
        }

        return data;
    }

}

