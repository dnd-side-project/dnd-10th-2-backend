package org.dnd.modutimer.common.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * HTTP 상태 코드 403 (Forbidden) : 금지됨
 * 인증은 되었지만, 리소스에 접근할 권한이 없을때 발생합니다.
 */
@Getter
public class ForbiddenError extends ApiException {

    public ForbiddenError(ErrorCode errorCode, Map<String, String> message) {
        super(errorCode, message, HttpStatus.FORBIDDEN);
    }

    public enum ErrorCode implements ApiException.ErrorCode {
        ROLE_BASED_ACCESS_ERROR(1101, "Role-Based Access Error"),
        RESOURCE_ACCESS_FORBIDDEN(1102, "Resource Authorization Error");

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
