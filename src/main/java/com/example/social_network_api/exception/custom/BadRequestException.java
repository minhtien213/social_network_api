package com.example.social_network_api.exception.custom;

//400
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
