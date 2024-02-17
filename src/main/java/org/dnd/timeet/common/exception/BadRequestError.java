package org.dnd.timeet.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * HTTP 상태 코드 400 (Bad Request) : 잘못된 요청 유효성 검사 실패 또는 잘못된 파라미터 요청시 발생합니다.
 */

@Getter
public class BadRequestError extends ApiException {

    public BadRequestError(ErrorCode errorCode, Map<String, String> message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }

    public enum ErrorCode implements ApiException.ErrorCode {
        VALIDATION_FAILED(1001, "Request Validation Failed"),
        WRONG_REQUEST_TRANSMISSION(1002, "Wrong Request Transmission"),
        MISSING_PART(1003, "Missing essential part"),
        DUPLICATE_RESOURCE(1004, "Duplicate Resource");

        private final int code;
        private final String message;

        ErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
