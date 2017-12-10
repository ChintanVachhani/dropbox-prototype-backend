package com.dropboxPrototypeBackend.entity;

public class SuccessResponse {
    private int status;
    private String message;
    private String token;
    private Object data;

    public SuccessResponse(int status, String message, String token, Object data) {
        this.status = status;
        this.message = message;
        this.token = token;
        this.data = data;
    }

    public SuccessResponse(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
