package com.dongpv.sns.gateway.configuration;

import static com.dongpv.sns.gateway.constant.CorsConstant.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.dongpv.sns.gateway.repository.IdentityClient;

@Configuration
public class WebClientConfiguration {

    @Value("${app.services.identity.url}")
    private String identityBaseUrl;

    @Bean
    WebClient webClient() {
        return WebClient.builder().baseUrl(identityBaseUrl).build();
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of(ALLOWED_ORIGIN));
        corsConfiguration.setAllowedHeaders(List.of(ALLOWED_HEADER));
        corsConfiguration.setAllowedMethods(List.of(ALLOWED_METHOD));

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration(CORS_CONFIGURATION_PATTERN, corsConfiguration);

        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }

    @Bean
    IdentityClient identityClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        WebClientAdapter.create(webClient))
                .build();

        return httpServiceProxyFactory.createClient(IdentityClient.class);
    }
}
