package com.ppb.lightweight.web.server.utils;

import com.ppb.lightweight.web.server.errors.WebServerInitializationException;
import com.ppb.lightweight.web.server.logger.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class that handles the parsing of the configuration file.
 *
 * The configuration file cannot be changed, it is always lightweightwebserver.config,
 * based in the same directory the web server started.
 *
 *
 * Created by DOBRE Antonel-George on 25.06.2016.
 */
public class ConfigurationParser {

    /**
     * Method that parses the configuration file for the Lightweight Web Server.
     *
     * The configuration file is a file present in the same directory as the server with the name
     * "lightweightwebserver.config" .
     *
     * This method changes the variables of the Configurations class to the ones specified by the configuration
     * file.
     *
     */
    public static void parseConfigurationFile() throws WebServerInitializationException{

        Path file = Paths.get(Constants.CONFIGURATION_FILE);

        if(!Files.exists(file)){
            Logger.log("Could not find a configuration file, defaulting to constants.");
            return;
        }
        if(!Files.isReadable(file)){
            Logger.log("Could not read from configuration file, defaulting to constants.");
            return;
        }

        BufferedReader bf = null;
        Charset charset = Charset.defaultCharset();
        try{
            bf = Files.newBufferedReader(file, charset);
            String buffer;

            while((buffer = bf.readLine()) != null){

                // if it is a new line or a comment, then ignore it
                if(buffer.equals("") || buffer.startsWith("#")){
                    continue;
                }

                String params[] = buffer.split("=", 2);
                if(params.length != 2){
                    // it means the configuration line is invalid
                    // we throw an exception in this case
                    throw new WebServerInitializationException("Configuration file is invalid.");
                }

                // we use java reflection to get all the static members of the Configuration
                // class and match them to the items in the configuration file
                try {
                    Class<?> c = Class.forName("com.ppb.lightweight.web.server.utils.Configurations");

                    for(Field field : c.getFields()){
                        if(java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                                field.getName().equalsIgnoreCase(params[0])){
                            // we have a match
                            try {
                                field.setAccessible(true);
                                if(field.getType().equals(String.class)) {
                                    field.set(null, params[1]);
                                }else if(field.getType().equals(int.class)) {
                                    field.set(null, Integer.parseInt(params[1]));
                                }
                            } catch(IllegalAccessException e) {
                                Logger.logE("Ignoring Configuration field: " + field.getName());
                            } catch(NumberFormatException e) {
                                Logger.logE("Configuration field: " + field.getName() + " is of type integer.");
                            }
                            break;
                        }
                    }

                } catch(ClassNotFoundException e){
                    Logger.logE("Could not assess configuration file. Resulting to default configurations.");
                }

            }
        } catch (IOException e){
            Logger.log("Could not read from configuration file, defaulting to constants.");
        } finally {
            try {
                if(bf != null)
                    bf.close();
            } catch(IOException e){
                Logger.logE("Problem while closing the configuration file.");
            }
        }



    }

}
