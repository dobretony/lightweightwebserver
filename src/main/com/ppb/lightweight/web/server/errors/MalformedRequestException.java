package com.ppb.lightweight.web.server.errors;

import com.ppb.lightweight.web.server.http.HTTPConstants.HTTP_RESPONSE_CODES;

/**
 * Class that represents the Exception thrown when a bad request was identified by HTTPRequest methods.
 * The message it contains is the message that will be logged in the server logging system.
 * The errCode variable contains the response the server should send the client.
 * The shouldClose variable states if the connection should be terminated by the server or not.
 *
 * Created by DOBRE Antonel-George on 16.06.2016.
 */
public class MalformedRequestException extends Exception {

    private final HTTP_RESPONSE_CODES errCode;
    private final boolean shouldClose;

    public MalformedRequestException(String message){
        super(message);
        this.errCode = null;
        this.shouldClose = false;
    }

    public MalformedRequestException(String message, HTTP_RESPONSE_CODES errCode){
        super(message);
        this.errCode = errCode;
        this.shouldClose = false;
    }

    public MalformedRequestException(String message, HTTP_RESPONSE_CODES errCode, boolean shouldClose){
        super(message);
        this.errCode = errCode;
        this.shouldClose = shouldClose;
    }

    public HTTP_RESPONSE_CODES getErrorCode(){
        return this.errCode;
    }

    public boolean isClosed(){
        return this.shouldClose;
    }

}
