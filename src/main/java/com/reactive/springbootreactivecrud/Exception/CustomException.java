package com.reactive.springbootreactivecrud.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class CustomException extends RuntimeException{
    public CustomException(String message, int value) {
        super(message);
    }
}
