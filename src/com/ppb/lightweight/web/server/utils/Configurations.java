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

    public static int NO_OF_ACTIVE_CONN = Constants.DEFAULT_ACTIVE_CONNECTIONS;
}
