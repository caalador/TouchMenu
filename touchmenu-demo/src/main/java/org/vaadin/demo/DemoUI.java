package org.vaadin.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.touchmenu.TouchMenu;
import org.vaadin.touchmenu.TouchMenuButton;
import org.vaadin.touchmenu.client.Direction;

import javax.servlet.annotation.WebServlet;

@Theme("demo")
@Title("MyComponent Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {


    private TextField width, height, rows, columns;
    private CheckBox animate, from, useArrows;
    int next = 0;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        final TouchMenu component = new TouchMenu(2, 3);
        component.setButtonSize(50, 50);

        String sizeW = "500px";
        String sizeH = "300px";
        width = new TextField("Width");
        width.setImmediate(true);
        width.setValue(sizeW);
        height = new TextField("Height");
        height.setImmediate(true);
        height.setValue(sizeH);
        rows = new TextField("Rows");
        rows.setImmediate(true);
        rows.setValue(Integer.toString(component.getRows()));
        columns = new TextField("Columns");
        columns.setImmediate(true);
        columns.setValue(Integer.toString(component.getColumns()));
        animate = new CheckBox("Animate");
        animate.setValue(true);
        animate.setImmediate(true);
        from = new CheckBox("From arrow direction");
        from.setValue(true);
        from.setImmediate(true);
        useArrows = new CheckBox("Use arrows");
        useArrows.setValue(true);
        useArrows.setImmediate(true);
        width.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                component.setWidth(width.getValue());
            }
        });

        height.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                component.setHeight(height.getValue());
            }
        });

        rows.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                component.setRows(Integer.parseInt(rows.getValue()));
            }
        });

        columns.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                component.setColumns(Integer.parseInt(columns.getValue()));
            }
        });

        animate.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                component.setAnimate(animate.getValue());
            }
        });

        from.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                component.setDirection(from.getValue() ? Direction.IN_FROM_SAME : Direction.IN_FROM_OPPOSITE);
            }
        });

        useArrows.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                component.setArrowNavigationEnabled(useArrows.getValue());
            }
        });
        HorizontalLayout hl = new HorizontalLayout(width, height, rows, columns);
        hl.setSpacing(true);

        // Check boxes + button size drop down
        HorizontalLayout hl2 = new HorizontalLayout(animate, from, useArrows);
//        hl2.addComponent(sizes);
        hl2.setSpacing(true);

        // Initialize our new UI component
        TouchMenuButton button = getButton("cake-48x48", "hundred", "position");
        button.setCaption("Cake");
        button.setWidth(100, Unit.PIXELS);
        button.setHeight(100, Unit.PIXELS);
        component.addComponent(button);

        button = getButton();
        button.setIcon(null);
        component.addComponent(button);

        button = getButton("capsule-48x48", "hundred", "position");
        button.setCaption("Capsule button");
        button.setHeight(100, Unit.PIXELS);
        button.setWidth(100, Unit.PIXELS);
        component.addComponent(button);

        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());
        component.addComponent(getButton());

        button = getButton();
        button.setWidth(75, Unit.PIXELS);
        button.setStyleName("hundred");
        component.addComponent(button);

        component.addComponent(getButton());
        component.addComponent(getButton());

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.addComponents(hl, hl2, component);
        layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
        layout.setExpandRatio(component, 1);
        setContent(layout);

    }

    private TouchMenuButton getButton(String icon, String... styles) {
        TouchMenuButton button = getButton(icon);
        for(String style: styles) {
            button.addStyleName(style);
        }
        return button;
    }

    private TouchMenuButton getButton(String icon) {
        TouchMenuButton button = new TouchMenuButton(captions[next % captions.length], new ThemeResource("images/" + icon + ".png"));
        next++;

        button.addButtonClickedListener(new TouchMenuButton.ButtonListener() {
            @Override
            public void buttonClicked(TouchMenuButton button) {
                Notification.show("Button clicked! " + button.getId());
            }
        });
        return button;
    }

    private TouchMenuButton getButton() {
        TouchMenuButton button = new TouchMenuButton(captions[next % captions.length]);
        next++;

        button.addButtonClickedListener(new TouchMenuButton.ButtonListener() {
            @Override
            public void buttonClicked(TouchMenuButton button) {
                Notification.show("Button clicked! " + button.getId());
            }
        });
        return button;
    }


    String[] captions = new String[]{
            "Cake", "Capsule button", "Coffee", "Sugar", "Honey", "Rain", "Movies"
    };
}
