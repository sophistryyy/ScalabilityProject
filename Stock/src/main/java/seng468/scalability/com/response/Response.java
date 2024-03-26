package seng468.scalability.com.response;

import java.util.HashMap;
import java.util.Map;

public record Response(Boolean success, Object data) {
    public static Response ok(Object data) {
        return new Response(true, data);
    }

    public static Response error(String message) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("error", message);
        return new Response(false, data);
    }
}