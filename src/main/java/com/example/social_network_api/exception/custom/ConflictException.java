package com.example.social_network_api.exception.custom;

//409
public class ConflictException extends  RuntimeException{
    public ConflictException(String message){
        super(message);
    }
}
