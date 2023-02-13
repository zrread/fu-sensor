package com.fsk.framework.gray.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class GrayFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String hostAddress = request.getRemoteAddress().getAddress().getHostAddress();
        request.mutate().header("GRAY_REMOTE_REQUEST_IP", hostAddress).build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    public static void main(String[] args) {
        System.out.println("10.1.10.68".hashCode() % 100);
        System.out.println(("10.1.10.68".hashCode() & Integer.MAX_VALUE) % 100);

        System.out.println("10.1.32.142".hashCode() % 100);
        System.out.println(("10.1.32.142".hashCode() & Integer.MAX_VALUE) % 100);
    }
}
