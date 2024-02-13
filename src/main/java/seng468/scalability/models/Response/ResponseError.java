package seng468.scalability.models.Response;

public record ResponseError(String success, Object data, String message) {
    public ResponseError(String message) {
        this("success",null,message);
    }

    public ResponseError(String success, Object data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}


