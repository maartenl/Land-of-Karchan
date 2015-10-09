package awesomeness.vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

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
        Panel mainPanel = new Panel("Administration pages");
        setContent(mainPanel);

        // Create the accordion
        Accordion accordion = new Accordion();
        mainPanel.setContent(accordion);

        // Create the first tab, specify caption when adding
        Layout worldattributes = new WorldAttributes(); // Wrap in a layout
        accordion.addTab(worldattributes, "World attributes");

        Layout scripts = new Scripts();
        accordion.addTab(scripts, "Scripts");

        Layout areas = new Areas();
        accordion.addTab(areas, "Areas");

        Layout events = new Events();
        accordion.addTab(events, "Events");
    }

}
