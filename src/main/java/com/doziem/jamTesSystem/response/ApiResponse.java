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

}
