package com.firewall.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOriginPattern("*");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("DELETE");
		config.addAllowedMethod("OPTIONS");
		config.addAllowedMethod("PATCH");
		config.addAllowedHeader("*");
		config.addAllowedHeader("X-Forwarded-Host");
		config.addAllowedHeader("x-forwarded-host");
		config.setAllowCredentials(false);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsWebFilter(source);
	}

	@Bean
	public RouteLocator customRouteLocator(
			RouteLocatorBuilder builder,
			@Value("${services.usuarios.url:http://ms-usuarios:8084}") String usuariosUrl,
			@Value("${services.reportes.url:http://ms-reportes:8081}") String reportesUrl,
			@Value("${services.geolocalizacion.url:http://ms-geolocalizacion:8083}") String geolocalizacionUrl,
			@Value("${services.alertas.url:http://ms-alertas:8082}") String alertasUrl) {
		return builder.routes()
			.route("ms_reportes_api_exact", r -> r.path("/api/reports")
				.filters(f -> f.rewritePath("/api/reports", "/api/reportes"))
				.uri(reportesUrl))
			.route("ms_geolocation_api_exact", r -> r.path("/api/geolocation")
				.filters(f -> f.rewritePath("/api/geolocation", "/api/monitoreo"))
				.uri(geolocalizacionUrl))
			.route("ms_usuarios_api", r -> r.path("/api/usuarios/**")
				.uri(usuariosUrl))
			.route("ms_usuarios_auth", r -> r.path("/auth/**")
				.uri(usuariosUrl))
			.route("ms_reportes_api", r -> r.path("/api/reports/**")
				.filters(f -> f.rewritePath("^/api/reports/(?<segment>.*)", "/api/reportes/${segment}"))
				.uri(reportesUrl))
			.route("ms_geolocation_api", r -> r.path("/api/geolocation/**")
				.filters(f -> f.rewritePath("^/api/geolocation/(?<segment>.*)", "/api/monitoreo/${segment}"))
				.uri(geolocalizacionUrl))
			.route("firewall_alerta_api", r -> r.path("/alerts/**")
				.uri(alertasUrl))
			.build();
	}
}
