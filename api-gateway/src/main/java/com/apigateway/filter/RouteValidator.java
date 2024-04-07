package com.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    //endpoints that don't require token. Remove /eureka later
    public static final List<String> openApiEndpoints = List.of(
            "/register",
            "/login",
            "/eureka"
    );

    public final List<String> restrictedEndpoints = List.of(
            "/internal"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
