package com.tasks.profit.server;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;

import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class RestServerTest extends JerseyTest {

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(RestServer.class);
    }

    @Test
    public void getAll() {
        Response response = target("/products/addall").
                request().post(Entity.json("[{\"name\":\"Apple\"},{\"name\":\"Samsung\"}]"));
        String json = target("/products/getall").request().get(String.class);

        assertThat(json, containsString("[{\"id\":0,\"name\":\"Apple\",\"stack\":[]},{\"id\":1,\"name\":\"Samsung\",\"stack\":[]}]"));
    }

    @Test
    public void addAll_various() {
        Response response = target("/products/addall").
                request().post(Entity.json("[{\"name\":\"Xiaomi\"},{\"name\":\"Sony\"}]"));
        String content = response.readEntity(String.class);

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
        assertEquals("Should return OK", "OK", content);
    }

    @Test
    public void addAll_theSame(){
        Response response = target("/products/addall").
                request().post(Entity.json("[{\"name\":\"Huawei\"},{\"name\":\"ChinaMobile\"},{\"name\":\"Huawei\"}]"));
        String content = response.readEntity(String.class);

        assertEquals("Http Response should be 409: ", Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
        assertEquals("Should return Error", "Code: 409\n" + "Description: Product Huawei already exists!\n" +
                                                            "Error status: Conflict", content);
    }

    @Test
    public void newProduct() {
        Response response = target("products/newproduct").
                request().post(Entity.json("{\"name\" : \"Meizu\"}"));
        String content = response.readEntity(String.class);

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
        assertEquals("Should return OK", "OK", content);
    }

    @Test
    public void newLot_withExistingName() {
        target("products/newproduct").request().post(Entity.json("{\"name\" : \"Apple\"}"));

        Response response = target("products/purchase").
                request().post(Entity.json("{\n" +
                "\t\"name\" : \"Apple\",\n" +
                "\t\"count\": 2,\n" +
                "\t\"price\": 2000,\n" +
                "\t\"date\" : \"05.05.2019\" \n" +
                "}"));
        String content = response.readEntity(String.class);

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
        assertEquals("Should return OK", "OK", content);
    }

    @Test
    public void newLot_withNOExistingName() {
        target("products/newproduct").request().post(Entity.json("{\"name\" : \"RussianPhone\"}"));

        Response response = target("products/purchase").
                request().post(Entity.json("{\n" +
                "\t\"name\" : \"Lenovo\",\n" +
                "\t\"count\": 2,\n" +
                "\t\"price\": 2000,\n" +
                "\t\"date\" : \"05.05.2019\" \n" +
                "}"));
        String content = response.readEntity(String.class);

        assertEquals("Http Response should be 404: ", Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
        assertEquals("Should return OK", "Code: 404\n" + "Description: Product not found!\n" +
                                                        "Error status: Not Found", content);
    }

    @Test
    public void newLot_withNegativePriceAndCount() {
        target("products/newproduct").request().post(Entity.json("{\"name\" : \"Apple\"}"));

        Response response1 = target("products/purchase").
                request().post(Entity.json("{\n" +
                "\t\"name\" : \"Apple\",\n" +
                "\t\"count\": 2,\n" +
                "\t\"price\": -2000,\n" +
                "\t\"date\" : \"05.05.2019\" \n" +
                "}"));
        Response response2 = target("products/purchase").
                request().post(Entity.json("{\n" +
                "\t\"name\" : \"Apple\",\n" +
                "\t\"count\": -2,\n" +
                "\t\"price\": 2000,\n" +
                "\t\"date\" : \"05.05.2019\" \n" +
                "}"));
        String content1 = response1.readEntity(String.class);
        String content2 = response2.readEntity(String.class);

        String error = "Code: 1001\n" + "Description: The price and the count of product must be positive!\n" + "Error status: Conflict";
        assertEquals("Http Response should be 404: ", Response.Status.CONFLICT.getStatusCode(), response1.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN, response1.getHeaderString(HttpHeaders.CONTENT_TYPE));
        assertEquals("Should return error", error, content1);

        assertEquals("Http Response should be 404: ", Response.Status.CONFLICT.getStatusCode(), response2.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN, response2.getHeaderString(HttpHeaders.CONTENT_TYPE));
        assertEquals("Should return error", error, content2);
    }

    @Test
    public void saleLot_whenHaveMoreThenNeed() {
        target("products/newproduct").request().post(Entity.json("{\"name\" : \"Apple\"}"));

        target("products/purchase").
                request().post(Entity.json("{\n \t\"name\" : \"Apple\",\n \t\"count\": 2,\n" +
                "\t\"price\": 2000,\n \t\"date\" : \"05.05.2019\" \n }"));

        Response response = target("products/demand").
                request().post(Entity.json("{\n" +
                "\t\"name\" : \"Apple\",\n" +
                "\t\"count\": 1,\n" +
                "\t\"price\": 5000,\n" +
                "\t\"date\" : \"07.05.2019\" \n" +
                "}"));
        String content = response.readEntity(String.class);

        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
        assertEquals("Should return OK", "OK", content);
    }

    @Test
    public void saleLot_whenHaveLessThenNeed() {
        target("products/newproduct").request().post(Entity.json("{\"name\" : \"Sony Ericsson\"}"));

        target("products/purchase").
                request().post(Entity.json("{\n \t\"name\" : \"Sony Ericsson\",\n \t\"count\": 2,\n" +
                "\t\"price\": 2000,\n \t\"date\" : \"05.05.2019\" \n }"));

        Response response = target("products/demand").
                request().post(Entity.json("{\n" +
                "\t\"name\" : \"Sony Ericsson\",\n" +
                "\t\"count\": 5,\n" +
                "\t\"price\": 5000,\n" +
                "\t\"date\" : \"07.05.2019\" \n" +
                "}"));
        String content = response.readEntity(String.class);

        String error = "Code: 1002\n" + "Description: Not enough products to sell! Sold only 2 of products!\n" + "Error status: Conflict";
        assertEquals("Http Response should be 1002: ", Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
        assertEquals("Should return error", error, content);
    }

    @Test
    public void salesReport() {
        target("products/newproduct").request().post(Entity.json("{\"name\" : \"HUAWEI\"}"));

        target("products/purchase").
                request().post(Entity.json("{\n \t\"name\" : \"HUAWEI\",\n \t\"count\": 1,\n" +
                "\t\"price\": 1000,\n \t\"date\" : \"05.05.2019\" \n }"));
        target("products/purchase").
                request().post(Entity.json("{\n \t\"name\" : \"HUAWEI\",\n \t\"count\": 2,\n" +
                "\t\"price\": 2000,\n \t\"date\" : \"05.05.2019\" \n }"));

        target("products/demand").request().post(Entity.json("{\n \t\"name\" : \"HUAWEI\",\n" +
                "\t\"count\": 2,\n \t\"price\": 5000,\n \t\"date\" : \"07.05.2019\" \n }"));


        String response = target("products/salesreport").queryParam("name", "HUAWEI").queryParam("date","07.05.2019").
                request().accept(MediaType.TEXT_PLAIN).get(String.class);


        assertThat(response, containsString("Sold for 7000"));
    }
}