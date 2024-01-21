package org.dnd.modutimer.common.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * HTTP 상태 코드 404 (Not Found)
 * 리소스 찾을 수 없을 때 발생합니다.
 */
@Getter
public class NotFoundError extends ApiException {

    public NotFoundError(ErrorCode errorCode, Map<String, String> message) {
        super(errorCode, message, HttpStatus.NOT_FOUND);
    }

    public enum ErrorCode implements ApiException.ErrorCode {
        RESOURCE_NOT_FOUND(1301, "Nonexistent Resource Access");

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
