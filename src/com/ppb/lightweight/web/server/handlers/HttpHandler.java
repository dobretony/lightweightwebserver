package com.ppb.lightweight.web.server.handlers;

import com.ppb.lightweight.web.server.errors.MalformedRequestException;
import com.ppb.lightweight.web.server.internal.HTTPConstants;
import com.ppb.lightweight.web.server.internal.HTTPRequest;
import com.ppb.lightweight.web.server.internal.HTTPResponse;
import com.ppb.lightweight.web.server.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by DOBRE Antonel-George on 14.06.2016.
 */
public class HttpHandler implements Handler{

    private Socket clientSocket = null;
    private BufferedReader clientSocketReader = null;
    private PrintWriter clientSocketWriter = null;
    private InetAddress clientIPAddress = null;
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
            // if we encounter a socket error, then we ignore this socket.
            Logger.logE("Error while reading from socket for client.");
            return;
        } catch (MalformedRequestException e){
            // we have identified a malformed request
            // we close the connection gracefully
            HTTPResponse response = HTTPResponse.getCloseMessage(
                    Integer.parseInt(HTTPConstants.HTTP_RESPONSE_CODES.BAD_REQUEST.getRepresentation()));
            sendResponse(response);
        }


    }

    public void sendResponse(HTTPResponse httpResponse){



    }


}
