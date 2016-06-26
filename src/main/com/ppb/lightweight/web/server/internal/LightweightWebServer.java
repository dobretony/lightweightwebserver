package com.ppb.lightweight.web.server.internal;

import com.ppb.lightweight.web.server.errors.WebServerInitializationException;
import com.ppb.lightweight.web.server.http.HTTPHandler;
import com.ppb.lightweight.web.server.handlers.HandlerFactory;
import com.ppb.lightweight.web.server.logger.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Main class of the application. Handles the initialization of the server socket,
 * listens for client connections and passes the new client sockets to handlers.
 *
 * Also handles the Thread pool for this server.
 *
 * Created by DOBRE Antonel-George on 14.06.2016.
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
                Logger.logD("Accepted a socket from client: " + clientSocket.getInetAddress().toString());
                // here we relinquish the client socket to a handler, once we find out what it wants.
                handleRequest(clientSocket);
            }catch(IOException e){
                Logger.logE("An error was thrown during a connection: " + e.getLocalizedMessage());
            }

        }

        this.cleanUp();

    }

    private void handleRequest(Socket clientSocket){

        HTTPHandler handler = new HTTPHandler(clientSocket);
        this.threadPool.submit(handler);

    }

    private void cleanUp(){

        // first stop all the threads
        this.threadPool.shutdownNow();

        // afterwards close the server socket
        try {
            this.serverSocket.close();
            this.serverSocket = null;
        } catch (IOException e){
            Logger.logE("The server socket was not closed properly.");
        }

    }

    public void stop(){
        this.isRunning = false;
    }

}
