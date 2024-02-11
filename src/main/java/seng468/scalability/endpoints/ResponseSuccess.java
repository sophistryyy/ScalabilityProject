package seng468.scalability.endpoints;

public record ResponseSuccess(String success, Object data) {
    public ResponseSuccess(String success, Object data) {
        this.success = success;
        this.data = data;
    }
}
