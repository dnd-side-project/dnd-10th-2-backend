package org.dnd.modutimer.common.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.dnd.modutimer.common.exception.ForbiddenError;
import org.dnd.modutimer.common.exception.UnAuthorizedError;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * HTTP 응답을 처리하기 위한 유틸리티 메서드
 * 보안 관련 예외(예: 인증되지 않음, 접근 금지)가 발생했을 때, 적절한 HTTP 응답을 생성하고 클라이언트에 전송하는 역할 수행
 * HTTP 상태 코드, 에러 메시지 등을 JSON 형식으로 변환하여 응답 본문에 포함함
 */
public class FilterResponseUtils {

    private static final ObjectMapper om = new ObjectMapper();

    public static void unAuthorized(HttpServletResponse resp, UnAuthorizedError e) throws IOException {
        String responseBody = om.writeValueAsString(ApiUtils.error(
                String.valueOf(e.getStatus().value()),
                String.valueOf(e.getErrorCode().getCode()),
                e.getErrors()
        ));

        sendResponse(resp, responseBody, e.getStatus());
    }

    public static void forbidden(HttpServletResponse resp, ForbiddenError e) throws IOException {
        String responseBody = om.writeValueAsString(ApiUtils.error(
                String.valueOf(e.getStatus().value()),
                String.valueOf(e.getErrorCode().getCode()),
                e.getErrors()
        ));

        sendResponse(resp, responseBody, e.getStatus());
    }

    private static void sendResponse(HttpServletResponse resp, String responseBody, HttpStatus status) throws IOException {
        resp.getWriter().println(responseBody);
        resp.setStatus(status.value());
        resp.setContentType("application/json; charset=utf-8");
    }
}
