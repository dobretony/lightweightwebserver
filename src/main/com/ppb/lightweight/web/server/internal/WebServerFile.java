package com.ppb.lightweight.web.server.internal;

import com.ppb.lightweight.web.server.errors.MalformedRequestException;
import com.ppb.lightweight.web.server.logger.Logger;
import com.ppb.lightweight.web.server.utils.Configurations;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileTypeDetector;
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

    /**
     * Returns a String containing the content type that should be specified
     * in the HTTP Response header field Content-type.
     *
     * The type is determined by utilizing Files.probeContentType(filePath), which should
     * return the MIME type of this WebServerFile.
     *
     * @return
     */
    public String getContentType(){
        String type;
        Path filePath = FileSystems.getDefault().getPath(this.getAbsolutePath());
        try {
            type = Files.probeContentType(filePath);
            if(type == null)
                type = "";
        } catch(IOException e){
            Logger.logE("Could not correctly determine content type of file " + filePath.toString() + ".");
            type = "";
        }
        return type;
    }

    /**
     * Creates a temporary HTML file with a directory listing based on this webserver file.
     *
     * Returns the reference to the directory listing file.
     *
     * @return
     */
    public WebServerFile createDirectoryListing(){

        WebServerFile newFile = null;
        SimpleDateFormat formatter = new SimpleDateFormat(HTTPConstants.RFC1123_DATE_FORMAT);

        try {
            newFile = new WebServerFile(File.createTempFile("directoryListing", ".html"));
        } catch(IOException e){
            Logger.logE("Could not create temporary file with directory listing for directory: " + this.toPath());
            return null;
        }

        File files[] = this.listFiles();

        StringBuilder sb = new StringBuilder();
        sb.append("\n<html>");
        sb.append("\n<head>");
        sb.append("\n<style>");
        sb.append("\n</style>");
        sb.append("\n<title>List of files/dirs under " + this.getName());
        sb.append("\n</head>");
        sb.append("\n<body>");
        sb.append("\n<div class=\"datagrid\">");
        sb.append("\n<table>");
        sb.append("\n<caption>Directory Listing</caption>");
        sb.append("\n<thead>");
        sb.append("\n	<tr>");
        sb.append("\n		<th>File</th>");
        sb.append("\n		<th>Dir ?</th>");
        sb.append("\n		<th>Size</th>");
        sb.append("\n		<th>Date</th>");
        sb.append("\n	</tr>");
        sb.append("\n</thead>");
        sb.append("\n<tfoot>");
        sb.append("\n	<tr>");
        sb.append("\n		<th>File</th>");
        sb.append("\n		<th>Dir ?</th>");
        sb.append("\n		<th>Size</th>");
        sb.append("\n		<th>Date</th>");
        sb.append("\n	</tr>");
        sb.append("\n</tfoot>");
        sb.append("\n<tbody>");

        for (int i = 0; i < files.length; i++) {
            if(i % 2 == 0) sb.append("\n\t<tr class='alt'>");
            else sb.append("\n\t<tr>");
            if (files[i].isDirectory()){
                sb.append("\n\t\t<td><a href='" + this.getName() + files[i].getName() + "/'>" +
                        files[i].getName() + "</a></td>" +
                        "<td>Y</td>" + "<td>" + files[i].length() +
                        "</td>" + "<td>" + formatter.format(new Date(files[i].lastModified())) + "</td>\n\t</tr>");
            }else{
                sb.append("\n\t\t<td><a href='" + this.getName() + files[i].getName() + "'>" + files[i].getName() + "</a></td>" +
                        "<td> </td>" + "<td>" + files[i].length() +
                        "</td>" + "<td>" + formatter.format(new Date(files[i].lastModified())) + "</td>\n\t</tr>");
            }
        }
        sb.append("\n</tbody>");
        sb.append("\n</table>");
        sb.append("\n</div>");
        sb.append("\n</body>");
        sb.append("\n</html>");

        try {
            FileWriter writer = new FileWriter(newFile);
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        } catch(IOException e){
            newFile = null;
        }

        return newFile;
    }

    /**
     * Get the last modified date of this file in RFC1123 format.
     *
     * @return
     */
    public String getLastModifiedDate(){
        SimpleDateFormat formatter = new SimpleDateFormat(HTTPConstants.RFC1123_DATE_FORMAT);
        return formatter.format(this.lastModified());
    }

}
