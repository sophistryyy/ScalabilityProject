package seng468.scalability.com.stock.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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