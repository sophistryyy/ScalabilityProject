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
    private Integer id;
    private String name;
    //not sure if needed
    private Integer quantity;
    private Integer price;

    public Stock() {}

    public Stock(String name) {
        this.name = name;
    }

    public Stock(Integer id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Integer getQuantity(){return this.quantity;}
    public Integer getPrice(){return this.price;}

    //basic set methods
    public void setPrice(Integer price) {this.price = price;}
    public void setQuantity(Integer quantity) {this.quantity = quantity;}
}
g