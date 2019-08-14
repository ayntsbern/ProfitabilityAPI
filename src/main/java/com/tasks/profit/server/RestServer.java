package com.tasks.profit.server;


import com.tasks.profit.exception.ErrorMessage;
import com.tasks.profit.exception.NotFoundException;
import com.tasks.profit.exception.ProductAlreadyExists;
import com.tasks.profit.model.Lot;
import com.tasks.profit.model.Product;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/products")
public class RestServer {
    static private List<Product> productDB = new ArrayList<>();
    static private List<Profit> profits = new ArrayList<>();
    static private Integer idProduct = 0;
    static private Integer idLot = 0;

    @GET
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getAll(){ return productDB; }

    @POST
    @Path("/addAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addAll(List<Product> products){
        for(Product product : products)
            newProduct(product);
        return "OK";
    }

    @POST
    @Path("/newproduct")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String newProduct(Product product){
        if (!productDB.isEmpty()){
            if (contains(product)) {
                ErrorMessage errorMessage = new ErrorMessage();
                throw new ProductAlreadyExists(errorMessage.get409(product.name));
            }
            idProduct++;
        }
        product.setId(idProduct);
        productDB.add(product);
        return "OK";
    }

    @GET
    @Path("/get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product getProduct(@PathParam("id") int id){
        try {
            return productDB.get(id);
        } catch (IndexOutOfBoundsException e){
            ErrorMessage errorMessage = new ErrorMessage();
            throw new NotFoundException(errorMessage.get404());
        }
    }

    @POST
    @Path("/purchase")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String newLot (Lot lot){
        Integer id;
        if ((id=indexContain(lot.name)) != null) {
            Product anotherProd = productDB.get(id);
            if (lot.getPrice() > 0 && lot.getCount() > 0) {
                anotherProd.push(lot, idLot);
                idLot++;
                return "OK";
            }
            else {
                ErrorMessage errorMessage = new ErrorMessage();
                throw new NotFoundException(errorMessage.get1001());
            }
        } else {
            ErrorMessage errorMessage = new ErrorMessage();
            throw new NotFoundException(errorMessage.get404());
        }
    }

    @POST
    @Path("/demand")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String saleLot(Lot lot){
        Integer id;
        if ((id=indexContain(lot.name)) != null){
            Product product = productDB.get(id);
            Profit profit = new Profit(lot.name);
            Lot anotherLot;
            while ((anotherLot = product.peek())!= null & lot.count != 0) {
                if (anotherLot.count > lot.count) {
                    anotherLot.count -= lot.count;
                    profit.calc(lot.count, anotherLot.price, lot.price);
                    lot.count = 0;
                } else if (anotherLot.count < lot.count) {
                    anotherLot = product.pop();
                    lot.count -= anotherLot.count;
                    profit.calc(anotherLot.count, anotherLot.price, lot.price);
                } else {
                    anotherLot = product.pop();
                    profit.calc(lot.count, anotherLot.price, lot.price);
                    lot.count = 0;
                }
                profit.setDate(lot.date);
                profits.add(profit);
            }
        } else {
            ErrorMessage errorMessage = new ErrorMessage();
            throw new NotFoundException(errorMessage.get404());
        }
        if (lot.count == 0) return "OK";
        else {
                ErrorMessage newErrorMessage = new ErrorMessage();
                throw new NotFoundException(newErrorMessage.get1002(lot.count));
        }
    }

    @GET
    @Path("/salesreport")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String salesReport(Lot lot){
        Profit profit;
        for (int i = 0; i<profits.size(); i++){
            profit = profits.get(i);
            if(profit.name.equals(lot.name))
                if(profit.getDate().equals(lot.date))
                    return "Sold for " + profit.getProfit();
        }
        return "Not found!";
    }

    @GET
    @Path("/lots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Lot> lots (){
        List<Lot> lotList = new ArrayList<>();
           for (Product p : productDB) {
               lotList.add(p.peek());
               lotList.add(p.pop());
               lotList.add(p.peek());
           }
        return lotList;
    }

    private static boolean contains (Product product){
        for (Product p : productDB)
            if (p.noDouble(product)) return true;
        return false;
    }
    private static Integer indexContain (String name){
        if (productDB.isEmpty()) return null;
        for (int i = 0; i<productDB.size(); i++)
            if (productDB.get(i).name.equals(name)) return i;
        return null;
    }


    class Profit {
        String name;
        private Integer priceLot = 0;
        private Integer countLot = 0;
        private Integer summaLot = 0;
        private String date;

        Profit(String name){
            this.name = name;
        }
        Profit(){ }

        private void calc (Integer count, Integer price, Integer sum){
            this.countLot += count;
            this.priceLot += price;
            this.summaLot = sum;
        }

        private Integer getProfit(){
            return summaLot*countLot-priceLot;
        }

        void setDate(String newDate){
            date = newDate;
        }

        String getDate(){
            return date;
        }
    }
}
