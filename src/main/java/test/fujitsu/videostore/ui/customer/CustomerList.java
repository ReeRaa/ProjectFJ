package test.fujitsu.videostore.ui.customer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.ui.MainLayout;
import test.fujitsu.videostore.ui.customer.components.CustomerForm;
import test.fujitsu.videostore.ui.customer.components.CustomerGrid;

import java.util.ArrayList;
import java.util.List;

@Route(value = CustomerList.VIEW_NAME, layout = MainLayout.class)
public class CustomerList extends HorizontalLayout implements HasUrlParameter<String> {

    public static final String VIEW_NAME = "CustomerList";
    private CustomerGrid grid;
    private CustomerForm form;
    private TextField filter;

    private ListDataProvider<Customer> dataProvider = new ListDataProvider<>(new ArrayList<>());
    private CustomerListLogic viewLogic = new CustomerListLogic(this);
    private Button newCustomer;

    public CustomerList() {
        setId(VIEW_NAME);
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new CustomerGrid();
        grid.setDataProvider(dataProvider);
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));

        form = new CustomerForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);

        viewLogic.init();
    }

    public HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setId("filter");
        filter.setPlaceholder("Filter by customer name");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> {
            // TODO: Implement filtering by customer name
        });

        newCustomer = new Button("New Customer");
        newCustomer.setId("new-item");
        newCustomer.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newCustomer.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newCustomer.addClickListener(click -> viewLogic.newCustomer());

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(newCustomer);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg);
    }

    public void setNewCustomerEnabled(boolean enabled) {
        newCustomer.setEnabled(enabled);
    }

    public void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    public void selectRow(Customer row) {
        grid.getSelectionModel().select(row);
    }

    public void addCustomer(Customer customer) {
        dataProvider.getItems().add(customer);
        grid.getDataProvider().refreshAll();
    }

    public void updateCustomer(Customer customer) {
        dataProvider.refreshItem(customer);
    }

    public void removeCustomer(Customer customer) {
        dataProvider.getItems().remove(customer);
        dataProvider.refreshAll();
    }

    public void editCustomer(Customer customer) {
        showForm(customer != null);
        form.editCustomer(customer);
    }

    public void showForm(boolean show) {
        form.setVisible(show);
    }

    public void setCustomers(List<Customer> customers) {
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(customers);
        dataProvider.refreshAll();
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {
        viewLogic.enter(parameter);
    }
}
