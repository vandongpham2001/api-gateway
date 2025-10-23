package com.dongpv.sns.gateway.configuration;

import static com.dongpv.sns.gateway.constant.CommonConstant.*;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import com.dongpv.sns.gateway.code.ErrorCode;
import com.dongpv.sns.gateway.dto.MultiRecordErrorResponseDtoBase;
import com.dongpv.sns.gateway.exception.JsonSerializationException;
import com.dongpv.sns.gateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    IdentityService identityService;
    ObjectMapper objectMapper;

    private static final String[] PUBLIC_ENDPOINTS = {
        "/identity/auth/.*", "/identity/users/registration", "/notification/email/send", "/file/media/download/.*"
    };

    @Value("${app.api-prefix}")
    @NonFinal
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LOGGER.info("Enter authentication filter....");

        if (isPublicEndpoint(exchange.getRequest())) return chain.filter(exchange);

        // Get token from authorization header
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader)) return unauthenticated(exchange.getResponse());

        String token = authHeader.getFirst().replace(BEARER, EMPTY);
        LOGGER.info("Token: {}", token);

        return identityService
                .introspect(token)
                .flatMap(introspectResponse -> {
                    if (introspectResponse.getData().isValid()) return chain.filter(exchange);
                    else return unauthenticated(exchange.getResponse());
                })
                .onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        return Arrays.stream(PUBLIC_ENDPOINTS)
                .anyMatch(s -> request.getURI().getPath().matches(apiPrefix + s));
    }

    Mono<Void> unauthenticated(ServerHttpResponse response) {
        MultiRecordErrorResponseDtoBase apiResponse = new MultiRecordErrorResponseDtoBase(
                ErrorCode.UNAUTHENTICATED.getCode(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        apiResponse.addDetail(KEY_EXCEPTION, ErrorCode.UNAUTHENTICATED.getMessage());
        String body;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException(
                    "Failed to serialize api response", ErrorCode.INTERNAL_SERVER_ERROR, e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
