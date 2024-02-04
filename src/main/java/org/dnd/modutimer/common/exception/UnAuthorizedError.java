package org.dnd.modutimer.common.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * HTTP 상태 코드 401 (Unauthorized) : 권한 없음 인증이 되지 않았을때 발생합니다.
 */
@Getter
public class UnAuthorizedError extends ApiException {

    public UnAuthorizedError(ErrorCode errorCode, Map<String, String> message) {
        super(errorCode, message, HttpStatus.UNAUTHORIZED);
    }

    public enum ErrorCode implements ApiException.ErrorCode {
        AUTHENTICATION_FAILED(1201, "Authentication Failure"),
        ACCESS_DENIED(1202, "Insufficient Permissions for Resource Access");

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
