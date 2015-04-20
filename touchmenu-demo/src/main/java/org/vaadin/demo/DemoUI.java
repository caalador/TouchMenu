package org.vaadin.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
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
    private Layout buttonLayout;
    private TextField buttonWidth, buttonHeight, caption;
    private boolean updating = false;
    private TouchMenuButton selection;
    private TouchMenu touchMenu;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        touchMenu = new TouchMenu(2, 3);
        touchMenu.setButtonSize(50, 50);

        initMenuControlls();

        HorizontalLayout hl = new HorizontalLayout(width, height, rows, columns);
        hl.setSpacing(true);

        // Check boxes + button size drop down
        HorizontalLayout hl2 = new HorizontalLayout(animate, from, useArrows);
        hl2.setSpacing(true);

        touchMenu.addTouchMenuListener(new TouchMenu.TouchMenuListener() {
            @Override
            public void buttonClicked(TouchMenuButton button) {
                Notification.show("Button clicked! " + button.getId());
                editButton(button);
            }
        });

        // Add buttons to TouchMenu
        TouchMenuButton button = getButton("cake-48x48", "hundred", "position");
        button.setCaption("Cake");
        button.setWidth(100, Unit.PIXELS);
        button.setHeight(100, Unit.PIXELS);
        touchMenu.addComponent(button);

        button = getButton();
        button.setIcon(null);
        touchMenu.addComponent(button);

        button = getButton("capsule-48x48", "hundred", "position");
        button.setCaption("Capsule button");
        button.setHeight(100, Unit.PIXELS);
        button.setWidth(100, Unit.PIXELS);
        touchMenu.addComponent(button);

        touchMenu.addComponent(getButton());
        touchMenu.addComponent(getButton());
        touchMenu.addComponent(getButton());
        touchMenu.addComponent(getButton());
        touchMenu.addComponent(getButton());
        touchMenu.addComponent(getButton());
        touchMenu.addComponent(getButton());

        button = getButton();
        button.setWidth(75, Unit.PIXELS);
        button.setStyleName("hundred");
        touchMenu.addComponent(button);

        touchMenu.addComponent(getButton());
        touchMenu.addComponent(getButton());

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        buttonLayout = buttonInfoLayout();
        layout.addComponents(hl, hl2, buttonLayout, touchMenu);
        layout.setComponentAlignment(touchMenu, Alignment.MIDDLE_CENTER);
        layout.setExpandRatio(touchMenu, 1);
        setContent(layout);

    }

    private void initMenuControlls() {
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
        rows.setValue(Integer.toString(touchMenu.getRows()));
        columns = new TextField("Columns");
        columns.setImmediate(true);
        columns.setValue(Integer.toString(touchMenu.getColumns()));
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
                touchMenu.setWidth(width.getValue());
            }
        });

        height.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                touchMenu.setHeight(height.getValue());
            }
        });

        rows.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                touchMenu.setRows(Integer.parseInt(rows.getValue()));
            }
        });

        columns.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                touchMenu.setColumns(Integer.parseInt(columns.getValue()));
            }
        });

        animate.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                touchMenu.setAnimate(animate.getValue());
            }
        });

        from.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                touchMenu.setDirection(from.getValue() ? Direction.IN_FROM_SAME : Direction.IN_FROM_OPPOSITE);
            }
        });

        useArrows.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                touchMenu.setArrowNavigationEnabled(useArrows.getValue());
            }
        });
    }

    private Layout buttonInfoLayout() {
        final HorizontalLayout layout = new HorizontalLayout();

        layout.setSpacing(true);

        buttonWidth = new TextField("Button Width");
        buttonWidth.setImmediate(true);
        buttonWidth.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (!updating) {
                    selection.setWidth(buttonWidth.getValue());
                    selection.addStyleName("orange");
                }
            }
        });
        buttonHeight = new TextField("Button Height");
        buttonHeight.setImmediate(true);
        buttonHeight.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (!updating) {
                    selection.setHeight(buttonHeight.getValue());
                    selection.addStyleName("orange");
                }
            }
        });
        caption = new TextField("Button caption");
        caption.setImmediate(true);
        caption.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (!updating) {
                    selection.setCaption(caption.getValue());
                }
            }
        });
        Button removeButton = new Button("Remove", new Button.ClickListener(){

            @Override
            public void buttonClick(Button.ClickEvent event) {
                touchMenu.removeComponent(selection);
                layout.setVisible(false);
            }
        });
        layout.addComponents(caption, buttonWidth, buttonHeight, removeButton);
        layout.setVisible(false);

        return layout;
    }

    private TouchMenuButton getButton(String icon, String... styles) {
        TouchMenuButton button = getButton(icon);
        for (String style : styles) {
            button.addStyleName(style);
        }
        return button;
    }

    private TouchMenuButton getButton(String icon) {
        TouchMenuButton button = new TouchMenuButton(captions[next % captions.length], new ThemeResource("images/" + icon + ".png"));
        next++;
        return button;
    }

    private TouchMenuButton getButton() {
        TouchMenuButton button = new TouchMenuButton(captions[next % captions.length]);
        next++;
        return button;
    }

    private void editButton(TouchMenuButton button) {
        selection = button;
        buttonLayout.setVisible(true);

        updating = true;
        buttonHeight.setValue(button.getHeight() + "" + button.getHeightUnits());
        buttonWidth.setValue(button.getWidth() + "" + button.getWidthUnits());
        caption.setValue(button.getCaption());
        updating = false;
    }


    String[] captions = new String[]{
            "Cake", "Capsule button", "Coffee", "Sugar", "Honey", "Rain", "Movies"
    };
}
