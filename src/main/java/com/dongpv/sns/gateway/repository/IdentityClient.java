package com.dongpv.sns.gateway.repository;

import com.dongpv.sns.gateway.dto.ApiResponse;
import com.dongpv.sns.gateway.dto.request.IntrospectRequestDto;
import com.dongpv.sns.gateway.dto.response.IntrospectResponseDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IdentityClient {
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<IntrospectResponseDto>> introspect(@RequestBody IntrospectRequestDto request);
}
