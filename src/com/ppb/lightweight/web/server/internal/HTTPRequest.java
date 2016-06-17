package com.ppb.lightweight.web.server.internal;

import com.ppb.lightweight.web.server.errors.MalformedRequestException;
import com.ppb.lightweight.web.server.utils.IPUtils;
import com.ppb.lightweight.web.server.internal.HTTPConstants.REQUEST_TYPE;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * Created by tony on 15.06.2016.
 */
public class HTTPRequest {

    private REQUEST_TYPE requestType = null;
    private HashMap<String, String> headers = null;
    private String requestURI = null;
    private String httpVersion = null;
    private InetAddress clientIPAddress = null;

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

        String[] header = headerLine.split(":");

        if(header.length != 2){
            throw new MalformedRequestException("An error occurred with header " +
                                                        headerLine + " from client: " +
                                                        this.clientIPAddress.toString());
        }

        // check if we understand the header
        // we ignore the header if we do not understand it
        // this behaviour might change in future implementations
        if(HTTPConstants.getGeneralHeadersStringList().contains(header[0])) {
            // we will check individual header integrity as the need arises for that header
            this.headers.put(header[0], header[1]);
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
     * TODO: get all possible cases
     * @return
     */
    public boolean doContinue(){
        if(this.headers.keySet().contains(HTTPConstants.HTTP_GENERAL_HEADERS.TRANSFER_ENCODING.getRepresentation())){
            if(this.headers.get(HTTPConstants.HTTP_GENERAL_HEADERS.TRANSFER_ENCODING.getRepresentation()).
                    equals("chunked")){
                return true;
            }
        }
        return false;
    }

    /**
     * Signals if the request stated a connection close to the server.
     * @return
     */
    public boolean closeConnection(){
        return this.headers.get(HTTPConstants.HTTP_GENERAL_HEADERS.CONNECTION.getRepresentation()).equals("close");
    }
}
