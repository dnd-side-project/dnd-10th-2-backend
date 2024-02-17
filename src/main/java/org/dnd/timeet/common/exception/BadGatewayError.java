package org.dnd.timeet.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * HTTP 상태 코드 502 (Bad Gateway) 게이트웨이나 프록시 역할을 하는 서버가 상위 서버로부터 잘못된 응답을 받았을 때 발생합니다.
 */
@Getter
public class BadGatewayError extends ApiException {

    public BadGatewayError(ForbiddenError.ErrorCode errorCode, Map<String, String> message) {
        super(errorCode, message, HttpStatus.BAD_GATEWAY);
    }

    public enum ErrorCode implements ApiException.ErrorCode {
        GATEWAY_ERROR(3000, "gateway error");

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
