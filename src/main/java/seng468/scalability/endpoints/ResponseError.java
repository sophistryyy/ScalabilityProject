package seng468.scalability.endpoints;

public record ResponseError(String success, Object data, String message) {
    public ResponseError(String success, Object data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}


