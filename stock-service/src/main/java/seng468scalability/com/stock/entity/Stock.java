package seng468scalability.com.stock.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

//general stocks available
@Getter
@NoArgsConstructor
@Entity
@Table(name = "`stocks`")
public class Stock {
    @Id
    @SequenceGenerator(
            name = "stocks_sequence",
            sequenceName = "stocks_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "stocks_sequence"
    )
    private long id;
    private String name;

    public Stock(String name) {
        this.name = name;
    }

}