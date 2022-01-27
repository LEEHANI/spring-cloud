package com.example.filter;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

//@Configuration
public class FilterConfig {

//    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/member/**")
                            .filters(f -> f.addRequestHeader("member-request", "member-request")
                                            .addResponseHeader("member-response", "member-response"))
                            .uri("http://localhost:8081"))
                .build();
    }
}
