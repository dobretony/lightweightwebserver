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

    public static LightweightWebServer webServer = null;

    public static void main(String[] args){

        int EXIT_CODE = 0;

        try{
            // add shutdown code in case the VM is shutting down.
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    if(webServer != null){
                        webServer.stop();
                    }
                }
            }));


            // First check if there is a configuration file
            ConfigurationParser.parseConfigurationFile();
            // initialize Logger
            Logger.initializeLogger();
            // initialize FileSystem
            FileFactory.initializeFileSystem();

            // get an instance of the server
            webServer = new LightweightWebServer(Configurations.SERVER_IP_ADDRESS,
                                                                      Configurations.PORT_NUMBER,
                                                                      Configurations.NO_OF_ACTIVE_CONN);
            webServer.run();

        } catch(WebServerInitializationException e){
            Logger.logE("There was a problem initializing the server: \n" + e.getLocalizedMessage());
            Logger.logE("Aborting..");
            EXIT_CODE = -1;
        } catch (UnknownHostException e){
            Logger.logE("Could not bind to specific address. Please check your configuration.");
            Logger.logE("Aborting..");
            EXIT_CODE = -1;
        }

        Logger.log("Have a nice day.");
        Logger.finalizeLogger();
        System.exit(EXIT_CODE);

    }


}
