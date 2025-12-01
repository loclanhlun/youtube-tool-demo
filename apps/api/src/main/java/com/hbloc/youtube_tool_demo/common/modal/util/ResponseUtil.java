package com.hbloc.youtube_tool_demo.common.modal.util;

import com.hbloc.youtube_tool_demo.common.constant.ResultCode;
import com.hbloc.youtube_tool_demo.common.modal.response.BaseResponse;

import java.util.Map;

public class ResponseUtil<T>{

    private final BaseResponse<T> response;


    /**
     * Initializes a successful response with default success code and description.
     */
    public ResponseUtil() {
        this.response = new BaseResponse<>();
        this.response.setSuccess(true);
        this.response.setMessage(ResultCode.SUCCESS.getMessage());
        this.response.setCode(ResultCode.SUCCESS.getCode());
    }

    /**
     * Sets the result data for the response.
     *
     * @param data the result data
     * @return the updated BaseResponse
     */
    public BaseResponse<T> setResult(T data) {
        this.response.setData(data);
        return this.response;
    }

    /**
     * Sets a successful response with a custom result code and result data.
     *
     * @param resultCode the result code enum
     * @param data       the result data
     * @return the updated BaseResponse
     */
    public BaseResponse<T> setSuccessResp(ResultCode resultCode, T data) {
        this.response.setCode(resultCode.getCode());
        this.response.setMessage(resultCode.getMessage());
        this.response.setSuccess(true);
        this.response.setData(data);
        return this.response;
    }

    /**
     * Sets an error response with a specific result code.
     *
     * @param resultCode the result code enum
     * @return the updated BaseResponse
     */
    public BaseResponse<T> setErrorResp(ResultCode resultCode) {
        this.response.setSuccess(false);
        this.response.setCode(resultCode.getCode());
        this.response.setMessage(response.getMessage());
        return this.response;
    }


    /**
     * Sets an error response with a custom code and message.
     *
     * @param code the error code
     * @param msg  the error message
     * @return the updated BaseResponse
     */
    public BaseResponse<T> setErrorResp(Integer code, String msg) {
        this.response.setSuccess(false);
        this.response.setCode(code);
        this.response.setMessage(msg);
        return this.response;
    }

    /**
     * Sets an error response with a custom code, message, and error details.
     *
     * @param code   the error code
     * @param msg    the error message
     * @param errors the error details map
     * @return the updated BaseResponse
     */
    public BaseResponse<T> setErrorResp(Integer code, String msg, Map<String, String> errors) {
        this.response.setSuccess(false);
        this.response.setCode(code);
        this.response.setMessage(msg);
        this.response.setError(errors);
        return this.response;
    }

    /**
     * Creates a successful response with the given result data.
     *
     * @param data   the result data
     * @param <T> the type of the result
     * @return a successful BaseResponse
     */
    public static <T> BaseResponse<T> success(T data) {
        return new ResponseUtil<T>().setSuccessResp(ResultCode.SUCCESS, data);
    }

    /**
     * Creates a successful response with the given result data.
     *
     * @param resultCode the result code enum
     * @param data   the result data
     * @param <T> the type of the result
     * @return a successful BaseResponse
     */
    public static <T> BaseResponse<T> success(ResultCode resultCode, T data) {
        return new ResponseUtil<T>().setSuccessResp(resultCode, data);
    }


    /**
     * Creates an error response with the given result code.
     *
     * @param resultCode the result code enum
     * @param <T>        the type of the result
     * @return an error BaseResponse
     */
    public static <T> BaseResponse<T> error(ResultCode resultCode) {
        return new ResponseUtil<T>().setErrorResp(resultCode);
    }

    /**
     * Creates an error response with a custom code and message.
     *
     * @param code the error code
     * @param msg  the error message
     * @param <T>  the type of the result
     * @return an error BaseResponse
     */
    public static <T> BaseResponse<T> error(Integer code, String msg) {
        return new ResponseUtil<T>().setErrorResp(code, msg);
    }

    /**
     * Creates an error response with a custom code and message and error fields.
     *
     * @param code   the error code
     * @param msg    the error message
     * @param errors the field errors map
     * @param <T>    the type of the result
     * @return an error BaseResponse
     */
    public static <T> BaseResponse<T> error(Integer code, String msg, Map<String, String> errors) {
        return new ResponseUtil<T>().setErrorResp(code, msg, errors);
    }

}
