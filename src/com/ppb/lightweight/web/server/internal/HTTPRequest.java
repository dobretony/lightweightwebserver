package com.ppb.lightweight.web.server.internal;

import com.ppb.lightweight.web.server.errors.MalformedRequestException;
import com.ppb.lightweight.web.server.utils.IPUtils;
import com.ppb.lightweight.web.server.internal.HTTPConstants.REQUEST_TYPE;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * Created by DOBRE Antonel-George on 15.06.2016.
 */
public class HTTPRequest {

    private REQUEST_TYPE requestType = null;
    private HashMap<String, String> headers = null;
    private String requestURI = null;
    private String httpVersion = null;
    private InetAddress clientIPAddress = null;
    private char[] content = null;

    public HTTPRequest(String requestLine, InetAddress clientIPAddress) throws MalformedRequestException {
        String[] requests = requestLine.split(" ");

        // check if request line was parsed ok and is valid
        if(requests.length != 3)
            throw new MalformedRequestException("An error occurred with request: " +
                                                requestLine +
                                                " from client " + clientIPAddress.toString() +
                                                ". REASON: Bad request from client.",
                                                HTTPConstants.HTTP_RESPONSE_CODES.BAD_REQUEST,
                                                true);

        String method = requests[0];
        this.requestType = REQUEST_TYPE.getRequestType(method);

        // check if we support the request type
        if(this.requestType == REQUEST_TYPE.UNDEFINED)
            throw new MalformedRequestException("An error occurred with request: " +
                                                requestLine +
                                                " from client " + clientIPAddress.toString() +
                                                ". REASON: Method not implemented or undefined.",
                                                HTTPConstants.HTTP_RESPONSE_CODES.NOT_IMPLEMENTED,
                                                true);

        // at the moment, the server does not allow DELETE, PUT, TRACE or CONNECT operations
        if(this.requestType == REQUEST_TYPE.DELETE ||
                this.requestType == REQUEST_TYPE.PUT ||
                this.requestType == REQUEST_TYPE.TRACE ||
                this.requestType == REQUEST_TYPE.CONNECT)
            throw new MalformedRequestException("An error occurred with request: " +
                                                requestLine +
                                                " from client " + clientIPAddress.toString() +
                                                ". REASON: Method not allowed on server.",
                                                HTTPConstants.HTTP_RESPONSE_CODES.METHOD_NOT_ALLOWED,
                                                true);

        this.headers = new HashMap<>();
        this.clientIPAddress = clientIPAddress;

        this.requestURI = requests[1];

        // check if requestURI is valid
        // request URI can either be * | absoluteURI | abs_path | authority
        switch(this.requestType){
            case GET:
                break;
            case POST:
                break;
            case OPTIONS:
                break;
            case HEAD:
                break;
            default:
                break;
        }

        this.httpVersion = requests[2];

        //check if HTTP version is valid
        //at the moment we only accept HTTP /1.1 and HTTP /1.0
        if(!this.httpVersion.equals("HTTP/1.1") && !this.httpVersion.equals("HTTP/1.0"))
            throw new MalformedRequestException("An error occurred with request: " +
                                                requestLine +
                                                " from client " + this.clientIPAddress.toString() +
                                                ". REASON: Protocol not supported by server.",
                                                HTTPConstants.HTTP_RESPONSE_CODES.BAD_REQUEST,
                                                true);
    }

    public REQUEST_TYPE getRequestType() {
        return requestType;
    }

    public void addHeader(String headerLine) throws MalformedRequestException {

        String[] header = headerLine.split(":", 2);

        // check if we understand the header
        // we ignore the header if we do not understand it
        // this behaviour might change in future implementations
        if(HTTPConstants.getGeneralHeadersStringList().contains(header[0]) ||
                HTTPConstants.getHttpRequestHeadersStringList().contains(header[0])) {
            // we will check individual header integrity as the need arises for that header
            this.headers.put(header[0].trim(), header[1].trim());
        }

    }

    /**
     * Method that checks the integrity of the Request as a whole, after the header files have been fully added.
     * @throws MalformedRequestException
     */
    public void checkIntegrity() throws MalformedRequestException{

    }

    /**
     * Method that checks if further information is needed from the client during this request.
     * (e.g. "Transfer-Encoding: chunked" header is present)
     *
     * @return
     */
    public boolean doContinue(){
        return !this.headers.get(HTTPConstants.HTTP_GENERAL_HEADERS.CONNECTION.getRepresentation()).equals("close");
    }

    public boolean hasKeepAlive(){
        if(this.headers.containsKey(HTTPConstants.HTTP_GENERAL_HEADERS.CONNECTION.getRepresentation())){
            if(this.headers.get(HTTPConstants.HTTP_GENERAL_HEADERS.CONNECTION.getRepresentation()).equals("keep-alive"))
                return true;
        }
        return false;
    }

    public String getRequestURI(){
        return this.requestURI;
    }

    public boolean hasHeader(String header){
        return this.headers.containsKey(header);
    }

    public String getHeaderValue(String header){
        return this.headers.get(header);
    }

    public void setContent(char[] buffer){
        this.content = buffer;
    }

    public boolean hasContent(){
        return this.headers.containsKey(HTTPConstants.HTTP_ENTITY_HEADERS.CONTENT_LENGTH.getRepresentation());
    }

    /**
     * Returns the content length presented in the header file of this socket.
     * This value represents the number of bytes that should be read from the client socket.
     *
     * @return
     */
    public int getContentLength(){
        if(!this.hasContent())
            return 0;
        return Integer.parseInt(this.headers.get(HTTPConstants.HTTP_ENTITY_HEADERS.CONTENT_LENGTH.getRepresentation()));
    }

    /**
     * Returns a String representation of this class.
     *
     * In this case, it returns the method and headers received from the client.
     *
     * @return
     */
    @Override
    public String toString(){

        StringBuilder builder = new StringBuilder();

        builder.append(this.requestType);
        builder.append(" ");
        builder.append(this.requestURI);
        builder.append(" ");
        builder.append(this.httpVersion);
        builder.append("\r\n");

        for(String value : this.headers.keySet()){
            builder.append(value + ": " + this.headers.get(value) + "\r\n");
        }

        return builder.toString();
    }

}
