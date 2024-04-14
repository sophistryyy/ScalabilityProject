package matching_engine.requests;

public record MarketOrderHandlerResponse(Boolean success,
                                         Long buyingStocks){
}
