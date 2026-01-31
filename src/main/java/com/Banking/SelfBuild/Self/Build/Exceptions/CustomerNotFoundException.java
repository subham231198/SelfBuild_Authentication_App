package com.Banking.SelfBuild.Self.Build.Exceptions;

public class CustomerNotFoundException extends RuntimeException
{
    public CustomerNotFoundException(String message)
    {
        super(message);
    }

    public CustomerNotFoundException(String message, Throwable clause)
    {
        super(message, clause);
    }
}
