package test.fujitsu.videostore.backend.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Database Factory.
 * <p>
 * TODO: Should be re-implemented with your file database. Current implementation is just demo for UI testing.
 */
public class DatabaseFactory {

    /**
     * Creates database "connection"/opens database from path.
     * <p>
     * TODO: Implement database parsing, fetching, creation, modification, removing from JSON or YAML file database.
     * Two example files, /db-examples/database.json and /db-examples/database.yaml.
     * Hint: MovieType.databaseId == type field in database files.
     *
     * TODO: Current way of creating next ID is incorrect, make better implementation.
     *
     * @param filePath file path to database
     * @return database proxy for different tables
     */
    public static Database from(String filePath) {

        return new Database() {
            @Override
            public DBTableRepository<Movie> getMovieTable() {

                final List<Movie> movieList = new ArrayList<>();

                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(new FileReader(filePath));
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONArray moviesArray = (JSONArray) jsonObject.get("movie");

                    for (int i = 0; i < moviesArray.size(); i++) {
                        Movie movie = new Movie();

                        JSONObject movieData = (JSONObject) moviesArray.get(i);

                        Number MovieId = (Number) movieData.get("id");
                        movie.setId(MovieId.intValue());

                        String MovieName = (String) movieData.get("name");
                        movie.setName(MovieName);

                        Number MovieStockCount = (Number) movieData.get("stockCount");
                        movie.setStockCount(MovieStockCount.intValue());

                        movieList.add(movie);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (ParseException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }

                return new DBTableRepository<Movie>() {

                    @Override
                    public List<Movie> getAll() {
                        return movieList;
                    }

                    @Override
                    public Movie findById(int id) {
                        return movieList.stream().filter(movie -> movie.getId() == id).findFirst().get();
                    }


                    //Delete movie

                    @Override
                    public boolean remove(Movie object) {

                        try {

                            JSONObject jo=new JSONObject();
                            Map mainMap;
                            JSONArray movieArray=new JSONArray();

                            for (int i=0;i<movieList.size();i++){
                                mainMap=new LinkedHashMap(4);
                                mainMap.put("id",movieList.get(i).getId());
                                mainMap.put("name",movieList.get(i).getName());
                                mainMap.put("stockCount",movieList.get(i).getStockCount());
                                mainMap.put("type",movieList.get(i).getType().getDatabaseId());
                                if (movieList.get(i).getId()!=object.getId()) {
                                    movieArray.add(mainMap);
                                    jo.put("movie",movieArray);
                                }
                            }

                          //  getCustomerTable();
                            final List<Customer> customerList = new ArrayList<>();

                                Object obj = parser.parse(new FileReader(filePath));
                                JSONObject jsonObject = (JSONObject) obj;
                                JSONArray customerArray = (JSONArray) jsonObject.get("customer");


                                for (int i = 0; i < customerArray.size(); i++) {
                                    Customer customer = new Customer();

                                    JSONObject customerData = (JSONObject) customerArray.get(i);
                                    Number id = (Number) customerData.get("id");
                                    customer.setId(id.intValue());
                                    String name = (String) customerData.get("name");
                                    customer.setName(name);
                                    Number points = (Number) customerData.get("points");
                                    customer.setPoints(points.intValue());

                                    customerList.add(customer);
                                }

                            Map mainMapC;
                            JSONArray customerArrayW=new JSONArray();

                            for (int i=0;i<customerList.size();i++){
                                mainMapC=new LinkedHashMap(3);
                                mainMapC.put("id",customerList.get(i).getId());
                                mainMapC.put("name",customerList.get(i).getName());
                                mainMapC.put("points",customerList.get(i).getPoints());
                               // if (customerList.get(i).getId()!=object.getId()) {
                                customerArray.add(mainMapC);
                                jo.put("customer",customerArrayW);
                                }




                            JSONArray customerArrayN=new JSONArray();

                            for (int i=0;i<customerList.size();i++){
                                mainMap=new LinkedHashMap(3);
                                mainMap.put("id",customerList.get(i).getId());
                                mainMap.put("name",customerList.get(i).getName());
                                mainMap.put("points",customerList.get(i).getPoints());
                                    customerArrayN.add(mainMap);
                                    jo.put("customer",customerArrayN);
                                }
                            //siiani
                            // TODO: add orders to WRITER

                            PrintWriter pwr=new PrintWriter("C:\\Users\\reelyka.laheb\\Desktop\\Java\\Result.json");
                            pwr.write(jo.toJSONString());
                            pwr.flush();
                            pwr.close();


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
/*                        }catch (ParseException e){
                            e.printStackTrace();*/
                        }catch (IOException e){
                            e.printStackTrace();
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                        return movieList.remove(object);
                    }

                    @Override
                    public Movie createOrUpdate(Movie object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            movieList.add(object);
                            return object;
                        }

                        Movie movie = findById(object.getId());

                        movie.setName(object.getName());
                        movie.setStockCount(object.getStockCount());
                        movie.setType(object.getType());


                        try {

                            Map mainMap;
                            JSONArray movieArray=new JSONArray();
                            JSONObject joAdd = new JSONObject();

                            for (int i = 0; i < movieList.size(); i++) {
                                mainMap = new LinkedHashMap(4);
                                mainMap.put("id", movieList.get(i).getId());
                                mainMap.put("name", movieList.get(i).getName());
                                mainMap.put("stockCount", movieList.get(i).getStockCount());
                                mainMap.put("type", movieList.get(i).getType().getDatabaseId());
                                    movieArray.add(mainMap);
                                    joAdd.put("movie", movieArray);
                            }

                            //add customer part

                            DatabaseFactory dbF=new DatabaseFactory();
                            final List<Customer> customerList = new ArrayList<>();

                            Map mainMapC;
                        JSONArray customerArray=new JSONArray();

                        for (int i=0;i<customerList.size();i++){
                            mainMapC=new LinkedHashMap(3);
                            mainMapC.put("id",customerList.get(i).getId());
                            mainMapC.put("name",customerList.get(i).getName());
                            mainMapC.put("points",customerList.get(i).getPoints());
                            // if (customerList.get(i).getId()!=object.getId()) {
                            customerArray.add(mainMapC);
                            joAdd.put("customer",customerArray);
                        }

                        //TODO
                            PrintWriter pwr=new PrintWriter("C:\\Users\\reelyka.laheb\\Desktop\\Java\\Result.json");
                            pwr.write(joAdd.toJSONString());
                            pwr.flush();
                            pwr.close();
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                        return movie;

                    }

                    @Override
                    public int generateNextId() {
                        return movieList.size() + 1; //add static variable
                    }
                };
            }

            @Override
            public DBTableRepository<Customer> getCustomerTable() {
                final List<Customer> customerList = new ArrayList<>();

                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(new FileReader(filePath));
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONArray customerArray = (JSONArray) jsonObject.get("customer");


                    for (int i = 0; i < customerArray.size(); i++) {
                        Customer customer = new Customer();

                        JSONObject customerData = (JSONObject) customerArray.get(i);
                        Number id = (Number) customerData.get("id");
                        customer.setId(id.intValue());
                        String name = (String) customerData.get("name");
                        customer.setName(name);
                        Number points = (Number) customerData.get("points");
                        customer.setPoints(points.intValue());

                        customerList.add(customer);
                    }


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (ParseException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }

                return new DBTableRepository<Customer>() {
                    @Override
                    public List<Customer> getAll() {
                        return customerList;
                    }

                    @Override
                    public Customer findById(int id) {
                        return getAll().stream().filter(customer -> customer.getId() == id).findFirst().get();
                    }

                    //remove customer
                    @Override
                    public boolean remove(Customer object) {
                        try {

                            //read in movies from file
                            final List<Movie> movieList = new ArrayList<>();

                            JSONParser parser = new JSONParser();
                                Object obj = parser.parse(new FileReader(filePath));
                                JSONObject jsonObject = (JSONObject) obj;
                                JSONArray moviesArray = (JSONArray) jsonObject.get("movie");

                                for (int i = 0; i < moviesArray.size(); i++) {
                                    Movie movie = new Movie();

                                    JSONObject movieData = (JSONObject) moviesArray.get(i);

                                    Number MovieId = (Number) movieData.get("id");
                                    movie.setId(MovieId.intValue());

                                    String MovieName = (String) movieData.get("name");
                                    movie.setName(MovieName);

                                    Number MovieStockCount = (Number) movieData.get("stockCount");
                                    movie.setStockCount(MovieStockCount.intValue());

                                    movieList.add(movie);
                                }

                                //write movies back to file

                            JSONObject jo=new JSONObject();
                            Map mainMap;
                            JSONArray movieArray=new JSONArray();

                            for (int i=0;i<movieList.size();i++){
                                mainMap=new LinkedHashMap(4);
                                mainMap.put("id",movieList.get(i).getId());
                                mainMap.put("name",movieList.get(i).getName());
                                mainMap.put("stockCount",movieList.get(i).getStockCount());
                                mainMap.put("type",movieList.get(i).getType().getDatabaseId());
                                if (movieList.get(i).getId()!=object.getId()) {
                                    movieArray.add(mainMap);
                                    jo.put("movie",movieArray);
                                }
                            }
                            //write customer back to file
                            JSONArray customerArray=new JSONArray();

                            for (int i=0;i<customerList.size();i++){
                                mainMap=new LinkedHashMap(3);
                                mainMap.put("id",customerList.get(i).getId());
                                mainMap.put("name",customerList.get(i).getName());
                                mainMap.put("points",customerList.get(i).getPoints());
                                if (customerList.get(i).getId()!=object.getId()) {
                                        customerArray.add(mainMap);
                                    jo.put("customer",customerArray);
                                }

                            }

                            // TODO: add orders to WRITER


                            PrintWriter pwr=new PrintWriter("C:\\Users\\reelyka.laheb\\Desktop\\Java\\Result.json");
                            pwr.write(jo.toJSONString());
                            pwr.flush();
                            pwr.close();


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }catch (ParseException e){
                            e.printStackTrace();
                        }


                        return customerList.remove(object);
                    }

                    @Override
                    public Customer createOrUpdate(Customer object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            customerList.add(object);
                            return object;
                        }

                        Customer customer = findById(object.getId());

                        customer.setName(object.getName());
                        customer.setPoints(object.getPoints());

                        return customer;
                    }

                    @Override
                    public int generateNextId() {
                        return customerList.size() + 1;
                    }
                };
            }

            @Override
            public DBTableRepository<RentOrder> getOrderTable() {

                final List<RentOrder> orderList = new ArrayList<>();

                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(new FileReader(filePath));
                    JSONObject jsonObject = (JSONObject) obj;

                    JSONArray orderArray = (JSONArray) jsonObject.get("order");

                    for (int i = 0; i < orderArray.size(); i++) {
                        RentOrder order = new RentOrder();

                        JSONObject orderData = (JSONObject) orderArray.get(i);

                        Number id = (Number) orderData.get("id");
                        order.setId(id.intValue());
                        Number customer=(Number) orderData.get("customer");
                        order.setCustomer(getCustomerTable().findById(customer.intValue()));

                        String orderDate=(String) orderData.get("orderDate");
                        LocalDate localDate=LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(orderDate));
                        order.setOrderDate(localDate);

                        JSONArray itemsArray = (JSONArray) orderData.get("items");
                        List<RentOrder.Item> orderItems = new ArrayList<>();

                        for (int j=0;j<itemsArray.size();j++){

                            JSONObject itemData = (JSONObject) itemsArray.get(j);
                            RentOrder.Item item = new RentOrder.Item();

                            Number movie = (Number) itemData.get("movie");
                            item.setMovie(getMovieTable().findById(movie.intValue()));

                            Number aMovieType = (Number) itemData.get("type");
                            switch (aMovieType.intValue()) {
                                case 1:
                                    MovieType mt1 = MovieType.NEW;
                                    item.setMovieType(mt1);
                                case 2:
                                    MovieType mt2 = MovieType.REGULAR;
                                    item.setMovieType(mt2);
                                case 3:
                                    MovieType mt3 = MovieType.OLD;
                                    item.setMovieType(mt3);
                            }
                            Number days = (Number) itemData.get("days");
                            item.setDays(days.intValue());
                            Boolean paidByBonus= (Boolean) itemData.get("paidByBonus");
                            item.setPaidByBonus(paidByBonus);
                            String returnedDay = (String) itemData.get("returnedDay");
                            if (returnedDay!=null){
                            LocalDate localDateReturned=LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(returnedDay));
                            item.setReturnedDay(localDateReturned);}

                            orderItems.add(item);
                        }
                        order.setItems(orderItems);
                        orderList.add(order);

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (ParseException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }


                return new DBTableRepository<RentOrder>() {
                    @Override
                    public List<RentOrder> getAll() {
                        return orderList;
                    }

                    @Override
                    public RentOrder findById(int id) {
                        return getAll().stream().filter(order -> order.getId() == id).findFirst().get();
                    }



                    //remove order object
                    @Override
                    public boolean remove(RentOrder object) {

                        try {

/*
                            ObjectMapper objMapper = new ObjectMapper();

                            for (int i=0;i<orderList.size();i++) {

                                RentOrder rentOrder = new RentOrder();
                                rentOrder.getId() = orderList.get(i).getId();
                                rentOrder.getCustomer().getId() = orderList.get(i).getCustomer().getId();
                                rentOrder.getOrderDate() = orderList.get(i).getOrderDate();
                                objMapper.writeValue(new FileOutputStream("C:\\Users\\reelyka.laheb\\Desktop\\Java\\OrdersWritingBack.json"), rentOrder);

                            }

                            String json="";

                            for (int k=0;k<orderList.size();k++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("id", orderList.get(k).getId());
                                map.put("customer", orderList.get(k).getCustomer().getId());
                                map.put("orderDate", orderList.get(k).getOrderDate());

                            json=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);

                                JsonNode root=mapper.readTree(json);
                                root.at.("\items").forEach

                            List<Object> list =new ArrayList<>();

                                list.add("movie", orderList.get(i).getItems().get(j).getMovie().getId());
                                list.add("type", orderList.get(i).getItems().get(j).getMovieType().getDatabaseId());
                                list.add("paidByBonus", orderList.get(j).getItems().get(j).isPaidByBonus());
                                list.add("days", orderList.get(j).getItems().get(j).getDays());
                                list.add("returnedDay", orderList.get(j).getItems().get(j).getReturnedDay());

                                map.put("items",list);

                            }

                            mapper.writeValue(new File ("C:\\Users\\reelyka.laheb\\Desktop\\Java\\OrdersWritingBack.json"),mapper);

                            JSONObject joMain=new JSONObject();*/

                            JSONObject orderObject=new JSONObject();
                            JSONObject itemsObject=new JSONObject();
                            JSONObject mainObject=new JSONObject();

                            JSONArray orderArray=new JSONArray();
                            JSONArray itemsArray=new JSONArray();
                            JSONArray mainArray=new JSONArray();


                            for (int i=0;i<orderList.size();i++) {
                                if (orderList.get(i).getId() != object.getId()) {
                                    orderObject=new JSONObject();
                                    itemsArray=new JSONArray();

                                        orderObject.put("id", orderList.get(i).getId());
                                        orderObject.put("customer", orderList.get(i).getCustomer().getId());
                                        orderObject.put("orderDate", orderList.get(i).getOrderDate());
                                        orderArray.add(orderObject);

                                           for (int j=0;j<orderList.get(i).getItems().size();j++) {
                                        itemsObject = new JSONObject();
                                        itemsObject.put("movie", orderList.get(i).getItems().get(j).getMovie().getId());
                                        itemsObject.put("type", orderList.get(i).getItems().get(j).getMovieType().getDatabaseId());
                                        itemsObject.put("paidByBonus", orderList.get(i).getItems().get(j).isPaidByBonus());
                                        itemsObject.put("days", orderList.get(i).getItems().get(j).getDays());
                                        itemsObject.put("returnedDay", orderList.get(i).getItems().get(j).getReturnedDay());
                                        itemsArray.add(itemsObject);

                                          }
                                }
                                orderObject.put("items", itemsArray); //tostsin sulu sisse
                                }
                            mainObject.put("order",orderArray);

                                // TODO: add movies + customer to WRITER


                                PrintWriter pwr = new PrintWriter("C:\\Users\\reelyka.laheb\\Desktop\\Java\\DBResult.json");
                                pwr.write(mainObject.toJSONString());

                                pwr.flush();
                                pwr.close();


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }

                        return orderList.remove(object);
                    }

                    @Override
                    public RentOrder createOrUpdate(RentOrder object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            orderList.add(object);
                            return object;
                        }

                        RentOrder order = findById(object.getId());

                        order.setCustomer(object.getCustomer());
                        order.setOrderDate(order.getOrderDate());
                        order.setItems(object.getItems());

                        return order;
                    }

                    @Override
                    public int generateNextId() {
                        return orderList.size() + 1;
                    }
                };
            }
        };
    }
}
