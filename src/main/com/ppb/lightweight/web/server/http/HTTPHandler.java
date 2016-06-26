package com.ppb.lightweight.web.server.http;

import com.ppb.lightweight.web.server.errors.MalformedRequestException;
import com.ppb.lightweight.web.server.handlers.Handler;
import com.ppb.lightweight.web.server.http.HTTPConstants;
import com.ppb.lightweight.web.server.http.HTTPResponse;
import com.ppb.lightweight.web.server.internal.*;
import com.ppb.lightweight.web.server.logger.Logger;
import com.ppb.lightweight.web.server.utils.Configurations;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handler for basic HTTP requests received from clients.
 *
 * This class maintains the socket connection with the client and listens for any requests.
 * For each request, a HTTPResponse object is created that can be sent back to the client.
 *
 * Created by DOBRE Antonel-George on 14.06.2016.
 */
public class HTTPHandler implements Handler {

    private Socket clientSocket = null;
    private BufferedReader clientSocketReader = null;
    private PrintWriter clientSocketWriter = null;
    private InetAddress clientIPAddress = null;
    /**
     * The current request the handler is working on.
     */
    private HTTPRequest httpRequest = null;

    public HTTPHandler(Socket clientSocket){

        this.clientSocket = clientSocket;

    }

    @Override
    public void run(){

        String buffer;
        this.clientIPAddress = this.clientSocket.getInetAddress();
        Logger.logD("Started communication with client: " + this.clientIPAddress + ".");

        // get the buffers and read the request header
        try {
            this.clientSocketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientSocketWriter = new PrintWriter(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            Logger.logE("Error establishing a socket I/O with client: " + this.clientIPAddress.toString() + ".");
            return;
        }

        try {
            // we consider keep-alive until stated otherwise
            this.clientSocket.setKeepAlive(true);
            this.clientSocket.setSoTimeout(Configurations.CLIENT_SOCKET_TIMEOUT);
        } catch (SocketException e) {
            Logger.logE("Error when setting keep alive on socket for client " + this.clientIPAddress);
            Logger.logE(e);
            HTTPResponse response = HTTPResponse.getCloseMessage(
                    HTTPConstants.HTTP_RESPONSE_CODES.INTERNAL_SERVER_ERROR);
            sendResponse(response);
            closeConnection();
        }

        // Start the main loop.
        while(this.clientSocket != null) {
            try {
                Logger.logD("Started main loop for client: " + this.clientIPAddress);
                // the first line should be the request type
                buffer = this.clientSocketReader.readLine();
                this.httpRequest = new HTTPRequest(buffer, this.clientIPAddress);

                // read the request headers
                buffer = this.clientSocketReader.readLine();
                while(!buffer.equals("")){
                    this.httpRequest.addHeader(buffer);
                    buffer = this.clientSocketReader.readLine();
                }

                // check to see if the request has content, as defined by headers
                // if it has, then read the content and save it to memory
                if(this.httpRequest.hasContent()){
                    Logger.logD("The request from: " + this.clientIPAddress + " has content.");
                    int contentLength = this.httpRequest.getContentLength();
                    // if the content present in the request exceeds the maximum allowed content
                    // then refuse the request.
                    if(contentLength > Configurations.MAX_LENGTH_OF_REQUEST_CONTENT){
                        HTTPResponse response = HTTPResponse.getCloseMessage(HTTPConstants.HTTP_RESPONSE_CODES.
                                REQUEST_ENTITY_TOO_LARGE);
                        sendResponse(response);
                        closeConnection();
                        break;
                    }
                    char[] resultBuff = new char[0];
                    char[] buff = new char[contentLength];
                    for(int index=0;index<contentLength;
                        index+=this.clientSocketReader.read(buff, index, contentLength - index));
                    this.httpRequest.setContent(buff);

                }

            } catch (IOException e) {
                // if we encounter a socket read error, we try to send an Internal Server error to the client. If that
                // fails, then we just log the message and close the socket.
                Logger.logE("Error while reading from socket for client: " + this.clientIPAddress);
                HTTPResponse response = HTTPResponse.getCloseMessage(
                        HTTPConstants.HTTP_RESPONSE_CODES.INTERNAL_SERVER_ERROR);
                sendResponse(response);
                closeConnection();
                break;
            } catch (MalformedRequestException e) {
                // we have identified a malformed request
                // we close the connection gracefully
                Logger.logE(e.getMessage());
                HTTPResponse response = HTTPResponse.getCloseMessage(HTTPConstants.HTTP_RESPONSE_CODES.BAD_REQUEST);
                sendResponse(response);
                closeConnection();
                break;
            }

            if (!httpRequest.hasKeepAlive()) {
                closeConnection();
                break;
            }

            // after we established the current request, we try and send a response.
            try {
                Logger.logD("Identified a legitimate request, sending a response.");
                HTTPResponse response = this.resolveRequest(httpRequest);
                Logger.logD("Resolved the request with response: " + response.toString());
                sendResponse(response);
            }catch(MalformedRequestException e){
                Logger.logE(e.getMessage());
                HTTPResponse response = HTTPResponse.getCloseMessage(
                        HTTPConstants.HTTP_RESPONSE_CODES.BAD_REQUEST);
                sendResponse(response);
                closeConnection();
                break;
            }
        }

    }

    private void sendResponse(HTTPResponse httpResponse){

        // print the headers
        System.out.println("Sending a response to client " + this.clientIPAddress);
        this.clientSocketWriter.print(httpResponse.toString());
        this.clientSocketWriter.flush();

        // If the response has a resource, then copy that file over to the client output stream
        if(httpResponse.hasResource()){
            try {
                Files.copy(httpResponse.getResource().toPath(), this.clientSocket.getOutputStream());
                this.clientSocket.getOutputStream().flush();
            } catch(IOException e){
                Logger.logE("Could not send full response to client: " + this.clientIPAddress);
            }
        }

        // print a line and flush
        this.clientSocketWriter.print("\r\n");
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


    /**
     * Method that returns a HTTPResponse constructed based on the type of request received by the server.
     *
     * Throws MalformedRequestException if a malformed request was identified.
     *
     * @param request
     * @return
     * @throws MalformedRequestException
     */
    private HTTPResponse resolveRequest(HTTPRequest request) throws MalformedRequestException{

        switch(request.getRequestType()){
            case GET:
                return resolveGetRequest(request);
            case HEAD:
                return resolveHeadRequest(request);
            case POST:
                return resolvePostRequest(request);
            case OPTIONS:
                return resolveOptionsRequest(request);
            case TRACE:
                return resolveTraceRequest(request);
            case CONNECT:
                return resolveConnectRequest(request);
            case DELETE:
                return resolveDeleteRequest(request);
            case PUT:
                return resolvePutRequest(request);
            case UNDEFINED:
            default:
                throw new MalformedRequestException("Undefined request detected.");
        }
    }

    private HTTPResponse resolveGetRequest(HTTPRequest request){

        // first check for the resource that the get operation needs
        WebServerFile file = FileFactory.searchForURI(request.getRequestURI());
        HTTPResponse response = null;
        String lastModifiedSince = HTTPConstants.HTTP_REQUEST_HEADERS.IF_MODIFIED_SINCE.getRepresentation();
        String lastUnmodifiedSince = HTTPConstants.HTTP_REQUEST_HEADERS.IF_UNMODIFIED_SINCE.getRepresentation();

        if(file == null){
            // create and return an error HTTPResponse
            response = HTTPResponse.getCloseMessage(HTTPConstants.HTTP_RESPONSE_CODES.NOT_FOUND);
            String today = new SimpleDateFormat(HTTPConstants.RFC1123_DATE_FORMAT).format(new Date());
            response.addHeader(HTTPConstants.HTTP_GENERAL_HEADERS.DATE.getRepresentation(), today);
            response.addHeader(HTTPConstants.HTTP_ENTITY_HEADERS.CONTENT_TYPE.getRepresentation(), "plain/html");
            // find the File Not Found file
            file = FileFactory.getFileNotFound();
            response.setResource(file);
            return response;
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

        // if the requested resource is a directory and is missing the trailing "/"
        // send the client a HTTP response with 301 (Moved Permenently) with the correct "/" appended
        if(file.isDirectory()){

            String uri = this.httpRequest.getRequestURI();
            uri = uri.replace("/", File.separator);
            if(uri.lastIndexOf(File.separator) != (uri.length()-1)){
                response = new HTTPResponse(HTTPConstants.HTTP_RESPONSE_CODES.MOVED_PERMANENTLY, "Moved Permanently");
                response.addHeader(HTTPConstants.HTTP_RESPONSE_HEADERS.LOCATION.getRepresentation(),
                        "/" + this.httpRequest.getRequestURI() + File.separator);
                return response;
            }

            // if the file is a directory, then create a temporary file with a directory listing
            file = file.createDirectoryListing();

        }

        String contentType = file.getContentType();
        if(!contentType.equals("")){
            // if we could not determine the correct content-type, then the protocol specifies that the recipient
            // should handle determining the content-type.
            response.addHeader(HTTPConstants.HTTP_ENTITY_HEADERS.CONTENT_TYPE.getRepresentation(), contentType);
            response.addHeader(HTTPConstants.HTTP_ENTITY_HEADERS.CONTENT_ENCODING.getRepresentation(), "identity");
        }

        response.addHeader(HTTPConstants.HTTP_ENTITY_HEADERS.LAST_MODIFIED.getRepresentation(),
                file.getLastModifiedDate());


        response.setResource(file);

        return response;
    }

    private HTTPResponse resolvePostRequest(HTTPRequest request){
        HTTPResponse response = null;

        return response;
    }

    /**
     * Resolves a HEAD request and returns a HTTPResponse.
     *
     * The HEAD request is resolved just as a GET Request, only we remove the
     * resource associated with the GET.
     *
     * @param request
     * @return
     */
    private HTTPResponse resolveHeadRequest(HTTPRequest request){
        HTTPResponse response = null;

        response = this.resolveGetRequest(request);
        response.removeResource();

        return response;
    }

    /**
     * Resolves a OPTIONS method request and returns a HTTPResponse.
     *
     * The OPTIONS method states for a given resource on the server what methods can be applied to it.
     *
     * This server has a general set of ALLOWED_OPTIONS that are applied to all resources on the server.
     *
     * @param request
     * @return
     */
    private HTTPResponse resolveOptionsRequest(HTTPRequest request){
        HTTPResponse response = null;

        response = HTTPResponse.getOKMessage();
        StringBuilder sb = new StringBuilder();
        for(HTTPConstants.REQUEST_TYPE req : HTTPConstants.ALLOWED_OPTIONS){
            sb.append(req.getRepresentation());
            sb.append(",");
        }

        response.addHeader(HTTPConstants.HTTP_ENTITY_HEADERS.ALLOW.getRepresentation(), sb.substring(0, sb.length()-2));
        response.addHeader(HTTPConstants.HTTP_ENTITY_HEADERS.CONTENT_TYPE.getRepresentation(), "httpd/unix-directory");

        return response;
    }

    /**
     * TRACE method request is not allowed.
     *
     * @param request
     * @return
     */
    private HTTPResponse resolveTraceRequest(HTTPRequest request){
        HTTPResponse response = null;
        response = HTTPResponse.getCloseMessage(HTTPConstants.HTTP_RESPONSE_CODES.METHOD_NOT_ALLOWED);
        return response;
    }

    /**
     * CONNECT request method is not allowed.
     *
     * @param request
     * @return
     */
    private HTTPResponse resolveConnectRequest(HTTPRequest request){
        HTTPResponse response = null;
        response = HTTPResponse.getCloseMessage(HTTPConstants.HTTP_RESPONSE_CODES.METHOD_NOT_ALLOWED);
        return response;
    }

    /**
     * DELETE request method is not allowed.
     *
     * @param request
     * @return
     */
    private HTTPResponse resolveDeleteRequest(HTTPRequest request){
        HTTPResponse response = null;
        response = HTTPResponse.getCloseMessage(HTTPConstants.HTTP_RESPONSE_CODES.METHOD_NOT_ALLOWED);
        return response;
    }

    /**
     * PUT request method is not allowed.
     *
     * @param request
     * @return
     */
    private HTTPResponse resolvePutRequest(HTTPRequest request){
        HTTPResponse response = null;
        response = HTTPResponse.getCloseMessage(HTTPConstants.HTTP_RESPONSE_CODES.METHOD_NOT_ALLOWED);
        return response;
    }

}
