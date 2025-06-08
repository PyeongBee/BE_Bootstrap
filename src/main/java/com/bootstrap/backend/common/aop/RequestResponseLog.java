package com.bootstrap.backend.common.aop;

import com.bootstrap.backend.common.annotation.Loggable;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RequestResponseLog {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around(
            "@annotation(com.bootstrap.backend.common.annotation.Loggable) || @within(com.bootstrap.backend.common.annotation.Loggable)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Loggable loggable = signature.getMethod().getAnnotation(Loggable.class);

        if (loggable == null) {
            loggable = joinPoint.getTarget().getClass().getAnnotation(Loggable.class);
        }

        String message = loggable != null ? loggable.value() : "";

        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            StringBuilder requestParams = new StringBuilder("Request Parameters: ");
            for (Object arg : args) {
                if (arg != null) {
                    try {
                        String json = objectMapper.writeValueAsString(arg);
                        requestParams.append(json).append("\n");
                    } catch (Exception e) {
                        requestParams.append(arg.toString()).append("\n");
                    }
                }
            }
            log.info("[REQUEST] {}::{} {}", message, signature.getName(), requestParams.toString());
        }

        try {
            Object result = joinPoint.proceed();

            if (result instanceof ResponseEntity<?>) {
                ResponseEntity<?> response = (ResponseEntity<?>) result;
                logResponse(response, signature.getName(), message);
            } else {
                log.info("[RESPONSE] {}::{} {}", message, signature.getName(), result);
            }

            return result;
        } catch (Exception e) {
            log.error("[ERROR] {}::{} Error: {}", message, signature.getName(), e.getMessage());
            throw e;
        }
    }

    private void logResponse(ResponseEntity<?> response, String methodName, String message) {
        StringBuilder responseLog = new StringBuilder("Response: ");

        responseLog.append(response.getStatusCode());

        HttpHeaders headers = response.getHeaders();
        if (!headers.isEmpty()) {
            responseLog.append(", Headers: ");
            headers.forEach(
                    (key, values) ->
                            responseLog.append(key).append(": ").append(values).append("\n"));
        }

        Object body = response.getBody();
        if (body != null) {
            try {
                String json = objectMapper.writeValueAsString(body);
                responseLog.append(", Body: ").append(json);
            } catch (Exception e) {
                responseLog.append(body.toString());
            }
        }

        log.info("[RESPONSE] {}::{} {}", message, methodName, responseLog.toString());
    }
}
