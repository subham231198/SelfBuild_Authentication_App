package com.Banking.SelfBuild.Self.Build.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class SessionNotCreatedException extends RuntimeException
{
    public SessionNotCreatedException(String message) {
        super(message);
    }

    public SessionNotCreatedException(String message, Throwable clause)
    {
        super(message, clause);
    }
}
