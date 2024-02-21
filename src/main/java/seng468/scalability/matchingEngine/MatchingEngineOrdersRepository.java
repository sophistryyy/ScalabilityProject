package seng468.scalability.matchingEngine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import  seng468.scalability.models.entity.StockOrder;
@Repository
public interface MatchingEngineOrdersRepository extends JpaRepository<StockOrder, Integer> {


}
