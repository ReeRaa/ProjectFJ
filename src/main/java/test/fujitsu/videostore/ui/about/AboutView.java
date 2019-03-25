package test.fujitsu.videostore.ui.about;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import test.fujitsu.videostore.ui.MainLayout;

@Route(value = AboutView.VIEW_NAME, layout = MainLayout.class)
@PageTitle("About")
public class AboutView extends HorizontalLayout {

    public static final String VIEW_NAME = "About";

    public AboutView() {
        setId("about-container");

        add(VaadinIcon.INFO_CIRCLE.create());

        // TODO: Replace "Your name here" text to your name
        Label fujitsuLabel = new Label("Reelyka Läheb");
        fujitsuLabel.setId("about-container-name");

        add(new Label("This task was done by: "), fujitsuLabel);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
