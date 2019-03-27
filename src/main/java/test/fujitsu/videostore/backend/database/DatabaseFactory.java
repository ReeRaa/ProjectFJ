package test.fujitsu.videostore.backend.database;

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
                            JSONParser parser = new JSONParser();

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
                            JSONObject jo=new JSONObject();
                            Map mainMapC;
                            JSONArray customerArray=new JSONArray();

                            for (int i=0;i<customerList.size();i++){
                                mainMapC=new LinkedHashMap(3);
                                mainMapC.put("id",customerList.get(i).getId());
                                mainMapC.put("name",customerList.get(i).getName());
                                mainMapC.put("points",customerList.get(i).getPoints());
                                if (customerList.get(i).getId()!=object.getId()) {
                                        customerArray.add(mainMapC);
                                    jo.put("customer",customerArray);
                                }

                            }

                            // TODO: add movies + orders to WRITER


                            PrintWriter pwr=new PrintWriter("C:\\Users\\reelyka.laheb\\Desktop\\Java\\Result.json");
                            pwr.write(jo.toJSONString());
                            pwr.flush();
                            pwr.close();


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }catch (IOException e){
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
      //                      Number type = (Number) itemData.get("type");
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

                    @Override
                    public boolean remove(RentOrder object) {

                        try {
                            JSONObject jo=new JSONObject();
                            Map mainMapO;
                            JSONArray orderArray=new JSONArray();

                            for (int i=0;i<orderList.size();i++){
                                mainMapO=new LinkedHashMap(3);
                                mainMapO.put("id",orderList.get(i).getId());
                                mainMapO.put("customer",orderList.get(i).getCustomer());
                                mainMapO.put("orderDate",orderList.get(i).getOrderDate());

                                if (orderList.get(i).getId()!=object.getId()) {
                                    orderArray.add(mainMapO);
                                    jo.put("order",orderArray);

                                    JSONArray itemsArray=new JSONArray();

                                for (int j=0;j<orderList.get(i).getItems().size();j++){
                                    mainMapO=new LinkedHashMap(5);
                                    mainMapO.put("movie",orderList.get(j).getItems().get(j).getMovie().getId());
                                    mainMapO.put("type",orderList.get(j).getItems().get(j).getMovieType().getDatabaseId());
                                    mainMapO.put("paidByBonus",orderList.get(j).getItems().get(j).isPaidByBonus());
                                    mainMapO.put("days",orderList.get(j).getItems().get(j).getDays());
                                    mainMapO.put("returnedDay",orderList.get(j).getItems().get(j).getReturnedDay());
                                        itemsArray.add(mainMapO);
                                        jo.put("items",itemsArray);
                                }

                                }

                            }

                            // TODO: add movies + customer to WRITER


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


                            PrintWriter pwr=new PrintWriter("C:\\Users\\reelyka.laheb\\Desktop\\Java\\Result.json");
                            pwr.write(jo.toJSONString());
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
