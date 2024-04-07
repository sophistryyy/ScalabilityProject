package com.apigateway.filter;

import com.apigateway.request.AuthRequest;
import com.apigateway.request.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {


    private final RouteValidator validator;
    private final WebClient.Builder webClientBuilder;

    public AuthenticationFilter(RouteValidator validator, WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.validator = validator;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if(validator.isSecured.test(request)){
                if (validator.restrictedEndpoints.stream().anyMatch(request.getURI().getPath()::contains)) {
                    // Endpoint is restricted
                    return errorResponse(exchange, HttpStatus.FORBIDDEN, "Access to this endpoint is forbidden");
                }
                //header contains token or not

                if(!request.getHeaders().containsKey("token")){
                    return errorResponse(exchange, HttpStatus.BAD_REQUEST, "Missing token header");
                }

                String authHeader = request.getHeaders().get("token").get(0);
                if(authHeader != null){
                    try{//Rest Call to /validate
                        Response authResponse = webClientBuilder.build().post().uri("http://user-service/validate")
                                .bodyValue(new AuthRequest(authHeader)).retrieve().bodyToMono(Response.class).block();
                        if(!authResponse.success()){
                            return errorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token is not correct.");
                        }

                        //success
                        String username = authResponse.data().toString();
                        ServerHttpRequest modifiedRequest = exchange.getRequest()
                                .mutate()
                                .header("X-Username", username)  // Add username as a custom header
                                .build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());

                    }catch(Exception e){
                        return errorResponse(exchange, HttpStatus.BAD_REQUEST, "Error with request to token validation (1). " + e.getMessage());
                    }
                }else{
                    return errorResponse(exchange, HttpStatus.BAD_REQUEST, "Error with request to token validation or no token was provided(2).");
                }

            }
            return chain.filter(exchange);
        }));
    }

    public static class Config{

    }

    private Mono<Void> errorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String responseBody = String.format("{\"success\": false, \"data\": \"%s\"}", message);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes())));
    }
}
