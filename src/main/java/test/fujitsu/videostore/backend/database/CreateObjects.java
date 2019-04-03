package test.fujitsu.videostore.backend.database;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CreateObjects {
/*    public List<Customer> getCustomerList() {

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

    //method to write Customer into Array
    public JSONArray createCustomersArrayForWritingBack(){
        CreateObjects createObjects=new CreateObjects();
        List customerList=new ArrayList(getCustomerList);

        JSONArray customerArray = new JSONArray();
        Map mainMap;

        for (int i = 0; i < customerList.size(); i++) {
            mainMap = new LinkedHashMap(3);
            mainMap.put("id", customerList.get(i).getId());
            mainMap.put("name", getCustomerList().get(i).getName());
            mainMap.put("points", getCustomerList().get(i).getPoints());

            customerArray.add(mainMap);
        }
        return customerArray;
    }
DatabaseFactory dbf=new DatabaseFactory();

    //ADDED NEW METHOD FOR REUSING OF GETTING CUSTOMERS LIST
    public List<RentOrder> getRentOrderList(){

        final List<RentOrder> rentOrderList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("C:\\Users\\reelyka.laheb\\Desktop\\Java\\DatabaseOriginal.json"));
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray orderArray = (JSONArray) jsonObject.get("order");

            for (int i = 0; i < orderArray.size(); i++) {
                RentOrder order = new RentOrder();

                JSONObject orderData = (JSONObject) orderArray.get(i);

                Number id = (Number) orderData.get("id");
                order.setId(id.intValue());
                Number customer = (Number) orderData.get("customer");
                order.setCustomer(Customer.getCustomerTable().findById(customer.intValue()));

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
    }*/


}
