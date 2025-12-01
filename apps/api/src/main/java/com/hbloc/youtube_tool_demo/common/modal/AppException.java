package com.hbloc.youtube_tool_demo.common.modal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AppException extends RuntimeException {
    private Integer code;
    private String message;
}
