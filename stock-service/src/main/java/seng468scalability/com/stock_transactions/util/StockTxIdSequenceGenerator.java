package seng468scalability.com.stock_transactions.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import seng468scalability.com.stock_transactions.entity.StockTXDbSequence;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@RequiredArgsConstructor
@Component
public class StockTxIdSequenceGenerator {

    private final MongoOperations mongoOperations;

    public Long getSequenceNumber(String sequenceName){
        //get seq number
        Query query = new Query(Criteria.where("id").is(sequenceName));
        //update seq num
        Update update = new Update().inc("seq", 1);
        //modify in doc
        StockTXDbSequence counter = mongoOperations.findAndModify(query, update, options().returnNew(true).upsert(true), StockTXDbSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}
