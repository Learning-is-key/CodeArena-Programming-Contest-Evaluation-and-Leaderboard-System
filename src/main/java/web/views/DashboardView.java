package web.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("dashboard")
@PageTitle("CodeArena | Dashboard")
public class DashboardView extends VerticalLayout {

    public DashboardView() {

        H1 title = new H1("CodeArena");

        H2 subtitle = new H2("Think. Code. Compete.");

        Paragraph description = new Paragraph(
            "Welcome to the Programming Contest Evaluation and Leaderboard System."
        );

        Button contestsButton = new Button("Explore Contests");

        add(title, subtitle, description, contestsButton);

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }
}