package com.tasks.profit.exception;

import javax.ws.rs.core.Response;

public class ErrorMessage {
    private String code;
    private String description;
    private Response.Status status;

    public ErrorMessage(){}
    public ErrorMessage(String code, String description, Response.Status status){
        this.code = code;
        this.description = description;
        this.status = status;
    }

    public ErrorMessage get404(){
        code = "404";
        description = "Product not found!";
        status = Response.Status.NOT_FOUND;
        return this;
    }

    public ErrorMessage get409(String name){
        code = "409";
        description = "Product " + name + " already exists!";
        status = Response.Status.CONFLICT;
        return this;
    }

    public ErrorMessage get1001(){
        code = "1001";
        description = "The price and the count of product must be positive!";
        status = Response.Status.CONFLICT;
        return this;
    }

    public ErrorMessage get1002(Integer count){
        code = "1002";
        description = String.format("Not enough products to sell! Sold only %s of products!", count);
        status = Response.Status.CONFLICT;
        return this;
    }

    @Override
    public String toString(){
        return String.format("Code: %s\n" +
                "Description: %s\n" +
                "Error status: %s",
                code, description, status.toString());
    }
}
