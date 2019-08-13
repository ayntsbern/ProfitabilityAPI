package com.tasks.profit.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ProductAlreadyExists extends WebApplicationException {
    public ProductAlreadyExists (ErrorMessage message){
        super(Response.status(Response.Status.CONFLICT).entity(message.toString()).type(MediaType.TEXT_PLAIN).build());
    }
}
