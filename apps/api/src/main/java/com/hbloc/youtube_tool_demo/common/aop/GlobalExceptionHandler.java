package com.hbloc.youtube_tool_demo.common.aop;

import com.hbloc.youtube_tool_demo.common.constant.ResultCode;
import com.hbloc.youtube_tool_demo.common.modal.AppException;
import com.hbloc.youtube_tool_demo.common.modal.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class, Exception.class, AppException.class, RuntimeException.class})
    public ResponseEntity<?> handleException(Exception e) {
        if (e instanceof AppException appException) {
            return ResponseEntity.ok(ResponseUtil.error(appException.getCode(), appException.getMessage()));
        }

        return ResponseEntity.ok(
                ResponseUtil.error(
                        ResultCode.UNKNOWN_ERROR.getCode(),
                        e.getMessage()
                )
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.ok(
                ResponseUtil.error(
                        ResultCode.VALIDATION_FAILED.getCode(),
                        ResultCode.VALIDATION_FAILED.getMessage(),
                        errors
                )
        );
    }
}
