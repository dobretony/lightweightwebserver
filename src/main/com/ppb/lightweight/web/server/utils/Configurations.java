package com.ppb.lightweight.web.server.utils;

/**
 * Class that contains the configurations available for the server,
 * such as log directory path, file root system path, number of active connections, etc.
 *
 * Created by DOBRE Antonel-George on 16.06.2016.
 */
public class Configurations {

    public static String WWW_FOLDER_PATH = Constants.DEFAULT_WWW_FOLDER_PATH;
    public static String LOG_FOLDER_PATH = Constants.DEFAULT_LOGGER_OUTPUT_DIRECTORY;
    public static String SERVER_HOSTNAME = null;
    public static String SERVER_IP_ADDRESS = Constants.DEFAULT_SERVER_IP_ADDRESS;

    public static int PORT_NUMBER = Constants.DEFAULT_PORT_NUMBER;
    public static int CLIENT_SOCKET_TIMEOUT = Constants.DEFAULT_CLIENT_SOCKET_TIMEOUT;

    public static int NO_OF_ACTIVE_CONN = Constants.DEFAULT_ACTIVE_CONNECTIONS;
    public static int MAX_LENGTH_OF_REQUEST_CONTENT = Constants.DEFAULT_MAX_LENGTH_OF_REQUEST_CONTENT;

    public static String FILE_NOT_FOUND_MESSAGE_FILE = null;
}
