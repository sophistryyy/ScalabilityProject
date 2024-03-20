package seng468.scalability.matchingEngine.specialReturnClass;

import seng468.scalability.models.entity.StockOrder;

public class IntOrError {
    private String message;
    private Long walletTXid;

    public IntOrError() {
        this.message = null;
        this.walletTXid = null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getWalletTXid() {
        return walletTXid;
    }

    public void setWalletTXid(Long walletTXid) {
        this.walletTXid = walletTXid;
    }
}
