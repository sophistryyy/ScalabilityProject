package com.wallet.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.wallet.models.entity.WalletTX;
import com.wallet.models.response.Response;
import com.wallet.repositories.WalletTXRepository;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GetWalletTransactions {

    private final WalletTXRepository walletTXRepository;

    @GetMapping("getWalletTransactions")
    public Response getWalletTransactions(@RequestHeader("X-username") String username) {
        if(username == null || username.isEmpty()){
            return Response.error("Username not found.");
        }
        List<WalletTX> entries = walletTXRepository.findAllByUsername(username);
        List<Map<String, Object>> data = formatData(entries);
        return Response.ok(data);
    }

    private List<Map<String, Object>> formatData(List<WalletTX> entries) {
        List<Map<String, Object>> data = new LinkedList<>();
        for (WalletTX entry : entries) {
            Map<String, Object> entryMap =  new LinkedHashMap<>();

            entryMap.put("wallet_tx_id", entry.getWalletTXId());
            entryMap.put("stock_tx_id", entry.getStockTXId());
            entryMap.put("is_debit", entry.isDebit());
            entryMap.put("amount", entry.getAmount());
            entryMap.put("time_stamp", entry.getTimestamp());
            data.add(entryMap);
        }

        return data;
    }

}

