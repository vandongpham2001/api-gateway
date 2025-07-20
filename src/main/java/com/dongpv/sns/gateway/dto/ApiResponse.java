package com.dongpv.sns.gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> implements BaseApiResponse {
    int code;
    boolean status;
    String message;
    T data;

    /**
     * Convenience method to create a success response with code
     */
    public static <T> ApiResponse<T> ok(int code) {
        return ApiResponse.<T>builder()
                .code(code)
                .status(true)
                .build();
    }

    /**
     * Convenience method to create a success response with message
     */
    public static <T> ApiResponse<T> ok(String message) {
        return ApiResponse.<T>builder()
                .code(200)
                .status(true)
                .message(message)
                .build();
    }

    /**
     * Convenience method to create a success response with data
     */
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .status(true)
                .data(data)
                .build();
    }

    /**
     * Convenience method to create a success response with data and code
     */
    public static <T> ApiResponse<T> ok(T data, int code) {
        return ApiResponse.<T>builder()
                .code(code)
                .status(true)
                .data(data)
                .build();
    }

    /**
     * Convenience method to create a success response with data and message
     */
    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .code(200)
                .status(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Convenience method to create a success response with code and message
     */
    public static <T> ApiResponse<T> ok(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .status(true)
                .message(message)
                .build();
    }

    /**
     * Convenience method to create a success response with data, code and message
     */
    public static <T> ApiResponse<T> ok(T data, int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .status(true)
                .message(message)
                .data(data)
                .build();
    }
}
