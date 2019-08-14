package com.tasks.profit.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

//@XmlRootElement (name="product")
public class Product{
    private int id;
    public String name;
    private Deque<Lot> lot = new ArrayDeque<Lot>();

    public int getId (){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public boolean noDouble(Product p) {
        return name.equals(p.name);
    }

    public void push(Lot lot, int id){
        lot.id = id;
        //this.lot.add(lot);
        this.lot.offerLast(lot);
    }

    public Lot pop(){
        return lot.pollFirst();
    }

    public Lot peek(){
        return lot.peekFirst();
    }

    public List<Lot> getStack(){
        return new ArrayList<>(lot);
    }

}
