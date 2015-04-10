package org.vaadin.demo;

import com.vaadin.server.ThemeResource;
import org.vaadin.MyComponent;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.touchmenu.TouchMenu;
import org.vaadin.touchmenu.TouchMenuButton;

@Theme("demo")
@Title("MyComponent Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        // Initialize our new UI component
        final TouchMenu component = new TouchMenu(3, 2);

        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.addComponent(component);
        layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
        setContent(layout);

    }

    private TouchMenuButton getButton() {
        TouchMenuButton button = new TouchMenuButton("Button");

        button.setIcon(new ThemeResource("capsule-48x48.png"));

        return button;
    }

}
