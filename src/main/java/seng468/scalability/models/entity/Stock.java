package seng468.scalability.models.entity;

import jakarta.persistence.*;

//general stocks available
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

    public Stock() {}

    public Stock(String name) {
        this.name = name;
    }

    public Stock(Long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}