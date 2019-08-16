package com.tasks.profit.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotFoundException extends WebApplicationException {
    public NotFoundException(ErrorMessage message){
        super(Response.status(Response.Status.NOT_FOUND).entity(message.toString()).type(MediaType.TEXT_PLAIN).build());
    }
}
