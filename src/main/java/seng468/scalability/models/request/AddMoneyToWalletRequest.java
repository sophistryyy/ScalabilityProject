package seng468.scalability.models.request;

public class AddMoneyToWalletRequest {
    Integer amount;

    public AddMoneyToWalletRequest(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return this.amount;
    }
}
