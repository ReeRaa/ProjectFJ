package test.fujitsu.videostore.ui.database;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.component.notification.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Route("DatabaseSelection")
@PageTitle("Database Selection")
@HtmlImport("css/shared-styles.html")
public class DatabaseSelectionView extends FlexLayout {

    private TextField databasePath;
    private Button selectDatabaseButton;

    public DatabaseSelectionView() {
        setSizeFull();
        setClassName("database-selection-screen");

        FlexLayout centeringLayout = new FlexLayout();
        centeringLayout.setSizeFull();
        centeringLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        centeringLayout.setAlignItems(Alignment.CENTER);
        centeringLayout.add(buildLoginForm());

        add(centeringLayout);
    }

    private Component buildLoginForm() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("310px");

        databasePath = new TextField("Enter database file path");
        databasePath.setId("database-path");
        databasePath.setRequired(true);

        verticalLayout.add(databasePath);

        HorizontalLayout buttons = new HorizontalLayout();
        verticalLayout.add(buttons);

        selectDatabaseButton = new Button("Select database");
        selectDatabaseButton.setId("database-select");
        selectDatabaseButton.addClickListener(event -> selectDatabase());
        selectDatabaseButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        buttons.add(selectDatabaseButton);

        return verticalLayout;
    }

        public boolean isJSONValid(String jsonInString ) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                mapper.readTree(readAllText(databasePath.getValue()));
                return true;
            } catch (IOException e) {
                return false;
            }
        }
   // }

    public String readAllText(String str){
        String content = "";
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(str) ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return content;
    }

    private void selectDatabase() {
        selectDatabaseButton.setEnabled(false);
        try {
            // Validations against selected database.
            //validates if file is valid json file and if there is a json file on given path
            String path= databasePath.getValue();
            String fileType= path.substring(path.length() - 5);

            if (!fileType.equals(".json")) {
                Notification firstError = Notification.show("Please give valid database file path!");
            }else {
                if (!isJSONValid(databasePath.getValue())){
                    Notification secondError= Notification.show("Please give valid json file!");

                } else {
                    CurrentDatabase.set(databasePath.getValue());
                }
            }

            getUI().get().navigate("");
        } finally {
            selectDatabaseButton.setEnabled(true);
        }
    }
}
