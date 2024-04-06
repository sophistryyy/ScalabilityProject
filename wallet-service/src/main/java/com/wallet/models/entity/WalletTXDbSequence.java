package com.wallet.models.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "wallet_tx_id_sequence")
public class WalletTXDbSequence {
    @Id
    private String id;
    private Long seq;
}
