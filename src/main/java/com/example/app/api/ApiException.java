package com.example.app.api;

import lombok.Getter;

public class ApiException extends RuntimeException {

    @Getter
    private int errorCode;

    public ApiException(String message){
        super(message);
    }
    public ApiException(String message,Throwable cause){
        super(message,cause);
    }

}
