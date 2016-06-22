package com.ppb.lightweight.web.server.internal;

import com.ppb.lightweight.web.server.errors.InternalServerError;
import com.ppb.lightweight.web.server.logger.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * Created by DOBRE Antonel-George on 15.06.2016.
 */
public class HTTPResponse {

    private String responseCode = null;
    private HashMap<String, String> responseHeaders = null;
    private WebServerFile resource = null;
    private int contentLength = 0;

    public HTTPResponse(String responseCode){

        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ");
        builder.append(responseCode);
        builder.append(" ");
        builder.append(" \r\n");
        this.responseCode = builder.toString();
        this.responseHeaders =  new HashMap<>();

    }

    public HTTPResponse(String responseCode, String reason){
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ");
        builder.append(responseCode);
        builder.append(" ");
        builder.append(reason);
        builder.append(" \r\n");
        this.responseCode = builder.toString();
        this.responseHeaders =  new HashMap<>();
    }

    public HTTPResponse(HTTPConstants.HTTP_RESPONSE_CODES code){
        this(String.format("%d", code.getCode()));
    }

    public HTTPResponse(HTTPConstants.HTTP_RESPONSE_CODES code, String reason){
        this(String.format("%d", code.getCode()), reason);
    }

    public void addHeader(String header, String headerMessage){

        if(!HTTPConstants.getGeneralHeadersStringList().contains(header) &&
                !HTTPConstants.getHttpResponseHeaderStringList().contains(header))
            return;

        this.responseHeaders.put(header, headerMessage);

    }

    public void setResource(WebServerFile file){
        this.resource = file;
    }

    /**
     * Get a connection close HTTP response, with the error code stated in parameter.
     *
     * @return
     */
    public static HTTPResponse getCloseMessage(HTTPConstants.HTTP_RESPONSE_CODES errCode){

        HTTPResponse response = new HTTPResponse(errCode, errCode.getRepresentation());
        response.addHeader(HTTPConstants.HTTP_GENERAL_HEADERS.CONNECTION.getRepresentation(), "close");
        return response;

    }

    public static HTTPResponse getOKMessage() {

        HTTPResponse response = new HTTPResponse(HTTPConstants.HTTP_RESPONSE_CODES.OK.getRepresentation());
        response.addHeader(HTTPConstants.HTTP_GENERAL_HEADERS.CONNECTION.getRepresentation(), "keep-alive");
        return response;

    }


    /**
     * The toString() method returns all the HTTP Response headers in a format that can be sent through a client Socket.
     *
     * @return
     */
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.responseCode);
        builder.append("\r\n");
        for(String header : this.responseHeaders.keySet()){
            builder.append(header);
            builder.append(": ");
            builder.append(this.responseHeaders.get(header));
            builder.append("\r\n");
        }
        if(this.hasResource()){
            builder.append(HTTPConstants.HTTP_ENTITY_HEADERS.CONTENT_LENGTH);
            builder.append(": ");
            builder.append(this.resource.length());
            builder.append("\r\n");
        }

        builder.append("\r\n");

        return builder.toString();
    }

    public boolean hasResource(){
        return !(this.resource == null);
    }

    public FileInputStream getResourceContent() throws InternalServerError{

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(this.resource);
        } catch(FileNotFoundException e){
            Logger.logE("Could not find a requested resource: " + this.resource.getAbsolutePath());
            Logger.logE(e);
            throw new InternalServerError("Error while finding resource.");
        }

        return inputStream;
    }

    /**
     * Checks to see if the HTTPResponse is an OK message.
     * Returns true if the response code is 200 (OK), false otherwise.
     *
     * @return
     */
    public boolean isOK(){
        return this.responseCode.contains("200");
    }

}
