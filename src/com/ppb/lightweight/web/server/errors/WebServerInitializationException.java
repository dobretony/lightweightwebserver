package com.ppb.lightweight.web.server.errors;

/**
 * Created by tony on 14.06.2016.
 */
public class WebServerInitializationException extends Exception {

    public WebServerInitializationException(String message){
        super(message);
    }

    public WebServerInitializationException(){
        super("There was an error while initializing the server.");
    }
}
