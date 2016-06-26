package com.ppb.lightweight.web.server.errors;

import com.ppb.lightweight.web.server.http.HTTPConstants;

/**
 * Internal Server Error is an exception used to notify when the server has to send a client a message because
 * of some internal problems ( e.g. error while reading from a file ).
 *
 * Created by DOBRE Antonel-George on 21.06.2016.
 */
public class InternalServerError extends Exception{

    private String errorCode = HTTPConstants.HTTP_RESPONSE_CODES.INTERNAL_SERVER_ERROR.getRepresentation();

    public InternalServerError(String message){
        super(message);
    }

    public String getErrorCode(){
        return errorCode;
    }

}
