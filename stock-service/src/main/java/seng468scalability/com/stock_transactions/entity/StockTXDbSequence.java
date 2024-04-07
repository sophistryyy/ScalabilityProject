package seng468scalability.com.stock_transactions.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "stock_tx_id_sequence")
public class StockTXDbSequence {
    @Id
    private String id;
    private Long seq;
}
