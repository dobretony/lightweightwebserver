package com.ppb.lightweight.web.server.errors;

/**
 * Exception launched when the server encounters problems during the initialization phase.
 *
 * Created by DOBRE Antonel-George on 14.06.2016.
 */
public class WebServerInitializationException extends Exception {

    public WebServerInitializationException(String message){
        super(message);
    }

    public WebServerInitializationException(){
        super("There was an error while initializing the server.");
    }
}
