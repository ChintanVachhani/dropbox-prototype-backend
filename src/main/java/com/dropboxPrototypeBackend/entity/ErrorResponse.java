package com.dropboxPrototypeBackend.entity;

import java.util.Map;

public class ErrorResponse {
    private int status;
    private String title;
    private Map<String, String> error;

    public ErrorResponse(int status, String title, String message) {
        this.status = status;
        this.title = title;
        this.error.put("message", message);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, String> getError() {
        return error;
    }

    public void setError(Map<String, String> error) {
        this.error = error;
    }
}
