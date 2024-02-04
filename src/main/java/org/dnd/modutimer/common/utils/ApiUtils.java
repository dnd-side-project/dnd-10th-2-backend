package org.dnd.modutimer.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

/**
 * API 응답을 생성하는데 사용되는 유틸리티 메서드와 내부 클래스 제공
 * 성공적인 API 응답과 에러 응답을 생성하는 정적 메서드를 포함
 */

public class ApiUtils {

    public static <T> ApiResult<T> success(T response) {
        return new ApiResult<>(true, response, null);
    }

    public static ApiResult<?> error(String status, String code, Map<String, String> message) {
        return new ApiResult<>(false, null, new ApiError(status, code, message));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ApiResult<T> {
        private final boolean success;
        private final T response;
        private final ApiError error;

        @Override
        public String toString() {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                return "{\"success\":false,\"response\":null,\"error\":{\"message\":\"Error in generating JSON response\"}}";
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ApiError {
        private final String status;
        private final String code;
        private final Map<String, String> message;

        public ApiError(String status, String code, String message) {
            this.status = status;
            this.code = code;
            this.message = new HashMap<>();
            this.message.put("defaultMessage", message);
        }
    }
}
