package com.tasks.profit.server;


import com.tasks.profit.exception.ErrorMessage;
import com.tasks.profit.exception.NotFoundException;
import com.tasks.profit.exception.ProductAlreadyExists;
import com.tasks.profit.model.Product;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/products")
public class RestServer {
    static private List<Product> productDB = new ArrayList<>();
    static private Integer idProduct = new Integer(0);

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
    public String newLot (Product product){
        Integer id;
        if ((id=indexContain(product)) != null) {
            Product anotherProd = productDB.get(id);
            anotherProd.setCount(product.getCount());
            anotherProd.setPrice(product.getPrice());
            anotherProd.setDate(product.getDate());
        }
        return "OK";
    }

    private static boolean contains (Product product){
        for (Product p : productDB)
            if (p.equals(product)) return true;
        return false;
    }
    private static Integer indexContain (Product product){
        for (int i = 0; i<productDB.size(); i++)
            if (productDB.get(i).equals(product)) return i;
        return null;
    }
}
