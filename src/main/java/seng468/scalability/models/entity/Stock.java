package seng468.scalability.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

//general stocks available
@Entity
@Table(name = "`stocks`")
public class Stock {
    @Id
    // Generate new unique id
    @GeneratedValue(generator = "generatorName")
    @SequenceGenerator(name = "generatorName", sequenceName = "SEQ_NAME")
    private int id; 
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

    public Stock(Integer id, String name, Integer quantity, Integer price)
    {
        this.id = id;
        this.name = name;
    }

<<<<<<< HEAD:src/main/java/seng468/scalability/models/entity/Stock.java
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
=======

    //basic get methods
    public Integer getId(){return this.id;}
    public String getName(){return this.name;}

>>>>>>> d4ab8aa (Update stock class):src/main/java/seng468/scalability/models/Entity/Stock.java
}
