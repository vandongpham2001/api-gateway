package com.dongpv.sns.gateway.service;

import com.dongpv.sns.gateway.dto.ApiResponse;
import com.dongpv.sns.gateway.dto.request.IntrospectRequestDto;
import com.dongpv.sns.gateway.dto.response.IntrospectResponseDto;
import com.dongpv.sns.gateway.repository.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    public Mono<ApiResponse<IntrospectResponseDto>> introspect(String token){
        return identityClient.introspect(IntrospectRequestDto.builder()
                        .token(token)
                .build());
    }
}
