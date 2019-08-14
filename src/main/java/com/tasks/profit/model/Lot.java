package com.tasks.profit.model;

public class Lot {
    public int id;
    public String name;
    public Integer count;
    public Integer price;
    public String date;

    public Integer getCount(){
        return count;
    }
    public void setCount(Integer count){
        this.count = count;
    }
    public Integer getPrice(){
        return price;
    }
    public void setPrice(Integer price){
        this.price = price;
    }
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }
}
