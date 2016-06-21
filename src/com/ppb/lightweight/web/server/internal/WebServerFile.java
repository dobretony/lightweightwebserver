package com.ppb.lightweight.web.server.internal;

import com.ppb.lightweight.web.server.errors.MalformedRequestException;
import com.ppb.lightweight.web.server.logger.Logger;

import java.io.File;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Wrapper over java.io.File class, specifically for Lightweight WebServer.
 *
 * The purpose of this class is for added security ( do not create files that are outside the server root directory )
 * and help with resource identification and modficiations.
 *
 * For future development, this class is needed to check and generate dynamic content.
 *
 * Created by DOBRE Antonel-George on 21.06.2016.
 */
public class WebServerFile extends File {

    public WebServerFile(File file){
        super(file.getAbsolutePath());
    }

    public WebServerFile(File parent, String child){
        super(parent, child);
    }

    public WebServerFile(String pathname){
        super(pathname);
    }

    public WebServerFile(String parent, String child){
        super(parent, child);
    }

    public WebServerFile(URI uri){
        super(uri);
    }

    /**
     * Checks if the file was modified since a timestamp expressed in HTTP-Date format
     * ( expressed in HTTPConstants.RFC1123_DATE_FORMAT).
     *
     * Returns true if file was modified since timestamp, false otherwise.
     *
     * @throws MalformedRequestException
     * @return
     */
    public boolean checkLastModifiedSince(String timestamp) throws MalformedRequestException{

        SimpleDateFormat sdf = new SimpleDateFormat(HTTPConstants.RFC1123_DATE_FORMAT);
        String lastModifiedString = sdf.format(this.lastModified());
        try{
            Date timestampDate = sdf.parse(timestamp);
            Date lastModifiedSinceDate = sdf.parse(lastModifiedString);
            return timestampDate.before(lastModifiedSinceDate);
        } catch(ParseException e){
            throw new MalformedRequestException("There was a parse exception in the last modified since header of a request.");
        }

    }

}
