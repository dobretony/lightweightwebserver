package com.ppb.lightweight.web.server.main;

import com.ppb.lightweight.web.server.errors.WebServerInitializationException;
import com.ppb.lightweight.web.server.internal.FileFactory;
import com.ppb.lightweight.web.server.internal.LightweightWebServer;
import com.ppb.lightweight.web.server.logger.Logger;
import com.ppb.lightweight.web.server.utils.ConfigurationParser;
import com.ppb.lightweight.web.server.utils.Configurations;

import javax.security.auth.login.Configuration;
import java.net.UnknownHostException;

/**
 * Created by DOBRE Antonel-George on 14.06.2016.
 */
public class LightweightWebServerMain {

    public static void main(String[] args){

        int EXIT_CODE = 0;

        try{
            // First check if there is a configuration file
            ConfigurationParser.parseConfigurationFile();
            // initialize Logger
            Logger.initializeLogger();
            // initialize FileSystem
            FileFactory.initializeFileSystem();

            // get an instance of the server
            LightweightWebServer webServer = new LightweightWebServer(Configurations.SERVER_IP_ADDRESS,
                                                                      Configurations.PORT_NUMBER,
                                                                      Configurations.NO_OF_ACTIVE_CONN);
            webServer.run();

        } catch(WebServerInitializationException e){
            System.err.println("There was a problem initializing the server: \n" + e.getLocalizedMessage());
            System.err.println("Aborting..");
            EXIT_CODE = -1;
        } catch (UnknownHostException e){
            System.err.println("Could not bind to specific address. Please check your configuration.");
            System.err.println("Aborting..");
            EXIT_CODE = -1;
        } finally {
            System.out.println("Have a nice day.");
            System.exit(EXIT_CODE);
        }



    }


}
