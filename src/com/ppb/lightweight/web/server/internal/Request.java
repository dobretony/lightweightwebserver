package com.ppb.lightweight.web.server.internal;

/**
 * Created by tony on 15.06.2016.
 */
public class Request {

    public enum REQUEST_TYPE {
        GET,
        POST,
        UNDEFINED;

        public static REQUEST_TYPE getRequestType(String line){
            if(line.contains("GET"))
                return REQUEST_TYPE.GET;
            else if(line.contains("POST"))
                return REQUEST_TYPE.POST;
            else
                return REQUEST_TYPE.UNDEFINED;
        }

    }

    private REQUEST_TYPE requestType = null;
    private String context = null;
    private String protocol = null;
    private String host = null;
    private String userAgentString = null;
    private String acceptString = null;
    private String acceptLanguageString = null;
    private String acceptEncodingString = null;
    private String connectionType = null;

    public Request(String requestHeader){
        String[] headers = requestHeader.split("\n");
        String[] protocolLine = headers[0].split(" ");
        this.requestType = REQUEST_TYPE.getRequestType(protocolLine[0]);
        this.context = protocolLine[1];
        this.protocol = protocolLine[2];
        this.host = headers[1];
        this.userAgentString = headers[2];
        this.acceptString = headers[3];
        this.acceptLanguageString = headers[4];
        this.acceptEncodingString = headers[5];
        this.connectionType = headers[6];
    }

    public REQUEST_TYPE getRequestType() {
        return requestType;
    }

    public String getContext() {
        return context;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getUserAgentString() {
        return userAgentString;
    }

    public String getAcceptString() {
        return acceptString;
    }

    public String getAcceptLanguageString() {
        return acceptLanguageString;
    }

    public String getAcceptEncodingString() {
        return acceptEncodingString;
    }

    public String getConnectionType() {
        return connectionType;
    }

}
