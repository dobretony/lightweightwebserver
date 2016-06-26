package com.ppb.lightweight.web.server.logger;

import java.io.*;

import com.ppb.lightweight.web.server.errors.WebServerInitializationException;
import com.ppb.lightweight.web.server.utils.Configurations;
import com.ppb.lightweight.web.server.utils.Constants;
import com.ppb.lightweight.web.server.utils.Utils;
import org.classpath.icedtea.Config;

/**
 * Created by tony on 14.06.2016.
 */
public class Logger {

    private String outputPath = null;
    private PrintStream out = null;
    private static Logger logger = null;
    private static String MESSAGE_SPECIFIER = "I";
    private static String ERROR_SPECIFIER = "E";
    private static String DEBUG_SPECIFIER = "D";
    private static String VERBOSE_SPECIFIER = "V";

    private Logger(String outputPath) throws WebServerInitializationException{

        if(outputPath == null)
            out = new PrintStream(System.out);
        else
            try{
                out = new PrintStream(outputPath);
            }catch(FileNotFoundException e){
                System.err.println("Could not initialize logger to the specific file. Defaulting to "
                        + Constants.DEFAULT_LOGGER_FILE);
                try{
                    out = new PrintStream(Constants.DEFAULT_LOGGER_FILE);
                }catch(FileNotFoundException err){
                    System.err.println("Could not safely initialize logger. Aborting.");
                    throw new WebServerInitializationException();
                }
            }

    }

    public static void initializeLogger() throws WebServerInitializationException{

        // check if a logs folder exists and create if not
        File directory = new File(Configurations.LOG_FOLDER_PATH);
        if (! directory.exists()){
            if(!directory.mkdir()){
                throw new WebServerInitializationException("Can not create logs directory. Aborting...");
            }
        }

        // create the current log file for this run
        String logFileName = "lightweightwebserver-" + Utils.getCurrentTimestampString() + ".log";
        File logfile = new File(Constants.DEFAULT_LOGGER_OUTPUT_DIRECTORY + File.separator + logFileName);
        try {
            if (!logfile.exists()) {
                logfile.createNewFile();
            }
        }catch(IOException e){
            throw new WebServerInitializationException("Could not create log file. Aborting.");
        }

        // initialize the Logger class
        Logger.logger = new Logger(logfile.getAbsolutePath());

    }

    public static void finalizeLogger() {
        Logger.logger.out.flush();
        Logger.logger.out.close();
    }

    public static synchronized void log(String message){

        if(Logger.logger == null)
            return;

        StringBuilder sb = new StringBuilder();
        sb.append(Utils.getCurrentTimestampString());
        sb.append(" " + Logger.MESSAGE_SPECIFIER);
        sb.append(" " + message);

        Logger.logger.out.println(sb.toString());
    }

    public static synchronized void logE(String message){

        if(Logger.logger == null)
            return;

        StringBuilder sb = new StringBuilder();
        sb.append(Utils.getCurrentTimestampString());
        sb.append(" " + Logger.ERROR_SPECIFIER);
        sb.append(" " + message);

        Logger.logger.out.println(sb.toString());
    }

    public static synchronized void logD(String message){

        if(Configurations.DEBUG == null){
            return;
        }

        if(Logger.logger == null)
            return;

        StringBuilder sb = new StringBuilder();
        sb.append(Utils.getCurrentTimestampString());
        sb.append(" " + Logger.DEBUG_SPECIFIER);
        sb.append(" " + message);

        Logger.logger.out.println(sb.toString());
    }

    public static synchronized void logV(String message){

        if(Configurations.VERBOSE == null){
            return;
        }

        if(Logger.logger == null)
            return;

        StringBuilder sb = new StringBuilder();
        sb.append(Utils.getCurrentTimestampString());
        sb.append(" " + Logger.VERBOSE_SPECIFIER);
        sb.append(" " + message);

        Logger.logger.out.println(sb.toString());
    }


    public static synchronized void logE(Exception e){
        if(Logger.logger == null)
            return;

        StackTraceElement[] elements = e.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for(StackTraceElement element : elements) {
            sb.append(Utils.getCurrentTimestampString());
            sb.append(" " + Logger.ERROR_SPECIFIER);
            sb.append(" " + element.toString());
            sb.append("\n");
        }

        Logger.logger.out.println(sb.toString());
    }

    /**
     * Outputs to terminal.
     * @param message
     */
    public static synchronized void outputWrite(String message){

        System.out.println(message);

    }



}
