package org.dnd.modutimer.common.exception;

import lombok.Getter;
import org.dnd.modutimer.utils.ApiUtils;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public abstract class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, String> errors;
    private final HttpStatus status;

    public ApiException(ErrorCode errorCode, Map<String, String> errors, HttpStatus status) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errors = errors;
        this.status = status;
    }

    public ApiUtils.ApiResult<?> body() {
        return ApiUtils.error(
                String.valueOf(this.getStatus().value()),
                String.valueOf(this.errorCode.getCode()),
                this.errors
        );
    }

    public interface ErrorCode {
        int getCode();

        String getMessage();
    }
}
