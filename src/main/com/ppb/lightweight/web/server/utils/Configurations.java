package com.ppb.lightweight.web.server.utils;

/**
 * Class that contains the configurations available for the server,
 * such as log directory path, file root system path, number of active connections, etc.
 *
 * Created by DOBRE Antonel-George on 16.06.2016.
 */
public class Configurations {

    /**
     * Enables DEBUG output for this project, if any value besides null is given.
     *
     */
    public static String DEBUG = null;

    /**
     * Enables VERBOSE output for this project, if any value besides null is given.
     */
    public static String VERBOSE = null;

    /**
     * Path to the folder that contains the root of the web server.
     * The web server checks this folder for any resources.
     */
    public static String WWW_FOLDER_PATH = Constants.DEFAULT_WWW_FOLDER_PATH;

    /**
     * Path to the folder that the server outputs all log files.
     * Log files are generated with the form:
     * lightweightwebserver-<timestap>.log, where timestamp is the
     * time that the server began running.
     */
    public static String LOG_FOLDER_PATH = Constants.DEFAULT_LOGGER_OUTPUT_DIRECTORY;

    /**
     * The server hostname, that it reports in the HTTP header "Server".
     */
    public static String SERVER_HOSTNAME = null;

    /**
     * The IP address of this server. It defaults to "localhost" if none is specified.
     * Users may want to change this address to match a public IP address.
     */
    public static String SERVER_IP_ADDRESS = Constants.DEFAULT_SERVER_IP_ADDRESS;

    /**
     * The portnumber the server should run on. It defaults to 80.
     */
    public static int PORT_NUMBER = Constants.DEFAULT_PORT_NUMBER;

    /**
     * The timeout the server should wait between client requests.
     * It defaults to 100000 ms.
     */
    public static int CLIENT_SOCKET_TIMEOUT = Constants.DEFAULT_CLIENT_SOCKET_TIMEOUT;

    /**
     * The number of active connections that the server can have at the same time.
     * This gives both the maximum number of threads in the Threadpool and the number
     * of sockets the server can accept at any given time. It defaults to 50.
     */
    public static int NO_OF_ACTIVE_CONN = Constants.DEFAULT_ACTIVE_CONNECTIONS;

    /**
     * Because request content is usually stored in memory, there is a maximum amount of
     * Bytes that any request can have. It defaults to 1000000 bytes.
     *
     * A 413 ( Request Entity Too Large) message is given to clients that send a request above this
     * threshold.
     */
    public static int MAX_LENGTH_OF_REQUEST_CONTENT = Constants.DEFAULT_MAX_LENGTH_OF_REQUEST_CONTENT;

    /**
     * This is the path to the file that should be sent if the requested resource is not found.
     * It is the content for 404 (Not Found) messages.
     *
     * If this configuration is not set, a temporary FILE_NOT_FOUND message file is created for each 404 message.
     */
    public static String FILE_NOT_FOUND_MESSAGE_FILE = null;
}
