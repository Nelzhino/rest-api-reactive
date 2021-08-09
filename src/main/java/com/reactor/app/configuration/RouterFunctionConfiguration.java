package com.reactor.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactor.app.handler.ProductoHandler;

@Configuration
public class RouterFunctionConfiguration {
	
	@Bean
	public RouterFunction<ServerResponse> routes(ProductoHandler handler) {
		return RouterFunctions
				.route(RequestPredicates.GET("/api/v2/productos")
						.or(RequestPredicates.GET("/api/v3/productos")), handler::list)
				.andRoute(RequestPredicates.GET("/api/v2/productos/{id}"), handler::show)
				.andRoute(RequestPredicates.POST("/api/v2/productos"), handler::create)
				.andRoute(RequestPredicates.PUT("/api/v2/productos/{id}"), handler::update)
				.andRoute(RequestPredicates.DELETE("/api/v2/productos/{id}"), handler::delete)
				.andRoute(RequestPredicates.POST("/api/v2/productos/upload/{id}"), handler::upload)
				.andRoute(RequestPredicates.POST("/api/v2/productos/crear"), handler::createWithPhotos);
	}
}
