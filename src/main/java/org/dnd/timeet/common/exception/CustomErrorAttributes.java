package org.dnd.timeet.common.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(
        WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(webRequest, options);

        Map<String, Object> customErrorAttributes = new LinkedHashMap<>();
        boolean errorOccurred = !defaultErrorAttributes.get("status").equals(200);
        int status = (int) defaultErrorAttributes.get("status");

        if (errorOccurred) {
            Map<String, Object> errorDetails = new LinkedHashMap<>();

            Object defaultMessage = defaultErrorAttributes.get("message");
            String customMessage = (status == 404) ? "Not Found" : String.valueOf(defaultMessage);

            errorDetails.put("status", String.valueOf(status));
            errorDetails.put("code", NotFoundError.ErrorCode.RESOURCE_NOT_FOUND.getCode());

            if (defaultMessage == null || defaultMessage.toString().trim().isEmpty()) {
                errorDetails.put("message", customMessage);
            }

            customErrorAttributes.put("response", null);
            customErrorAttributes.put("error", errorDetails);
        } else {
            customErrorAttributes.put("response", null);
            customErrorAttributes.put("error", null);
        }

        return customErrorAttributes;
    }
}