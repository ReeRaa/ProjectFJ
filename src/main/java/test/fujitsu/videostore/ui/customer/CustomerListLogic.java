package test.fujitsu.videostore.ui.customer;

import com.vaadin.flow.component.UI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import test.fujitsu.videostore.backend.database.DBTableRepository;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.ui.database.CurrentDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerListLogic {

    private CustomerList view;

    private DBTableRepository<Customer> customerDBTableRepository;

    public CustomerListLogic(CustomerList customerList) {
        view = customerList;
    }

    public void init() {
        if (CurrentDatabase.get() == null) {
            return;
        }

        customerDBTableRepository = CurrentDatabase.get().getCustomerTable();

        view.setNewCustomerEnabled(true);
        view.setCustomers(customerDBTableRepository.getAll());
    }

    public void cancelCustomer() {
        setFragmentParameter("");
        view.clearSelection();
    }

    private void setFragmentParameter(String movieId) {
        String fragmentParameter;
        if (movieId == null || movieId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = movieId;
        }

        UI.getCurrent().navigate(CustomerList.class, fragmentParameter);
    }

    public void enter(String customerId) {
        if (customerId != null && !customerId.isEmpty()) {
            if (customerId.equals("new")) {
                newCustomer();
            } else {
                int pid = Integer.parseInt(customerId);
                Customer customer = findCustomer(pid);
                view.selectRow(customer);
            }
        } else {
            view.showForm(false);
        }
    }

    private Customer findCustomer(int customerId) {
        return customerDBTableRepository.findById(customerId);
    }

    public void saveCustomer(Customer customer) {
        boolean isNew = customer.isNewObject();

        Customer updatedObject = customerDBTableRepository.createOrUpdate(customer);

        if (isNew) {
            view.addCustomer(updatedObject);
        } else {
            view.updateCustomer(customer);
        }

        view.clearSelection();
        setFragmentParameter("");
        view.showSaveNotification(customer.getName() + (isNew ? " created" : " updated"));
    }

    public void deleteCustomer(Customer customer) {
        customerDBTableRepository.remove(customer);

        view.clearSelection();
        view.removeCustomer(customer);
        setFragmentParameter("");
        view.showSaveNotification(customer.getName() + " removed");
    }

    public void editCustomer(Customer customer) {
        if (customer == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(customer.getId() + "");
        }
        view.editCustomer(customer);
    }

    public void newCustomer() {
        setFragmentParameter("new");
        view.clearSelection();
        view.editCustomer(new Customer());
    }

    public void rowSelected(Customer customer) {
        editCustomer(customer);
    }

    //I ADDED NEW METHOD WHICH CAN BE MAYBE REUSED???
    public List<Customer> getCustomerList() {

        final List<Customer> customerList = new ArrayList<>();

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("C:\\Users\\reelyka.laheb\\Desktop\\Java\\getCustomerList.json"));
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

}
