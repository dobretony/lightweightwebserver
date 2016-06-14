package com.ppb.lightweight.web.server.internal;

import com.ppb.lightweight.web.server.errors.WebServerInitializationException;
import com.ppb.lightweight.web.server.handlers.HandlerFactory;
import com.ppb.lightweight.web.server.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ExecutorService;

/**
 * Created by tony on 14.06.2016.
 */
public class LightweightWebServer {

    private String ipAddressString = null;
    private InetAddress ipAddress = null;
    private int port = 0;
    private ServerSocket serverSocket = null;
    private HandlerFactory handlerFactory = null;
    private boolean isRunning = false;

    /**
     * Variable that holds the maximum number of allowed connections on the server socket
     * and the maximum server thread pool. Default is 0.
     */
    private int maxClients = 0;
    private ExecutorService threadPool = null;

    public LightweightWebServer(String ipAddress, int port, int maxClients) throws UnknownHostException, WebServerInitializationException{
        this.ipAddressString = ipAddress;
        this.port = port;
        this.maxClients = maxClients;

        this.ipAddress = InetAddress.getByName(this.ipAddressString);

        //try and bind the server socket
        try {
            this.serverSocket = new ServerSocket(this.port, this.maxClients, this.ipAddress);
        } catch(IOException e){
            throw new WebServerInitializationException("The server socket cannot be binded to: " +
                        this.ipAddressString + ":" + this.port);
        }

        // initialize the thread pool
        if(maxClients != 0)
            threadPool = Executors.newFixedThreadPool(this.maxClients);
        else
            threadPool = Executors.newCachedThreadPool();

        this.handlerFactory = new HandlerFactory();

    }

    public void run(){
        isRunning = true;

        while(isRunning){
            try {
                Socket clientSocket = this.serverSocket.accept();
                // here we relinquish the client socket to a handler, once we find out what it wants.
                handleRequest(clientSocket);
            }catch(IOException e){
                Logger.logE("An error was thrown during a connection: " + e.getLocalizedMessage());
            }

        }
    }

    private void handleRequest(Socket clientSocket){

        //////// TEST CODE from http://techwiki.ordak.org/
        BufferedReader is;
        PrintWriter os;
        String request;

        try{
            String webServerAddress = clientSocket.getInetAddress().toString();
            Logger.log("Accepted connection from " + webServerAddress);
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //request = is.readLine();
            //Logger.log("Server received request from client: " + request);

            while ( (request = is.readLine()) != null){
                System.out.println("Message: " + request);
            }

            os = new PrintWriter(clientSocket.getOutputStream(), true);
            os.println("HTTP/1.1 200");
            os.println("Content-type: text/html");
            os.println("Server-name: myserver");
            String response = "<html><head>" +
                    "<title>Simpl Web Page</title></head>\n" +
                    "<h1>Congratulations!!!</h1>\n" +
                    "<h3>This page was returned by " + webServerAddress + "</h3>\n" +
                    "<p>This is the first page hosted by your web server.\n</p>" +
                    "Visit <A HREF=\"http://www.techwiki.ordak.org\"> http://www.techwiki.ordak.org</A> for more sample codes.\n" +
                    "</html>\n";
            os.println("Content-length: " + response.length(  ));
            os.println("");
            os.println(response);
            os.flush();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Failed to send response to client: " + e.getMessage());
        } finally {
            if(clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;

    }

}
