package com.ppb.lightweight.web.server.utils;

import static org.junit.Assert.*;
import java.util.Random;

/**
 * Created by tony on 14.06.2016.
 */
public class IPUtilsTest {

    private static String testIPMaxString = "255.255.255.255";
    private static String testIPRandomString = "antantate";
    private static String testIPValidString = "10.0.0.1";
    private static String testIPMinString = "0.0.0.0";
    private static long testIPValidLong = 218791509L;
    private static String testIPValidLongCheck = "13.10.126.85";

    @org.junit.Test
    public void shouldGetIPFromString() throws Exception {

        // generate a random valid IP address
        Random rand = new Random();
        String ipAddress = rand.nextInt(256) + "." + rand.nextInt(256) + "." + rand.nextInt(256) + "." + rand.nextInt(256);

        // check to see if any exceptions occur
        IPUtils.getIPFromString(ipAddress);
    }

    @org.junit.Test
    public void shouldGetIPMinMaxFromString() throws Exception {
        IPUtils.getIPFromString(IPUtilsTest.testIPMaxString);
        IPUtils.getIPFromString(IPUtilsTest.testIPMinString);
    }

    @org.junit.Test (expected = Exception.class)
    public void shouldNotGetIPFromString() throws Exception {
        IPUtils.getIPFromString(IPUtilsTest.testIPRandomString);
    }

    @org.junit.Test
    public void shouldGenerateIPAddressToString() throws Exception {
        String result = IPUtils.ipAddressToString(IPUtilsTest.testIPValidLong);
        assertEquals(result, IPUtilsTest.testIPValidLongCheck);
    }

}