package awesomeness.vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import javax.servlet.annotation.WebServlet;

/**
 *
 */
@Theme("mytheme")
@Widgetset("awesomeness.vaadin.MyAppWidgetset")
public class MyUI extends UI
{

    @Override
    protected void init(VaadinRequest vaadinRequest)
    {
        Panel menuPanel = new Panel("Menu");
        Panel contentPanel = new Panel("Content");

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(menuPanel, contentPanel);
        setContent(splitPanel);

        final FormLayout menuLayout = new FormLayout();
        menuPanel.setContent(menuLayout);

        Button worldattributes = new Button("World attributes");
        worldattributes.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(ClickEvent event)
            {
                menuLayout.addComponent(new Label("Thank you for clicking"));
            }
        });
        menuLayout.addComponent(worldattributes);

        Button scripts = new Button("Scripts");
        scripts.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(ClickEvent event)
            {
                menuLayout.addComponent(new Label("Thank you for clicking"));
            }
        });
        menuLayout.addComponent(scripts);

        Button areas = new Button("Areas");
        areas.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(ClickEvent event)
            {
                menuLayout.addComponent(new Label("Thank you for clicking"));
            }
        });
        menuLayout.addComponent(areas);

        Button events = new Button("Events");
        events.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(ClickEvent event)
            {
                menuLayout.addComponent(new Label("Thank you for clicking"));
            }
        });
        menuLayout.addComponent(events);

    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet
    {
    }
}
