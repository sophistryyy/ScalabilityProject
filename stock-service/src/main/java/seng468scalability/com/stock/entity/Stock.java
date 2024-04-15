package seng468scalability.com.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

//general stocks available
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "stock")
public class Stock {
    @Transient
    public static final String SEQUENCE_NAME= "stock_id_sequence";

    @Id
    private long id;
    @Indexed(unique = true)
    private String name;

    public Stock(String name) {
        this.name = name;
    }

}