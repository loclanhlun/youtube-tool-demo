package com.hbloc.youtube_tool_demo.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "Success"),
    USER_PERMISSION_ERROR(20005, "Forbidden"),
    VALIDATION_FAILED(100, "Validation Failed"),
    DUPLICATE_ERROR(101, "Duplicated"),
    NOT_FOUND_ERROR(102, "Not Found"),
    UNKNOWN_ERROR(999, "Unknown Error");

    private final Integer code;
    private final String message;
}
