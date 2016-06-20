package com.ppb.lightweight.web.server.internal;

import java.util.HashMap;

/**
 * Created by DOBRE Antonel-George on 15.06.2016.
 */
public class HTTPResponse {

    private String responseCode = null;
    private HashMap<String, String> responseHeaders = null;
    private StringBuilder contentBuilder = null;

    public HTTPResponse(int responseCode){

        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ");
        builder.append(responseCode);
        this.responseCode = builder.toString();
        this.responseHeaders =  new HashMap<>();
        this.contentBuilder = new StringBuilder();

    }


    public void addHeader(String header, String headerMessage){

        if(!HTTPConstants.getGeneralHeadersStringList().contains(header))
            return;

        if(!HTTPConstants.getHttpResponseHeaderStringList().contains(header))
            return;

        this.responseHeaders.put(header, headerMessage);

    }

    public void addContentMessage(String messageLine){
        this.contentBuilder.append(messageLine);
    }

    /**
     * Get a connection close HTTP response, with the error code stated in parameter.
     *
     * @return
     */
    public static HTTPResponse getCloseMessage(int errCode){

        HTTPResponse response = new HTTPResponse(errCode);
        response.addHeader(HTTPConstants.HTTP_GENERAL_HEADERS.CONNECTION.getRepresentation(), "close" );
        return response;

    }

    public static HTTPResponse getOKMessage() {

        HTTPResponse response = new HTTPResponse(Integer.parseInt(HTTPConstants.HTTP_RESPONSE_CODES.OK.getRepresentation()));
        response.addHeader(HTTPConstants.HTTP_GENERAL_HEADERS.CONNECTION.getRepresentation(), "keep-alive");
        return response;

    }


    public String getOutput(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.responseCode);
        for(String header : this.responseHeaders.keySet()){
            builder.append(header + ": " + this.responseHeaders.get(header) + "\n");
        }

        return builder.toString();
    }
}
