package com.just.cn.mgg.data.remote.response;

/**
 * 统一API响应格式
 * @param <T> 数据类型
 */
public class ApiResponse<T> {
    private int code;      // 200成功 其他失败
    private String message;
    private T data;
    
    public ApiResponse() {}
    
    public boolean isSuccess() {
        return code == 200;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}

