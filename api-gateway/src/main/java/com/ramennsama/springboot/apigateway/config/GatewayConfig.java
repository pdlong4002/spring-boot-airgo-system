package com.ramennsama.springboot.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

        private final AuthFilter filter;

        @Bean
        public KeyResolver userKeyResolver() {
                return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        }

        @Bean
        @Primary
        public RedisRateLimiter authRateLimiter() {
                return new RedisRateLimiter(10, 20);
        }

        @Bean //test
        public RedisRateLimiter seatRateLimiter() {
                return new RedisRateLimiter(3, 5);
        }

        @Bean
        public RedisRateLimiter flightRateLimiter() {
                return new RedisRateLimiter(5, 10);
        }

        @Bean
        public RedisRateLimiter bookingRateLimiter() {
                return new RedisRateLimiter(2, 5);
        }

        @Bean // test trên gg mới dc ratelimit
        public RouteLocator routes(RouteLocatorBuilder builder) {
                return builder.routes()
                                .route("auth-service", r -> r.path("/api/v1/auth/**")
                                                .filters(f -> f.requestRateLimiter(
                                                                c -> c.setRateLimiter(authRateLimiter())
                                                                                .setKeyResolver(userKeyResolver())))
                                                .uri("lb://auth-service"))

                                .route("flight-service-public", r -> r.path("/api/v1/flights/**")
                                                .and().method("GET")
                                                .filters(f -> f.requestRateLimiter(
                                                                c -> c.setRateLimiter(flightRateLimiter())
                                                                                .setKeyResolver(userKeyResolver())))
                                                .uri("lb://flight-service"))

                                .route("flight-service-protected", r -> r.path("/api/v1/flights/**")
                                                .and().method("POST", "PUT", "DELETE")
                                                .filters(f -> f.filter(filter)
                                                                .requestRateLimiter(
                                                                                c -> c.setRateLimiter(flightRateLimiter())
                                                                                                .setKeyResolver(userKeyResolver())))
                                                .uri("lb://flight-service"))

                                .route("seat-service-public", r -> r.path("/api/v1/seats/**")
                                                .and().method("GET")
                                                .filters(f -> f.requestRateLimiter(
                                                                c -> c.setRateLimiter(seatRateLimiter())
                                                                                .setKeyResolver(userKeyResolver())))
                                                .uri("lb://seat-service"))

                                .route("seat-service-protected", r -> r.path("/api/v1/seats/**")
                                                .and().method("POST", "PUT", "DELETE")
                                                .filters(f -> f.filter(filter)
                                                                .requestRateLimiter(
                                                                                c -> c.setRateLimiter(seatRateLimiter())
                                                                                                .setKeyResolver(userKeyResolver())))
                                                .uri("lb://seat-service"))

                                .route("booking-service", r -> r.path("/api/v1/bookings/**")
                                                .filters(f -> f.filter(filter)
                                                                .requestRateLimiter(
                                                                                c -> c.setRateLimiter(
                                                                                                bookingRateLimiter())
                                                                                                .setKeyResolver(userKeyResolver())))
                                                .uri("lb://booking-service"))

                                .route("payment-service", r -> r.path("/api/v1/payments/**", "/api/v1/vnpay/**")
                                                .uri("lb://payment-service"))

                                .build();
        }
}
