package com.tasks.profit.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//@XmlRootElement (name="product")
public class Product{
    private int id;
    public String name;
    public Integer count;
    public Integer price;
    public String date;

    public int getId (){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
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

    @Override
    public boolean equals(Object o){
        Product product = (Product)o;
        return this.name.equals(product.name);
    }
}
