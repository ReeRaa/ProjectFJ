package test.fujitsu.videostore.backend.database;

import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
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

               final List<Movie> movieList =getMoviesList();
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

                            JSONObject jo = new JSONObject();
                            Map mainMap;
                            JSONArray movieArray = new JSONArray();

                            for (int i = 0; i < movieList.size(); i++) {
                                mainMap = new LinkedHashMap(4);
                                mainMap.put("id", movieList.get(i).getId());
                                mainMap.put("name", movieList.get(i).getName());
                                mainMap.put("stockCount", movieList.get(i).getStockCount());
                                mainMap.put("type", movieList.get(i).getType().getDatabaseId());
                                if (movieList.get(i).getId() != object.getId()) {
                                    movieArray.add(mainMap);
                                    jo.put("movie", movieArray);
                                }
                            }

                            jo.put("customer", createCustomersArrayForWritingBack());
                            jo.put("order", createOrdersArrayForWritingBack());

                            PrintWriter pwr=new PrintWriter(filePath);
                            pwr.write(jo.toJSONString());
                            pwr.flush();
                            pwr.close();


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }

                        return movieList.remove(object);
                    }

                    //method to write movies into Array
                    public JSONArray createMovieArrayforWritingBackInsideMovie(Movie object) {
                        Movie movie = findById(object.getId());

                            Map mainMap;
                            JSONObject jo = new JSONObject();
                            JSONObject joW = new JSONObject();
                            JSONArray movieArray = new JSONArray();
                            mainMap = new LinkedHashMap(4);

                            mainMap.put("id", object.getId());
                            mainMap.put("name", object.getName());
                            mainMap.put("stockCount", object.getStockCount());
                            mainMap.put("type", object.getType().getDatabaseId());
                            movieArray.add(mainMap);

                            for (int i = 0; i < movieList.size(); i++) {
                                if (object.getId()!= movieList.get(i).getId()) {
                                    mainMap = new LinkedHashMap(4);
                                    mainMap.put("id", movieList.get(i).getId());
                                    mainMap.put("name", movieList.get(i).getName());
                                    mainMap.put("stockCount", movieList.get(i).getStockCount());
                                    mainMap.put("type", movieList.get(i).getType().getDatabaseId());
                                    movieArray.add(mainMap);
                                }
                            }
                       return  movieArray;
                    }

                    //method to write movie + customer + order back to file
                    public Movie writeMovieBackToFile(Movie object){
                        Movie movie = findById(object.getId());

                        try {
                            Map mainMap;
                            JSONObject jo=new JSONObject();
                            JSONArray movieArray=new JSONArray();
                            mainMap = new LinkedHashMap(4);

                            jo.put("movie", createMovieArrayforWritingBackInsideMovie(object));
                            jo.put("customer",createCustomersArrayForWritingBack());
                            jo.put("order", createOrdersArrayForWritingBack());

                            //printer part
                           // PrintWriter pwr=new PrintWriter("C:\\Users\\reelyka.laheb\\Desktop\\Java\\createOrUpdateMovie.json");
                            PrintWriter pwr=new PrintWriter(filePath);
                            pwr.write(jo.toJSONString());
                            pwr.flush();
                            pwr.close();
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                        return movie;
                    }


                    @Override
                    public Movie createOrUpdate(Movie object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            movieList.add(object);

                        Movie movie = findById(object.getId());
                        movie.setName(object.getName());
                        movie.setStockCount(object.getStockCount());
                        movie.setType(object.getType());
                        writeMovieBackToFile(object);
                            return movie;
                        }
                        writeMovieBackToFile(object);
                        return object;
                    }

                    @Override
                    public int generateNextId() {
                        return movieList.size() + 1; //add static variable
                    }
                };
            }

            //method to write Customer into Array
            public JSONArray createCustomersArrayForWritingBack(){
                JSONArray customerArray = new JSONArray();
                Map mainMap;

                for (int i = 0; i < getCustomerList().size(); i++) {
                    mainMap = new LinkedHashMap(3);
                    mainMap.put("id", getCustomerList().get(i).getId());
                    mainMap.put("name", getCustomerList().get(i).getName());
                    mainMap.put("points", getCustomerList().get(i).getPoints());

                    customerArray.add(mainMap);
                }
                return customerArray;
            }

            //method to write movies into Array
            public JSONArray createMovieArrayforWritingBack() {
                Map mainMap;
                JSONObject jo = new JSONObject();
                JSONObject joW = new JSONObject();
                JSONArray movieArray = new JSONArray();
                mainMap = new LinkedHashMap(4);

                for (int i = 0; i < getMoviesList().size(); i++) {
                    mainMap = new LinkedHashMap(4);
                    mainMap.put("id", getMoviesList().get(i).getId());
                    mainMap.put("name", getMoviesList().get(i).getName());
                    mainMap.put("stockCount", getMoviesList().get(i).getStockCount());
                    mainMap.put("type", getMoviesList().get(i).getType().getDatabaseId());
                    movieArray.add(mainMap);
                }
                return  movieArray;
            }

            //method to write rentOrders into Array
            public JSONArray createOrdersArrayForWritingBack(){
                JSONObject orderObject = new JSONObject();
                JSONObject itemsObject = new JSONObject();

                JSONArray ordersArray = new JSONArray();
                JSONArray itemsArray = new JSONArray();

                List orderList=new LinkedList(getRentOrderList());

                int year;
                int month;
                int day;

                LocalDate returnedDate;
                LocalDate localDateOriginalValue;
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                String formattedDate;

                for (int t = 0; t < getRentOrderList().size(); t++) {
                    orderObject = new JSONObject();
                    itemsArray = new JSONArray();

                    orderObject.put("id", getRentOrderList().get(t).getId());
                    orderObject.put("customer",getRentOrderList().get(t).getCustomer().getId());

                    year=getRentOrderList().get(t).getOrderDate().getYear();
                    month=getRentOrderList().get(t).getOrderDate().getMonthValue();
                    day=getRentOrderList().get(t).getOrderDate().getDayOfMonth();

                    returnedDate=LocalDate.of(year,month,day);
                    formattedDate=formatter.format(returnedDate);

                    orderObject.put("orderDate", formattedDate);
                    ordersArray.add(orderObject);

                    for (int j = 0; j < getRentOrderList().get(t).getItems().size(); j++) {
                        itemsObject = new JSONObject();
                        itemsObject.put("movie", getRentOrderList().get(t).getItems().get(j).getMovie().getId());
                        itemsObject.put("type", getRentOrderList().get(t).getItems().get(j).getMovieType().getDatabaseId());
                        itemsObject.put("paidByBonus", getRentOrderList().get(t).getItems().get(j).isPaidByBonus());
                        itemsObject.put("days", getRentOrderList().get(t).getItems().get(j).getDays());

                        localDateOriginalValue= getRentOrderList().get(t).getItems().get(j).getReturnedDay();
                        if(localDateOriginalValue !=null){
                            year=getRentOrderList().get(t).getItems().get(j).getReturnedDay().getYear();
                            month=getRentOrderList().get(t).getItems().get(j).getReturnedDay().getMonthValue();
                            day=getRentOrderList().get(t).getItems().get(j).getReturnedDay().getDayOfMonth();

                            returnedDate=LocalDate.of(year,month,day);
                            formattedDate=formatter.format(returnedDate);
                            itemsObject.put("returnedDay", formattedDate);
                        }else {
                            itemsObject.put("returnedDay", getRentOrderList().get(t).getItems().get(j).getReturnedDay());

                        }

                        itemsArray.add(itemsObject);
                    }
                    orderObject.put("items", itemsArray);
                }
                return ordersArray;
            }


            //added new method to get movies list
            public List<Movie> getMoviesList(){
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
                return movieList;
            }

            //ADDED NEW METHOD to get customer list
            public List<Customer> getCustomerList() {

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

                return customerList;
            }

            //ADDED NEW METHOD FOR REUSING OF GETTING Order LIST
            public List<RentOrder> getRentOrderList(){

                final List<RentOrder> rentOrderList = new ArrayList<>();
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
                        Number customer = (Number) orderData.get("customer");
                        order.setCustomer(getCustomerTable().findById(customer.intValue()));

                        String orderDate = (String) orderData.get("orderDate");
                        LocalDate localDate = LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(orderDate));
                        order.setOrderDate(localDate);

                        JSONArray itemsArray = (JSONArray) orderData.get("items");
                        List<RentOrder.Item> orderItems = new ArrayList<>();

                        for (int j = 0; j < itemsArray.size(); j++) {

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
                            Boolean paidByBonus = (Boolean) itemData.get("paidByBonus");
                            item.setPaidByBonus(paidByBonus);
                            String returnedDay = (String) itemData.get("returnedDay");
                            if (returnedDay != null) {
                                LocalDate localDateReturned = LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(returnedDay));
                                item.setReturnedDay(localDateReturned);
                            }

                            orderItems.add(item);
                        }
                        order.setItems(orderItems);
                        rentOrderList.add(order);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (ParseException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return rentOrderList;
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
                            JSONObject jo = new JSONObject();
                            Map mainMap;

                            jo.put("movie",createMovieArrayforWritingBack());
                            jo.put("order",createOrdersArrayForWritingBack());

                            //write customer back to file
                            JSONArray customerArray = new JSONArray();

                            for (int i = 0; i < customerList.size(); i++) {
                                mainMap = new LinkedHashMap(3);
                                mainMap.put("id", customerList.get(i).getId());
                                mainMap.put("name", customerList.get(i).getName());
                                mainMap.put("points", customerList.get(i).getPoints());
                                if (customerList.get(i).getId() != object.getId()) {
                                    customerArray.add(mainMap);
                                    jo.put("customer", customerArray);
                                }

                            }

                            //PrintWriter pwr = new PrintWriter(filePath);
                           // PrintWriter pwr = new PrintWriter("C:\\Users\\reelyka.laheb\\Desktop\\Java\\RemoveCustomer.json");
                            PrintWriter pwr = new PrintWriter(filePath);
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


                    //method to write movies into Array
                    public JSONArray createCustomerArrayforWritingBackInsideCustomer(Customer object) {
                        Customer customer=findById((object.getId()));

                        Map mainMap;
                        JSONObject jo = new JSONObject();
                        JSONObject joW = new JSONObject();
                        JSONArray customerArray = new JSONArray();
                        mainMap = new LinkedHashMap(4);

                        mainMap.put("id", object.getId());
                        mainMap.put("name", object.getName());
                        mainMap.put("points", object.getPoints());
                        customerArray.add(mainMap);

                        for (int i = 0; i < customerList.size(); i++) {
                            if (object.getId()!= customerList.get(i).getId()) {
                                mainMap = new LinkedHashMap(4);
                                mainMap.put("id", customerList.get(i).getId());
                                mainMap.put("name", customerList.get(i).getName());
                                mainMap.put("points", customerList.get(i).getPoints());
                                customerArray.add(mainMap);
                            }
                        }

                        return  customerArray;
                    }

                    //method to write for Customer:  movie + customer + order back to file
                    public Customer writeCustomerBackToFile(Customer object){
                        Customer customer=findById((object.getId()));

                        try {
                            Map mainMap;
                            JSONObject jo=new JSONObject();
                            JSONArray customerArray=new JSONArray();
                            mainMap = new LinkedHashMap(4);

                            jo.put("movie", createMovieArrayforWritingBack());
                            jo.put("customer",createCustomerArrayforWritingBackInsideCustomer(object));
                            jo.put("order", createOrdersArrayForWritingBack());

                            //printer part
                            // PrintWriter pwr=new PrintWriter("C:\\Users\\reelyka.laheb\\Desktop\\Java\\createOrUpdateMovie.json");
                          PrintWriter pwr=new PrintWriter(filePath);
                            pwr.write(jo.toJSONString());
                            pwr.flush();
                            pwr.close();
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                        return customer;
                    }


                    @Override
                    public Customer createOrUpdate(Customer object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            customerList.add(object);
                            writeCustomerBackToFile(object);

                            return object;
                        }

                        Customer customer = findById(object.getId());

                        customer.setName(object.getName());
                        customer.setPoints(object.getPoints());
                        writeCustomerBackToFile(object);
                        return customer;
                    }

                    @Override
                    public int generateNextId() {
                        return customerList.size() + 1;
                    }
                };
            }
            //TODO: for next Order

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

                            JSONObject jo=new JSONObject();
                            jo.put("movie",createMovieArrayforWritingBack());
                            jo.put("customer",createCustomersArrayForWritingBack());

                            //write Orders to file
                            JSONObject orderObject=new JSONObject();
                            JSONObject itemsObject;
                            JSONObject mainObject;

                            JSONArray orderArray=new JSONArray();
                            JSONArray itemsArray=new JSONArray();
                            int year;
                            int month;
                            int day;

                            LocalDate returnedDate;
                            LocalDate localDateOriginalValue;
                            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                            String formattedDate;

                            for (int i=0;i<orderList.size();i++) {
                                if (orderList.get(i).getId() != object.getId()) {
                                orderObject = new JSONObject();
                                itemsArray = new JSONArray();

                                orderObject.put("id", orderList.get(i).getId());
                                orderObject.put("customer", orderList.get(i).getCustomer().getId());

                                year = orderList.get(i).getOrderDate().getYear();
                                month = orderList.get(i).getOrderDate().getMonthValue();
                                day = orderList.get(i).getOrderDate().getDayOfMonth();

                                returnedDate = LocalDate.of(year, month, day);
                                formattedDate = formatter.format(returnedDate);
                                orderObject.put("orderDate", formattedDate);
                                orderArray.add(orderObject);

                                for (int j = 0; j < orderList.get(i).getItems().size(); j++) {
                                    itemsObject = new JSONObject();
                                    itemsObject.put("movie", orderList.get(i).getItems().get(j).getMovie().getId());
                                    itemsObject.put("type", orderList.get(i).getItems().get(j).getMovieType().getDatabaseId());
                                    itemsObject.put("paidByBonus", orderList.get(i).getItems().get(j).isPaidByBonus());
                                    itemsObject.put("days", orderList.get(i).getItems().get(j).getDays());

                                    localDateOriginalValue = orderList.get(i).getItems().get(j).getReturnedDay();
                                    if (localDateOriginalValue != null) {
                                        year = orderList.get(i).getItems().get(j).getReturnedDay().getYear();
                                        month = orderList.get(i).getItems().get(j).getReturnedDay().getMonthValue();
                                        day = orderList.get(i).getItems().get(j).getReturnedDay().getDayOfMonth();
                                        returnedDate = LocalDate.of(year, month, day);
                                        formattedDate = formatter.format(returnedDate);

                                        itemsObject.put("returnedDay", formattedDate);

                                    } else {
                                        itemsObject.put("returnedDay", orderList.get(i).getItems().get(j).getReturnedDay());
                                    }
                                    itemsArray.add(itemsObject);
                                }
                                orderObject.put("items", itemsArray);

                                jo.put("order", orderArray); }
                            }


                            //  PrintWriter pwr = new PrintWriter("C:\\Users\\reelyka.laheb\\Desktop\\Java\\RemoveOrders.json");
                           PrintWriter pwr = new PrintWriter(filePath);
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

//TODO next: update /add order
                    //method to write movies into Array
                    public JSONArray createRentOrderArrayForWritingBackInsideOrder(RentOrder object) {
                     //   RentOrder order = findById((object.getId()));

                      //  Map mainMap;
                        // mainMap = new LinkedHashMap(4);

                        // JSONObject jo = new JSONObject();
                       // JSONObject joW = new JSONObject();
                       // JSONArray ordersArray = new JSONArray();
                        JSONArray objectArray = new JSONArray();
                        JSONArray mainArray = new JSONArray();
                        JSONArray itemsArray = new JSONArray();

                        JSONObject orderObject = new JSONObject();
                        JSONObject itemsObject;
                        JSONObject mainObject;

                        int year;
                        int month;
                        int day;

                        LocalDate returnedDate;
                        LocalDate orderDate;
                        LocalDate localDateOriginalValue;
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                        String formattedDate;

                        orderObject.put("id", object.getId());
                        orderObject.put("customer", object.getCustomer().getId());
                            year = object.getOrderDate().getYear();
                            month = object.getOrderDate().getMonthValue();
                            day = object.getOrderDate().getDayOfMonth();

                            orderDate = LocalDate.of(year, month, day);
                            formattedDate = formatter.format(orderDate);
                        orderObject.put("orderDate", formattedDate);
                        //ordersArray.add(orderObject);

                        //objectArray.add(mainMap);

                        for (int j = 0; j < object.getItems().size(); j++) {
                            itemsObject = new JSONObject();
                            itemsObject.put("movie", object.getItems().get(j).getMovie().getId());
                            itemsObject.put("type", object.getItems().get(j).getMovieType().getDatabaseId());
                            itemsObject.put("paidByBonus", object.getItems().get(j).isPaidByBonus());
                            itemsObject.put("days", object.getItems().get(j).getDays());

                            localDateOriginalValue = object.getItems().get(j).getReturnedDay();
                            if (localDateOriginalValue != null) {
                                year = object.getItems().get(j).getReturnedDay().getYear();
                                month = object.getItems().get(j).getReturnedDay().getMonthValue();
                                day = object.getItems().get(j).getReturnedDay().getDayOfMonth();
                                returnedDate = LocalDate.of(year, month, day);
                                formattedDate = formatter.format(returnedDate);

                                itemsObject.put("returnedDay", formattedDate);

                            } else {
                                itemsObject.put("returnedDay", object.getItems().get(j).getReturnedDay());
                            }
                            itemsArray.add(itemsObject);
                        }
                        orderObject.put("items", itemsArray);

                        mainArray.add(orderObject);

                        //end of object data


                        for (int i = 0; i < orderList.size(); i++) {
                            if (orderList.get(i).getId() != object.getId()) {
                                orderObject = new JSONObject();
                                itemsArray = new JSONArray();

                                orderObject.put("id", orderList.get(i).getId());
                                orderObject.put("customer", orderList.get(i).getCustomer().getId());

                                    year = orderList.get(i).getOrderDate().getYear();
                                    month = orderList.get(i).getOrderDate().getMonthValue();
                                    day = orderList.get(i).getOrderDate().getDayOfMonth();

                                    orderDate = LocalDate.of(year, month, day);
                                    formattedDate = formatter.format(orderDate);
                                orderObject.put("orderDate", formattedDate);
                                //mainArray.add(orderObject);

                                for (int j = 0; j < orderList.get(i).getItems().size(); j++) {
                                    itemsObject = new JSONObject();

                                    itemsObject.put("movie", orderList.get(i).getItems().get(j).getMovie().getId());
                                    itemsObject.put("type", orderList.get(i).getItems().get(j).getMovieType().getDatabaseId());
                                    itemsObject.put("paidByBonus", orderList.get(i).getItems().get(j).isPaidByBonus());
                                    itemsObject.put("days", orderList.get(i).getItems().get(j).getDays());

                                    localDateOriginalValue = orderList.get(i).getItems().get(j).getReturnedDay();
                                    if (localDateOriginalValue != null) {
                                        year = orderList.get(i).getItems().get(j).getReturnedDay().getYear();
                                        month = orderList.get(i).getItems().get(j).getReturnedDay().getMonthValue();
                                        day = orderList.get(i).getItems().get(j).getReturnedDay().getDayOfMonth();
                                        returnedDate = LocalDate.of(year, month, day);
                                        formattedDate = formatter.format(returnedDate);

                                        itemsObject.put("returnedDay", formattedDate);

                                    } else {
                                        itemsObject.put("returnedDay", orderList.get(i).getItems().get(j).getReturnedDay());
                                    }

                                    itemsArray.add(itemsObject);
                                }
                                orderObject.put("items", itemsArray);
                                if (object.getId() != orderList.get(i).getId()) {
                                    mainArray.add(orderObject);
                                }
                                // jo.put("order", ordersArray); }
                            }}
                           // ordersArray.add(objectArray);
                           // ordersArray.add(mainArray);
//siin loppeb muudetav kood
                            return mainArray;
                        }



                    //method to write for Customer:  movie + customer + order back to file
                    public RentOrder writeRentOrderBackToFile(RentOrder object){
                        RentOrder order=findById((object.getId()));

                        try {
                            Map mainMap;
                            JSONObject jo=new JSONObject();
                            JSONArray customerArray=new JSONArray();
                            mainMap = new LinkedHashMap(4);

                            jo.put("movie", createMovieArrayforWritingBack());
                            jo.put("customer",createCustomersArrayForWritingBack());
                            jo.put("order", createRentOrderArrayForWritingBackInsideOrder(object));

                            //printer part
                           // PrintWriter pwr=new PrintWriter("C:\\Users\\reelyka.laheb\\Desktop\\Java\\createOrUpdateRentOrder.json");
                            PrintWriter pwr=new PrintWriter(filePath);
                            pwr.write(jo.toJSONString());
                            pwr.flush();
                            pwr.close();
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                        return order;
                    }

                    @Override
                    public RentOrder createOrUpdate(RentOrder object) {
                        if (object == null) {
                            return null;
                        }

                        if (object.isNewObject()) {
                            object.setId(generateNextId());
                            orderList.add(object);
                            writeRentOrderBackToFile(object);

                            return object;
                        }

                        RentOrder order = findById(object.getId());

                        order.setCustomer(object.getCustomer());
                        order.setOrderDate(order.getOrderDate());
                        order.setItems(object.getItems());
                        writeRentOrderBackToFile(object);
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
