package com.ppb.lightweight.web.server.errors;

import com.ppb.lightweight.web.server.internal.HTTPConstants;

/**
 * Created by tony on 21.06.2016.
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
