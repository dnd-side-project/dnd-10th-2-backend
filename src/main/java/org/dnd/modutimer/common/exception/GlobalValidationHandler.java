package org.dnd.modutimer.common.exception;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class GlobalValidationHandler {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void postOrPutMapping() { // Post 또는 Put 요청시 공통 로직 처리(AOP)
    }

    @Before("postOrPutMapping()")
    public void validationAdvice(JoinPoint jp) {
        Object[] args = jp.getArgs();
        for (Object arg : args) {
            if (arg instanceof Errors) {
                Errors errors = (Errors) arg;

                if (errors.hasErrors()) {
                    BadRequestError.ErrorCode errorCode = BadRequestError.ErrorCode.VALIDATION_FAILED;
                    Map<String, String> validationErrors = new HashMap<>();

                    for (ObjectError error : errors.getAllErrors()) {
                        if (error instanceof FieldError) {
                            FieldError fieldError = (FieldError) error;
                            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
                        } else {
                            validationErrors.put(error.getObjectName(), error.getDefaultMessage());
                        }
                    }

                    throw new BadRequestError(errorCode, validationErrors);
                }
            }
        }
    }
}
