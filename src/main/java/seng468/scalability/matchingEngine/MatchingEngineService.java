package seng468.scalability.matchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
public class MatchingEngineService {

    private final MatchingEngineOrdersRepository repository;

    @Autowired
    public MatchingEngineService(MatchingEngineOrdersRepository repository){
        this.repository = repository;
    }

    public void test()
    {
        System.out.println(repository.findAll());
    }
}
