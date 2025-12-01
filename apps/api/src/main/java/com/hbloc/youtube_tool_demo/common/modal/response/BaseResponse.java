package com.hbloc.youtube_tool_demo.common.modal.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private boolean success;
    private long timestamp = System.currentTimeMillis();
    private T data;
    private Map<String, String> error;
}
