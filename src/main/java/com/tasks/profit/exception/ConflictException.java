package com.tasks.profit.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ConflictException extends WebApplicationException {
    public ConflictException(ErrorMessage message){
        super(Response.status(Response.Status.CONFLICT).entity(message.toString()).type(MediaType.TEXT_PLAIN).build());
    }
}
