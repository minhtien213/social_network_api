package com.example.social_network_api.exception.custom;

//401
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message){
        super(message);
    }
}
