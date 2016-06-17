package com.ppb.lightweight.web.server.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 16.06.2016.
 */
public class HTTPConstants {

    /**
     * Date format used by HTTP 1.1 as defined in RFC 1123.
     *
     */
    public static final String RFC1123_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";


    /**
     * Enumeration containing HTTP request types.
     * Defined in Hypertext Transfer Protocol -- HTTP/1.1, RFC 2616, June 1999.
     * Section 5.1.1 Method
     */
    public enum REQUEST_TYPE {
        OPTIONS("OPTIONS"),
        GET("GET"),
        HEAD("HEAD"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        TRACE("TRACE"),
        CONNECT("CONNECT"),
        UNDEFINED("UNDEFINED");

        private final String representation;
        REQUEST_TYPE(String representation){
            this.representation = representation;
        }

        String getRepresentation(){
            return this.representation;
        }


        public static REQUEST_TYPE getRequestType(String requestLine){
            if(requestLine.contains("GET"))
                return REQUEST_TYPE.GET;
            else if(requestLine.contains("POST"))
                return REQUEST_TYPE.POST;
            else if(requestLine.contains("HEAD"))
                return REQUEST_TYPE.HEAD;
            else if(requestLine.contains("OPTIONS"))
                return REQUEST_TYPE.OPTIONS;
            else if(requestLine.contains("PUT"))
                return REQUEST_TYPE.PUT;
            else if(requestLine.contains("TRACE"))
                return REQUEST_TYPE.TRACE;
            else if(requestLine.contains("DELETE"))
                return REQUEST_TYPE.DELETE;
            else if(requestLine.contains("CONNECT"))
                return REQUEST_TYPE.CONNECT;
            else
                return REQUEST_TYPE.UNDEFINED;
        }

    }

    /**
     * Enumeration containing HTTP headers that are present both in HTTP Requests and Responses.
     * Defined in Hypertext Transfer Protocol -- HTTP/1.1, RFC 2616, June 1999.
     * Section 4.5 General Header Fields
     */
    public enum HTTP_GENERAL_HEADERS {

        CACHE_CONTROL("Cache-Control"),
        CONNECTION("Connection"),
        DATE("Date"),
        PRAGMA("Pragma"),
        TRAILER("Trailer"),
        TRANSFER_ENCODING("Transfer-Encoding"),
        UPGRADE("Upgrade"),
        VIA("Via"),
        WARNING("Warning");

        private final String headerString;
        HTTP_GENERAL_HEADERS(String headerString){
            this.headerString = headerString;
        }

        String getRepresentation(){
            return this.headerString;
        }
    }


    /**
     * Enumeration containing HTTP headers that can be present in a HTTP Request.
     * Defined in Hypertext Transfer Protocol -- HTTP/1.1, RFC 2616, June 1999.
     * Section 5.3 Request Header Fields
     */
    public enum HTTP_REQUEST_HEADERS {

        ACCEPT("Accept"),
        ACCEPT_CHARSET("Accept-Charset"),
        ACCEPT_ENCODING("Accept-Encoding"),
        ACCEPT_LANGUAGE("Accept-Language"),
        AUTHORIZATION("Authorization"),
        EXPECT("Expect"),
        FROM("From"),
        HOST("Host"),
        IF_MATCH("If-Match"),
        IF_MODIFIED_SINCE("If-Modified-Since"),
        IF_NONE_MATCH("If-None-Match"),
        IF_RANGE("If-Range"),
        IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
        MAX_FORWARDS("Max-Forwards"),
        PROXY_AUTHORIZATION("Proxy-Authorization"),
        RANGE("Range"),
        REFERER("Referer"),
        TE("TE"),
        USER_AGENT("User-Agent");

        private final String headerString;
        HTTP_REQUEST_HEADERS(String headerString){
            this.headerString = headerString;
        }

        String getRepresentation(){
            return this.headerString;
        }

    }


    /**
     * Enumeration containing HTTP headers that can be present in a HTTP Response.
     * Defined in Hypertext Transfer Protocol -- HTTP/1.1, RFC 2616, June 1999.
     * Section 6.2 Response Header Fields
     */
    public enum HTTP_RESPONSE_HEADERS {

        ACCEPT_RANGES("Accept-Ranges"),
        AGE("Age"),
        ETAG("ETag"),
        LOCATION("Location"),
        PROXY_AUTHENTICATE("Proxy-Authenticate"),
        RETRY_AFTER("Retry-After"),
        SERVER("Server"),
        VARY("Vary"),
        WWW_AUTHENTICATE("WWW-Authenticate");


        private final String headerString;
        HTTP_RESPONSE_HEADERS(String headerString){
            this.headerString = headerString;
        }

        String getRepresentation(){
            return this.headerString;
        }

    }


    /**
     * Enumeration containing HTTP headers that can be present in a HTTP Entity.
     * Defined in Hypertext Transfer Protocol -- HTTP/1.1, RFC 2616, June 1999.
     * Section 7.1 Entity Header Fields
     */
    public enum HTTP_ENTITY_HEADERS {

        ALLOW("Allow"),
        CONTENT_ENCODING("Content-Encoding"),
        CONTENT_LANGUAGE("Content-Language"),
        CONTENT_LENGTH("Content-Length"),
        CONTENT_LOCATION("Content-Location"),
        CONTENT_MD5("Content-MD5"),
        CONTENT_RANGE("Content-Range"),
        CONTENT_TYPE("Content-Type"),
        EXPIRES("Expires"),
        LAST_MODIFIED("Last-Modified");

        private final String headerString;
        HTTP_ENTITY_HEADERS(String headerString){
            this.headerString = headerString;
        }

        String getRepresentation(){
            return this.headerString;
        }

    }

    /**
     * Enumeration with all the HTTP Response Codes as defined in
     * Hypertext Transfer Protocol -- HTTP/1.1, RFC 2616, June 1999, Section 6.1.1
     * The comments next to each code represents the section in RFC 2616 where that particular code is explained.
     */
    public enum HTTP_RESPONSE_CODES {

        CONTINUE(100, "Continue"), // Section 10.1.1: Continue
        SWITCHING_PROTOCOLS(101, "Switching Protocols"), // Section 10.1.2: Switching Protocols
        OK(200, "OK"), // Section 10.2.1: OK
        CREATED(201, "Created"), // Section 10.2.2: Created
        ACCEPTED(202, "Accepted"), // Section 10.2.3: Accepted
        NON_AUTHORITATIVE(203, "Non-Authoritative Information"), // Section 10.2.4: Non-Authoritative Information
        NO_CONTENT(204, "No Content"),  // Section 10.2.5: No Content
        RESET_CONTENT(205, "Reset Content"),  // Section 10.2.6: Reset Content
        PARTIAL_CONTENT(206, "Partial Content"), // Section 10.2.7: Partial Content
        MULTIPLE_CHOICES(300, "Multiple Choices"),  // Section 10.3.1: Multiple Choices
        MOVED_PERMANENTLY(301, "Moved Permanently"), // Section 10.3.2: Moved Permanently
        FOUND(302, "Found"), // Section 10.3.3: Found
        SEE_OTHER(303, "See Other"),  // Section 10.3.4: See Other
        NOT_MODIFIED(304, "Not Modified"), // Section 10.3.5: Not Modified
        USE_PROXY(305, "use Proxy"),  // Section 10.3.6: Use Proxy
        TEMPORARY_REDIRECT(307, "Temporary Redirect"),  // Section 10.3.8: Temporary Redirect
        BAD_REQUEST(400, "Bad Request"),  // Section 10.4.1: Bad Request
        UNAUTHORIZED(401, "Unauthorized"), // Section 10.4.2: Unauthorized
        PAYMENT_REQUIRED(402, "Payment Required"),  // Section 10.4.3: Payment Required
        FORBIDDEN(403, "Forbidden"),  // Section 10.4.4: Forbidden
        NOT_FOUND(404, "Not Found"),  // Section 10.4.5: Not Found
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),  // Section 10.4.6: Method Not Allowed
        NOT_ACCEPTABLE(406, "Not Acceptable"),  // Section 10.4.7: Not Acceptable
        PROXY_AUTHETICATION_REUIRED(407, "Proxy Authentication Required"),  // Section 10.4.8: Proxy Authentication Required
        REQUEST_TIME_OUT(408, "Request Time-out"), // Section 10.4.9: Request Time-out
        CONFLICT(409, "Conflict"), // Section 10.4.10: Conflict
        GONE(410, "Gone"),  // Section 10.4.11: Gone
        LENGTH_REQUIRED(411, "Length Required"),  // Section 10.4.12: Length Required
        PRECONDITION_FAILED(412, "Precondition Failed"),  // Section 10.4.13: Precondition Failed
        REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),  // Section 10.4.14: Request Entity Too Large
        REQUEST_URI_TOO_LARGE(414, "Request-URI Too Large"), // Section 10.4.15: Request-URI Too Large
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"), // Section 10.4.16: Unsupported Media Type
        REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested range not satisfiable"), // Section 10.4.17: Requested range not satisfiable
        EXPECTATION_FAILED(417, "Expectation Failed"),  // Section 10.4.18: Expectation Failed
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"), // Section 10.5.1: Internal Server Error
        NOT_IMPLEMENTED(501, "Not Implemented"),  // Section 10.5.2: Not Implemented
        BAD_GATEWAY(502, "Bad Gateway"), // Section 10.5.3: Bad Gateway
        SERVICE_UNAVAILABLE(503, "Service Unavailable"),  // Section 10.5.4: Service Unavailable
        GATEWAY_TIMEOUT(504, "Gateway Time-out"),  // Section 10.5.5: Gateway Time-out
        HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version not supported"); // Section 10.5.6: HTTP Version not supported


        private final int code;
        private final String representation;
        HTTP_RESPONSE_CODES(int code, String representation){
            this.code = code;
            this.representation = representation;
        }

        public String getRepresentation() {
            return representation;
        }
    }

    // a list containing the string representation of HTTP GENERAL HEADER Enumeration
    // this list is more for caching purposes
    private static List<String> httpGeneralHeadersStringList = null;
    public static List<String> getGeneralHeadersStringList() {
        if (httpGeneralHeadersStringList == null) {
            httpGeneralHeadersStringList = new ArrayList<>();
            for (HTTP_GENERAL_HEADERS header : HTTP_GENERAL_HEADERS.values()) {
                httpGeneralHeadersStringList.add(header.getRepresentation());
            }
        }

        return httpGeneralHeadersStringList;
    }

    private static List<String> httpRequestHeadersStringList = null;
    public static List<String> getHttpRequestHeadersStringList() {
        if (httpRequestHeadersStringList == null) {
            httpRequestHeadersStringList = new ArrayList<>();
            for (HTTP_REQUEST_HEADERS header : HTTP_REQUEST_HEADERS.values()) {
                httpRequestHeadersStringList.add(header.getRepresentation());
            }
        }
        return httpRequestHeadersStringList;
    }

    private static List<String> httpResponseHeaderStringList = null;
    public static List<String> getHttpResponseHeaderStringList() {
        if (httpResponseHeaderStringList == null) {
            httpResponseHeaderStringList = new ArrayList<>();
            for (HTTP_RESPONSE_HEADERS header : HTTP_RESPONSE_HEADERS.values()) {
                httpResponseHeaderStringList.add(header.getRepresentation());
            }
        }
        return httpResponseHeaderStringList;
    }


}
