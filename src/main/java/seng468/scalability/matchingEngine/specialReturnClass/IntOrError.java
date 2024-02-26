package seng468.scalability.matchingEngine.specialReturnClass;

import seng468.scalability.models.entity.StockOrder;

public class IntOrError {
    private String message;
    private Integer walletTXid;

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

    public Integer getWalletTXid() {
        return walletTXid;
    }

    public void setWalletTXid(Integer walletTXid) {
        this.walletTXid = walletTXid;
    }
}
