package com.ppb.lightweight.web.server.internal;

import com.ppb.lightweight.web.server.errors.InternalServerError;
import com.ppb.lightweight.web.server.logger.Logger;
import com.ppb.lightweight.web.server.utils.Configurations;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by DOBRE Antonel-George on 15.06.2016.
 */
public class HTTPResponse {

    private String responseCode = null;
    private HashMap<String, String> responseHeaders = null;
    private WebServerFile resource = null;

    public HTTPResponse(String responseCode){

        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ");
        builder.append(responseCode);
        builder.append(" ");
        builder.append(" \r\n");
        this.responseCode = builder.toString();
        this.responseHeaders =  new HashMap<>();

        String today = new SimpleDateFormat(HTTPConstants.RFC1123_DATE_FORMAT).format(new Date());
        this.addHeader(HTTPConstants.HTTP_GENERAL_HEADERS.DATE.getRepresentation(), today);

        if(Configurations.SERVER_HOSTNAME != null) {
            this.addHeader(HTTPConstants.HTTP_RESPONSE_HEADERS.SERVER.getRepresentation(),
                    Configurations.SERVER_HOSTNAME);
        }

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

        String today = new SimpleDateFormat(HTTPConstants.RFC1123_DATE_FORMAT).format(new Date());
        this.addHeader(HTTPConstants.HTTP_GENERAL_HEADERS.DATE.getRepresentation(), today);

        if(Configurations.SERVER_HOSTNAME != null) {
            this.addHeader(HTTPConstants.HTTP_RESPONSE_HEADERS.SERVER.getRepresentation(),
                    Configurations.SERVER_HOSTNAME);
        }
    }

    public HTTPResponse(HTTPConstants.HTTP_RESPONSE_CODES code){
        this(String.format("%d", code.getCode()));
    }

    public HTTPResponse(HTTPConstants.HTTP_RESPONSE_CODES code, String reason){
        this(String.format("%d", code.getCode()), reason);
    }

    public void addHeader(String header, String headerMessage){

        if(!HTTPConstants.getGeneralHeadersStringList().contains(header) &&
                !HTTPConstants.getHttpResponseHeaderStringList().contains(header) &&
                !HTTPConstants.getHttpEntityHeaderStringList().contains(header)) {
            return;
        }

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

        HTTPResponse response = new HTTPResponse(HTTPConstants.HTTP_RESPONSE_CODES.OK);
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
        for(String header : this.responseHeaders.keySet()){
            builder.append(header);
            builder.append(": ");
            builder.append(this.responseHeaders.get(header));
            builder.append("\r\n");
        }
        if(this.hasResource()){
            builder.append(HTTPConstants.HTTP_ENTITY_HEADERS.CONTENT_LENGTH.getRepresentation());
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

    /**
     * Returns the length in OCTET of the resource associated with this response.
     *
     * @return
     */
    public long getContentLength(){
        if(this.hasResource()){
            return this.resource.length();
        }
        return 0;
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

    public WebServerFile getResource(){
        return this.resource;
    }

    public void removeResource(){
        this.resource = null;
    }

}
