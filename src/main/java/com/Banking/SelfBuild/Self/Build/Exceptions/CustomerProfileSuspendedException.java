package com.Banking.SelfBuild.Self.Build.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CustomerProfileSuspendedException extends RuntimeException
{
    public CustomerProfileSuspendedException(String message) {
        super(message);
    }

    public CustomerProfileSuspendedException(String message, Throwable cause) {
        super(message, cause);
    }
}
