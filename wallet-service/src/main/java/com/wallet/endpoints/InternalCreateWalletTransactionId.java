package com.wallet.endpoints;

import com.wallet.models.entity.WalletTX;
import com.wallet.models.entity.WalletTXDbSequence;
import com.wallet.models.request.NewWalletTransactionRequest;
import com.wallet.models.response.Response;
import com.wallet.mongo.repository.WalletTXRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

import java.util.Objects;

@RequiredArgsConstructor
@RestController
public class InternalCreateWalletTransactionId {
    private final MongoOperations mongoOperations;

    @PostMapping( "/internal/createWalletTransactionId")
    public Response createWalletTransactionId() {
        try{
            Long walletTXID = getSequenceNumber(WalletTX.SEQUENCE_NAME);
            return Response.ok(walletTXID.toString());
        }catch(Exception e){
            return Response.error(e.getMessage());
        }
    }


    public Long getSequenceNumber(String sequenceName){
        //get seq number
        Query query = new Query(Criteria.where("id").is(sequenceName));
        //update seq num
        Update update = new Update().inc("seq", 1);
        //modify in doc
        WalletTXDbSequence counter = mongoOperations.findAndModify(query, update, options().returnNew(true).upsert(true), WalletTXDbSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}
