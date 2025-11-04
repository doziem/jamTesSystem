package com.doziem.jamTesSystem.response;

import lombok.Data;

@Data
public class ApiResponse {
    boolean status = false;
    String message;
    private Object data;

    public ApiResponse(boolean status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data =data;
    }

    public ApiResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
