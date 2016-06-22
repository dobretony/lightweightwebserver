package com.ppb.lightweight.web.server.internal;

import com.ppb.lightweight.web.server.errors.WebServerInitializationException;
import com.ppb.lightweight.web.server.logger.Logger;
import com.ppb.lightweight.web.server.utils.Configurations;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

/**
 * Class that handles the files present on this server.
 * Has special methods for creating File objects, checking permissions and finding resources in this
 * servers file structure.
 *
 * The root of the server file system begins in the 'www' folder included where this server
 * has started running (by default), or the directory specified in the server config file.
 *
 * The server should not have access to any other file other than those present in the server structure.
 *
 * Any call to access files for a request must use this Factory interface to generate the File Object.
 *
 * Default behaviour for files is to not follow symbolic links, as this may compromise security.
 *
 * Created by DOBRE Antonel-George on 16.06.2016.
 */
public class FileFactory {

    public static void initializeFileSystem() throws WebServerInitializationException{

        // first check if the www is present at the location specified, if not create it
        File directory = new File(Configurations.WWW_FOLDER_PATH);
        if (!directory.exists()){
            if(directory.mkdir())
                Logger.log("WWW directory was not present, created it at location: " + Configurations.WWW_FOLDER_PATH);
            else
                throw new WebServerInitializationException("Error while creating WWW directory. Aborting..");
        }

    }


    /**
     * Searches for a given URI path in the server root filesystem and returns a File object
     * for that URI if found.
     *
     * If the URI is absolute ( contains a full valid Internet address ) then it check to see if the domain is on this
     * server. If it is not on this server, it returns NULL.
     *
     * If the URI is a path, then it checks the full file system. If the file does not exist on the server, then
     * the method returns NULL.
     *
     * @param URI
     * @return
     */
    public static WebServerFile searchForURI(String URI){

        // if this is not a resource on this server, then ignore and return a null File object
        if(URI.startsWith("http://")){
            if(Configurations.SERVER_HOSTNAME != null)
                if(URI.contains(Configurations.SERVER_HOSTNAME)){
                    //extract the URI resource
                    URI = URI.replaceFirst("http://", "");
                    URI = URI.replaceFirst(Configurations.SERVER_HOSTNAME, "");
                }
            else
                // we also return null if do not have a method of determining the server host name
                return null;
        }

        // remove leading / from filename
        while(URI.indexOf("/") == 0){
            URI = URI.substring(1);
        }

        // if after removing the leading "/" we have a length of 0, then we display index.html page
        if(URI.length() == 0){
            URI += "index.html";
        }

        // replace "/" with the Files.pathSeparator
        URI=URI.replace("/", File.pathSeparator);

        // check to see if the URI has illegal characters to prevent access to super directories
        if(URI.contains("..") || URI.contains(":") || URI.contains("|")){
            return null;
        }

        Path startingDirectory = Paths.get(Configurations.WWW_FOLDER_PATH);
        Path fileToFind = Paths.get(startingDirectory.toUri() + URI);

        WebServerFile file = new WebServerFile(fileToFind.toFile());
        if(!file.exists())
           return null;
        else if(!file.canRead())
           return null;

        return file;
    }

}
