package com.ppb.lightweight.web.server.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class with useful static methods for different internal server usages.
 *
 * Created by DOBRE Antonel-George on 14.06.2016.
 */
public class Utils {
    public static String getCurrentTimestampString(){
        return new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT).format(new Date());
    }
}
