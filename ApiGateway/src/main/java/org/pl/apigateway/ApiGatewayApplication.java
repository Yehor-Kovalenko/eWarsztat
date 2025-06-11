package org.pl.apigateway;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.WebFilter;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    @Autowired
    private EurekaClient discoveryClient;

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

//    @Bean
//    public RouterFunction<ServerResponse> getRoute() {
//        // todo: all routes from properties file
//        return route()
//                .GET("/hello", request -> ServerResponse.ok().body("Hello from ApiGateway"))
//                .build();
//    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routes = builder.routes();

        try {
            String staffServiceUrl = discoveryClient.getNextServerFromEureka("STAFF-SERVICE", false).getHomePageUrl();
            routes.route("staff-service", r -> r.path("/staff-service/**")
                    .filters(f -> f.stripPrefix(1))
                    .uri(staffServiceUrl));
        } catch (Exception e) {
            System.out.println("STAFF-SERVICE not available: " + e.getMessage());
        }

        try {
            String clientServiceUrl = discoveryClient.getNextServerFromEureka("CLIENT-SERVICE", false).getHomePageUrl();
            routes.route("client-service", r -> r.path("/client-service/**")
                    .filters(f -> f.stripPrefix(1))
                    .uri(clientServiceUrl));
        } catch (Exception e) {
            System.out.println("CLIENT-SERVICE not available: " + e.getMessage());
        }

        try {
            String paymentServiceUrl = discoveryClient.getNextServerFromEureka("PAYMENT-SERVICE", false).getHomePageUrl();
            routes.route("payment-service", r -> r.path("/payments-service/**")
                    .filters(f -> f.stripPrefix(1))
                    .uri(paymentServiceUrl));
        } catch (Exception e) {
            System.out.println("PAYMENT-SERVICE not available: " + e.getMessage());
        }

        try {
            String securityServiceUrl = discoveryClient.getNextServerFromEureka("SECURITY-SERVICE", false).getHomePageUrl();
            routes.route("security-service", r -> r.path("/security-service/**")
                    .filters(f -> f.stripPrefix(1))
                    .uri(securityServiceUrl));
        } catch (Exception e) {
            System.out.println("SECURITY-SERVICE not available: " + e.getMessage());
        }

        return routes.build();
    }


}
