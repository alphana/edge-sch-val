package com.example.edgeschval.schemavalidation.filter;

import com.example.edgeschval.schemavalidation.domain.SwaggerGroup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@Component
public class SchemaValidationFilter implements GlobalFilter {

    private static final Log logger = LogFactory.getLog(SchemaValidationFilter.class);

    @Autowired
    private RouteLocator routeLocator;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {


        Route targetService = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String targetServiceUrl = targetService.getUri().toString().replace( targetService.getUri().getPath(),"");


        routeLocator.getRoutes();

        WebClient client = WebClient.builder()
                .build();

        return client.get()
                //.uri("https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml")
               .uri(targetServiceUrl+"/v3/api-docs/swagger-config")
                .retrieve()
                .bodyToMono(SwaggerGroup.class)
                .doOnNext(body -> {
                    logger.info(body);
                    Stream<SwaggerGroup> groups = Stream.of(body);

                })
                .then(chain.filter(exchange));


    }


    private String getMicroserviceName(String url) {
        while (url.charAt(0) == '/') {
            url = url.substring(1);
        }

        while (url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, url.length() - 1);
        }

        return url.split("/")[0];
    }
}
