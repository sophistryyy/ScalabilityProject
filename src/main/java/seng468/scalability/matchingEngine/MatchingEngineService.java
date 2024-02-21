package seng468.scalability.matchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import seng468.scalability.models.Entity.StockOrder;

import java.util.List;

@Service
public class MatchingEngineService {

    private final MatchingEngineOrdersRepository repository;

    @Autowired
    public MatchingEngineService(MatchingEngineOrdersRepository repository){
        this.repository = repository;
    }

    public void test()
    {
        List<StockOrder> res = repository.findAll();
        System.out.println(res);
    }
}
