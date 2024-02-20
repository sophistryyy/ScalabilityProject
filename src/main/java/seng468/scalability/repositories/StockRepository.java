package seng468.scalability.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import seng468.scalability.models.Entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    public Stock findStockById(int id);
}