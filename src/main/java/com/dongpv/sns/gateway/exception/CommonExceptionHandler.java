package com.dongpv.sns.gateway.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.xml.bind.UnmarshalException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import com.dongpv.sns.gateway.code.ErrorCode;
import com.dongpv.sns.gateway.dto.BaseApiResponse;
import com.dongpv.sns.gateway.dto.MultiRecordErrorResponseDtoBase;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DongPV
 */
@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    private static final String KEY_EXCEPTION = "exception";
    private static final String LOG_TEMPLATE = "{}::{}() - {}";
    private static final String ATTRIBUTE_MIN = "min";
    private static final String PLACEHOLDER_PATTERN = "{" + ATTRIBUTE_MIN + "}";

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final BaseApiResponse handleMultipartException(MultipartException ex) {
        final MultiRecordErrorResponseDtoBase error = new MultiRecordErrorResponseDtoBase(
                ErrorCode.INVALID_FIELD.getCode(), HttpStatus.BAD_REQUEST.getReasonPhrase());

        error.addRecordDetail(KEY_EXCEPTION, ex.getLocalizedMessage());
        return error;
    }

    @ExceptionHandler(UnmarshalException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleUnmarshalException(UnmarshalException ex) {
        LOGGER.error(
                LOG_TEMPLATE,
                getClass().getSimpleName(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                ex.getMessage());
        return getResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        LOGGER.error("Unhandled exception occurred", ex);
        return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        final MultiRecordErrorResponseDtoBase response =
                new MultiRecordErrorResponseDtoBase(status.value(), status.getReasonPhrase());
        setFieldErrors(ex.getBindingResult(), response);

        return new ResponseEntity<>(response, new HttpHeaders(), status);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return getResponseEntity(status, ex.getLocalizedMessage(), new HttpHeaders());
    }

    private final ResponseEntity<Object> getResponseEntity(HttpStatus status, String message) {
        return getResponseEntity(status, message, new HttpHeaders());
    }

    private final ResponseEntity<Object> getResponseEntity(HttpStatus status, String message, HttpHeaders headers) {
        final MultiRecordErrorResponseDtoBase response =
                new MultiRecordErrorResponseDtoBase(status.value(), status.getReasonPhrase());

        response.addFirstRecordDetail(KEY_EXCEPTION, message);
        return new ResponseEntity<>(response, headers, status);
    }

    private void setFieldErrors(BindingResult bindingResult, MultiRecordErrorResponseDtoBase response) {

        // Handle validate field level
        for (final FieldError fieldError : bindingResult.getFieldErrors()) {
            final String field = fieldError.getField();
            final String objectName = fieldError.getObjectName();
            String message = fieldError.getDefaultMessage();

            if (message != null) {
                ErrorCode errorCode = resolveErrorCode(message);
                Integer minValue = extractMinValue(fieldError);

                message = errorCode != null ? errorCode.getMessage() : message;
                if (minValue != null) {
                    message = message.replace(PLACEHOLDER_PATTERN, String.valueOf(minValue));
                }
                message = message.replaceAll(field + " ", "");
            }

            if (objectName.contains("_")) {
                message = objectName + " " + message;
            }

            response.addFirstRecordDetail(field, message);
        }
    }

    private ErrorCode resolveErrorCode(String message) {
        try {
            return ErrorCode.valueOf(message);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid error code from message", e);
            return null;
        }
    }

    private Integer extractMinValue(FieldError fieldError) {
        try {
            ConstraintViolation<?> violation = fieldError.unwrap(ConstraintViolation.class);
            Object minAttr = violation.getConstraintDescriptor().getAttributes().get(ATTRIBUTE_MIN);
            if (minAttr instanceof Integer integer) {
                return integer;
            }
        } catch (Exception e) {
            LOGGER.warn("Can't get min value from annotation", e);
        }
        return null;
    }
}
