package org.dnd.modutimer.common.exception;


import org.dnd.modutimer.common.utils.ApiUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> handleApiException(ApiException e) {

        return new ResponseEntity<>(e.body(), e.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> message = new HashMap<>();
        message.put("error", e.getMessage());

        BadRequestError.ErrorCode errorCode = BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION;
        ApiUtils.ApiResult<?> errorResult = ApiUtils.error(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                String.valueOf(errorCode.getCode()),
                message
        );

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        Map<String, String> message = new HashMap<>(); // 맵으로 변경
        String errorMessage = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());
        message.put("error", errorMessage);

        BadRequestError.ErrorCode errorCode = BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION;
        ApiUtils.ApiResult<?> errorResult = ApiUtils.error(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                String.valueOf(errorCode.getCode()),
                message
        );

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> messages = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                messages.put(error.getField(), error.getDefaultMessage()));

        BadRequestError.ErrorCode errorCode = BadRequestError.ErrorCode.VALIDATION_FAILED;
        ApiUtils.ApiResult<?> errorResult = ApiUtils.error(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                String.valueOf(errorCode.getCode()),
                messages
        );

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> message = new HashMap<>();
        message.put("error", String.format("The required parameter '%s' of type '%s' is missing", ex.getParameterName(), ex.getParameterType()));

        BadRequestError.ErrorCode errorCode = BadRequestError.ErrorCode.MISSING_PART;
        ApiUtils.ApiResult<?> errorResult = ApiUtils.error(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                String.valueOf(errorCode.getCode()),
                message
        );

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        Map<String, String> message = new HashMap<>();
        message.put("error", e.getMessage());

        BadRequestError.ErrorCode errorCode = BadRequestError.ErrorCode.MISSING_PART;
        ApiUtils.ApiResult<?> errorResult = ApiUtils.error(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                String.valueOf(errorCode.getCode()),
                message
        );

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> unknownServerError(Exception e) {
        Map<String, String> message = new HashMap<>();
        message.put("error", e.getMessage());

        InternalServerError.ErrorCode errorCode = InternalServerError.ErrorCode.INTERNAL_SERVER_ERROR;
        ApiUtils.ApiResult<?> errorResult = ApiUtils.error(
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                String.valueOf(errorCode.getCode()),
                message
        );

        return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiUtils.ApiResult<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        Map<String, String> message = new HashMap<>();
        message.put("error", "The request body is not readable or has an invalid format.");

        BadRequestError.ErrorCode errorCode = BadRequestError.ErrorCode.WRONG_REQUEST_TRANSMISSION;
        ApiUtils.ApiResult<?> errorResult = ApiUtils.error(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                String.valueOf(errorCode.getCode()),
                message
        );

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

}
