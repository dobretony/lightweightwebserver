package com.ppb.lightweight.web.server.utils;

import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Created by tony on 14.06.2016.
 */
public class IPUtils {

    /**
     * Transforms an IP Address from string to a long.
     * @param ipAddress
     * @return
     * @throws UnknownHostException
     */
    public static long getIPFromString(String ipAddress) throws UnknownHostException{
        int[] decimals = new int[4];
        long ipAddressLong = 0L;
        try{
            String[] tokens = ipAddress.split(Pattern.quote("."));
            if(tokens.length != 4) {
                throw new UnknownHostException();
            }

            for(int i = 0; i < 4; i++){
                int value = Integer.parseInt(tokens[i]);
                if(value < 0 || value > 256){
                    throw new UnknownHostException();
                }
                decimals[i] = value;
            }

            ipAddressLong = (decimals[0] << 24) + (decimals[1] << 16) + (decimals[2] << 8) + decimals[3];

        } catch(NumberFormatException e){
            throw new UnknownHostException();
        }

        return ipAddressLong;
    }

    /**
     * Converts an IP Address from its long representation to its String format.
     * @param ipAddress
     * @return
     */
    public static String ipAddressToString(long ipAddress){

        StringBuilder sb = new StringBuilder();
        long aux = 0L;

        aux = (ipAddress >> 24) & 255;
        sb.append(aux);
        aux = (ipAddress >> 16) & 255;
        sb.append("." + aux);
        aux = (ipAddress >> 8) & 255;
        sb.append("." + aux);
        aux = ipAddress & 255;
        sb.append("." + aux);

        return sb.toString();
    }

}
