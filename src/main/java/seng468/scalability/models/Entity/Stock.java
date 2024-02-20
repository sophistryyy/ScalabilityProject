package seng468.scalability.models.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "`stocks`")
public class Stock {
    @Id
    private int id; 
    private String name;

    public Stock() {}

    public Stock(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
