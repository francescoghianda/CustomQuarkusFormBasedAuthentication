package com.security.exception;

public class MalformedCookieException extends Exception{

    public MalformedCookieException(){}

    public MalformedCookieException(String message){
        super(message);
    }
}
