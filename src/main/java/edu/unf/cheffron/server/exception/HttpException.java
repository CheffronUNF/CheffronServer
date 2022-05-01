package edu.unf.cheffron.server.exception;

public class HttpException extends RuntimeException 
{
    public final int statusCode;
    public final String message;

    public HttpException(int statusCode, String message)
    {
        this.statusCode = statusCode;
        this.message = message;
    }
}
