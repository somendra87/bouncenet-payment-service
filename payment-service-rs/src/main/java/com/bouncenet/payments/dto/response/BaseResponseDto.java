package com.bouncenet.payments.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponseDto<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> BaseResponseDto<T> success(T data, String message) {
        BaseResponseDto<T> response = new BaseResponseDto<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> BaseResponseDto<T> error(String message) {
        BaseResponseDto<T> response = new BaseResponseDto<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
