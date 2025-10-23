package com.dongpv.sns.gateway.service;

import org.springframework.stereotype.Service;

import com.dongpv.sns.gateway.dto.ApiResponse;
import com.dongpv.sns.gateway.dto.request.auth.IntrospectRequestDto;
import com.dongpv.sns.gateway.dto.response.auth.IntrospectResponseDto;
import com.dongpv.sns.gateway.repository.IdentityClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    public Mono<ApiResponse<IntrospectResponseDto>> introspect(String token) {
        return identityClient.introspect(
                IntrospectRequestDto.builder().token(token).build());
    }
}
