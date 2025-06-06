package com.delivery.Exceptions;

public class FailedToSendMailException extends RuntimeException{

    public FailedToSendMailException(String message){
        super(message);
    }
}
