package seng468.scalability.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "`stocks`")
public class Stock {
    @Id
    // Generate new unique id
    @GeneratedValue(generator = "generatorName")
    @SequenceGenerator(name = "generatorName", sequenceName = "SEQ_NAME")
    private int id; 
    private String name;

    public Stock() {}

    public Stock(String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
g