package com.ppb.lightweight.web.server.handlers;

import com.ppb.lightweight.web.server.errors.InternalServerError;
import com.ppb.lightweight.web.server.errors.MalformedRequestException;
import com.ppb.lightweight.web.server.internal.*;
import com.ppb.lightweight.web.server.logger.Logger;
import com.ppb.lightweight.web.server.utils.Configurations;
import com.ppb.lightweight.web.server.utils.Constants;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;

/**
 * Created by DOBRE Antonel-George on 14.06.2016.
 */
public class HttpHandler implements Handler{

    private Socket clientSocket = null;
    private BufferedReader clientSocketReader = null;
    private PrintWriter clientSocketWriter = null;
    private InetAddress clientIPAddress = null;
    /**
     * The current request the handler is working on.
     */
    private HTTPRequest httpRequest = null;

    public HttpHandler(Socket clientSocket){

        this.clientSocket = clientSocket;

    }

    @Override
    public void run(){

        String buffer = null;
        this.clientIPAddress = this.clientSocket.getInetAddress();

        // get the buffers and read the request header
        try {
            this.clientSocketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientSocketWriter = new PrintWriter(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            Logger.logE("Error establishing a socket I/O with client: " + this.clientIPAddress.toString() + ".");
            return;
        }

        // read the first line and determine the request
        try{
            buffer = this.clientSocketReader.readLine();
            this.httpRequest = new HTTPRequest(buffer, this.clientIPAddress);
        } catch (IOException e){
            // if we encounter a socket read error, we try to send an Internal Server error to the client. If that
            // fails, then we just log the message and close the socket.
            Logger.logE("Error while reading from socket for client: " + this.clientIPAddress);
            HTTPResponse response = HTTPResponse.getCloseMessage(
                    HTTPConstants.HTTP_RESPONSE_CODES.INTERNAL_SERVER_ERROR.getRepresentation());
            sendResponse(response);
            closeConnection();
            return;
        } catch (MalformedRequestException e){
            // we have identified a malformed request
            // we close the connection gracefully
            HTTPResponse response = HTTPResponse.getCloseMessage(
                    HTTPConstants.HTTP_RESPONSE_CODES.BAD_REQUEST.getRepresentation());
            sendResponse(response);
            closeConnection();
            return;
        }

        // check if the connection should not continue
        if(!httpRequest.doContinue()){
            closeConnection();
            return;
        }

        if(!httpRequest.hasKeepAlive()){
            return;
        }

        try {
            this.clientSocket.setKeepAlive(true);
            this.clientSocket.setSoTimeout(Configurations.CLIENT_SOCKET_TIMEOUT);
        }catch(SocketException e){
            Logger.logE("Error when setting keep alive on socket for client " + this.clientIPAddress);
            Logger.logE(e);
            HTTPResponse response = HTTPResponse.getCloseMessage(
                    HTTPConstants.HTTP_RESPONSE_CODES.INTERNAL_SERVER_ERROR.getRepresentation());
            sendResponse(response);
            closeConnection();
        }

        while(this.clientSocket != null){

            // here we enter the main loop, we await other requests, besides the initial one.

        }



    }

    private void sendResponse(HTTPResponse httpResponse){

        FileInputStream input = null;
        if(httpResponse.hasResource()){
            try{
                input = httpResponse.getResourceContent();
            }catch(InternalServerError e){
                // we could not read from the content provided, so we close gracefully
                Logger.logE("Could not open the resource for reading.");
                sendResponse(HTTPResponse.getCloseMessage(HTTPConstants.HTTP_RESPONSE_CODES.
                        INTERNAL_SERVER_ERROR.getRepresentation()));
                closeConnection();
                return;
            }
        }

        // print the headers
        this.clientSocketWriter.print(httpResponse.toString());

        // print the content, if it has any
        if(httpResponse.hasResource()){
            try {
                int c;
                while ((c = input.read()) != -1) {
                    this.clientSocketWriter.write(c);
                }
            } catch(IOException e){
                Logger.logE("Could not read from a requested resource. " +
                        "The client may have received an incomplete request.");
            }
        }
        this.clientSocketWriter.println();
        this.clientSocketWriter.flush();

    }

    private void closeConnection(){

        try {
            this.clientSocketWriter.close();
            this.clientSocketReader.close();
            this.clientSocket.close();
        }catch(IOException e){
            Logger.logE("Server encountered an error while trying to close the socket to client "
                    + this.clientIPAddress);
            Logger.logE(e);
        }finally {
            this.clientSocket = null;
        }
    }


    private HTTPResponse resolveRequest(HTTPRequest request){

        switch(request.getRequestType()){
            case GET:
                return resolveGetRequest(request);
            case HEAD:
                break;
            case POST:
                break;
            case OPTIONS:
                break;
            case TRACE:
                break;
            case CONNECT:
                break;
            case DELETE:
                break;
            case PUT:
                break;
            case UNDEFINED:
                break;
            default:
                break;
        }

        return null;
    }

    private HTTPResponse resolveGetRequest(HTTPRequest request){

        // first check for the resource that the get operation needs
        WebServerFile file = FileFactory.searchForURI(request.getRequestURI());
        HTTPResponse response = null;
        String lastModifiedSince = HTTPConstants.HTTP_REQUEST_HEADERS.IF_MODIFIED_SINCE.getRepresentation();
        String lastUnmodifiedSince = HTTPConstants.HTTP_REQUEST_HEADERS.IF_UNMODIFIED_SINCE.getRepresentation();

        if(file == null){
            // return an error HTTPResponse
            return HTTPResponse.getCloseMessage(HTTPConstants.HTTP_RESPONSE_CODES.NOT_FOUND.getRepresentation());
        }

        // if the resource was found, we check to see if this is a conditional GET

        if(request.hasHeader(HTTPConstants.HTTP_REQUEST_HEADERS.IF_RANGE.getRepresentation())){
            if(!request.hasHeader(HTTPConstants.HTTP_REQUEST_HEADERS.RANGE.getRepresentation())){
                // if there is no Range header, then we ignore the IF_RANGE
                    response = HTTPResponse.getOKMessage();
            }
        }

        // check to see if the GET request requires if the modified since file
        if(request.hasHeader(lastModifiedSince)){
            try {
                String timestamp = request.getHeaderValue(lastModifiedSince);

                if (file.checkLastModifiedSince(timestamp)) {
                    // if the file was modified since last request, defined by header value
                    // create a 200 ( OK ) message
                    if(response == null)
                        response = HTTPResponse.getOKMessage();
                }else{
                    // otherwise create a 304 ( Not modified message )
                    if(response == null || response.isOK())
                        response = new HTTPResponse(HTTPConstants.HTTP_RESPONSE_CODES.NOT_MODIFIED);
                }
            }catch(MalformedRequestException e){
                // in case the date is invalid, as expressed by this Exception, then the response is a normal GET
                if(response == null)
                    response = HTTPResponse.getOKMessage();
            }

        }

        if(request.hasHeader(lastUnmodifiedSince)){
            try {
                String timestamp = request.getHeaderValue(lastModifiedSince);

                if (file.checkLastModifiedSince(timestamp)) {
                    // if the file was modified since last request, defined by header value
                    // create a response with  412 (precondition failed)
                    if ( response == null || response.isOK())
                        response = new HTTPResponse(HTTPConstants.HTTP_RESPONSE_CODES.PRECONDITION_FAILED);
                }else{
                    // otherwise create a 200 ( Not modified message )
                    if (response == null)
                        response = HTTPResponse.getOKMessage();
                }
            }catch(MalformedRequestException e){
                // in case the date is invalid, as expressed by this Exception, then the response is a normal GET
                if (response == null)
                    response = HTTPResponse.getOKMessage();
            }
        }

        if(request.hasHeader(HTTPConstants.HTTP_REQUEST_HEADERS.IF_MATCH.getRepresentation())
                || request.hasHeader(HTTPConstants.HTTP_REQUEST_HEADERS.IF_NONE_MATCH.getRepresentation())){
            // currently, server does not support ETags, we ignore this header
            // this is left here for future implementations
        }

        // if the response is ok, and is not a conditional get, then just send the intended resource
        if(response == null)
            response = HTTPResponse.getOKMessage();

        response.setResource(file);

        return response;
    }


}
