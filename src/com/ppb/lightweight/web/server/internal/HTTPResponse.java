package com.ppb.lightweight.web.server.internal;

import java.util.HashMap;

/**
 * Created by DOBRE Antonel-George on 15.06.2016.
 */
public class HTTPResponse {

    private String responseCode = null;
    private HashMap<String, String> responseHeaders = null;

    public HTTPResponse(int responseCode){

        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ");
        builder.append(responseCode);
        this.responseCode = builder.toString();
        this.responseHeaders =  new HashMap<>();

    }


    public void addHeader(String header, String headerMessage){

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

}
