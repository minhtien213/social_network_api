package com.example.social_network_api.exception.custom;

//404
public class ResourceNotfoundException extends RuntimeException {
    public ResourceNotfoundException(String message) {
        super(message);
    }
}
