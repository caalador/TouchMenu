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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.touchmenu.TouchMenu;
import org.vaadin.touchmenu.TouchMenuButton;
import org.vaadin.touchmenu.client.Direction;
import org.vaadin.touchmenu.client.ScrollDirection;

import javax.servlet.annotation.WebServlet;

@Theme("demo")
@Title("TouchMenu Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {


    private Layout buttonLayout;
    private CheckBox animate, from, useArrows, enabled;
    private NativeSelect scroll;
    private TextField width, height, rows, columns, buttonWidth, buttonHeight, caption;
    private ComboBox background;

    private boolean updating = false;
    private int next = 0;

    private TouchMenuButton selection;
    private TouchMenu touchMenu;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        // Init touch menu.
        touchMenu = new TouchMenu(2, 3);
        touchMenu.setButtonSize(50, 50);
        touchMenu.addTouchMenuListener(touchMenuListener);

        initMenuControlls();

        HorizontalLayout hl = new HorizontalLayout(width, height, rows, columns, scroll);
        hl.setSpacing(true);

        HorizontalLayout hl2 = new HorizontalLayout(animate, from, useArrows);
        hl2.setSpacing(true);


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

        // Build demo layout
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
        width = newTextField("Width", "500px");

        height = newTextField("Height", "300px");

        rows = newTextField("Rows", Integer.toString(touchMenu.getRows()));

        columns = newTextField("Columns", Integer.toString(touchMenu.getColumns()));

        animate = newCheckBox("Animate", touchMenu.isAnimate());
        animate.setDescription("Set if end positioning should be animated or just snap");

        from = newCheckBox("From arrow direction", touchMenu.getDirection().equals(Direction.IN_FROM_SAME));
        from.setDescription("Decide where the items should come in from when clicking an side arrow");

        useArrows = newCheckBox("Use arrows", touchMenu.isArrowNavigationEnabled());
        useArrows.setDescription("Show/hide the naviagtion arrows");

        scroll = newSelect("Scroll Horizontally", ScrollDirection.HORIZONTAL, ScrollDirection.VERTICAL, ScrollDirection.FREE);
        scroll.setDescription("Select wanted scrolling mode");
        scroll.select(touchMenu.getScrollDirection());
        scroll.setNullSelectionAllowed(false);

        // Add listeners
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
        scroll.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                touchMenu.setScrollDirection((ScrollDirection)scroll.getValue());
            }
        });
    }

    private TextField newTextField(String caption, String value) {
        TextField textField = new TextField(caption);
        textField.setImmediate(true);
        textField.setValue(value);

        return textField;
    }

    private CheckBox newCheckBox(String caption, boolean value) {
        CheckBox checkBox = new CheckBox(caption);
        checkBox.setValue(value);
        checkBox.setImmediate(true);
        return checkBox;
    }

    private NativeSelect newSelect(String caption, Object... values) {
        NativeSelect select = new NativeSelect(caption);

        for(Object item:values) {
            select.addItem(item);
        }

        return select;
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
        caption.setDescription("Change the button caption");
        caption.setImmediate(true);
        caption.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (!updating) {
                    selection.setCaption(caption.getValue());
                }
            }
        });
        enabled = newCheckBox("Enabled", false);
        enabled.setDescription("Enable or disable selected button. NOTE! if you click another button this button will stay disabled as you cant 'select' it again.");
        enabled.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (!updating) {
                    selection.setEnabled(enabled.getValue());
                }
            }
        });
        background = new ComboBox("Background");
        background.setNullSelectionAllowed(false);
        background.addItem("50x50");
        background.addItem("100x100");
        background.addItem("150x150");

        background.setDescription("Background is just a css style name that has a predefined image of given size.");

        background.select("50x50");

        background.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (updating) {
                    return;
                }

                if (background.getValue().equals("50x50")) {
                    selection.removeStyleName("hundred");
                    selection.removeStyleName("hundred-fidy");
                } else if (background.getValue().equals("100x100")) {
                    selection.removeStyleName("hundred-fidy");
                    selection.addStyleName("hundred");
                } else if (background.getValue().equals("150x150")) {
                    selection.removeStyleName("hundred");
                    selection.addStyleName("hundred-fidy");
                }
            }
        });

        Button removeButton = new Button("Remove", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                touchMenu.removeComponent(selection);
                layout.setVisible(false);
            }
        });
        layout.addComponents(caption, buttonWidth, buttonHeight, background, enabled, removeButton);
        layout.setComponentAlignment(enabled, Alignment.BOTTOM_CENTER);
        layout.setComponentAlignment(removeButton, Alignment.BOTTOM_CENTER);
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
        enabled.setValue(button.isEnabled());
        background.setValue(button.getStyleName().contains("hundred-fidy") ? "150x150" : button.getStyleName().contains("hundred") ? "100x100" : "50x50");
        updating = false;
    }


    String[] captions = new String[]{
            "Cake", "Capsule button", "Coffee", "Sugar", "Honey", "Rain", "Movies"
    };

    TouchMenu.TouchMenuListener touchMenuListener = new TouchMenu.TouchMenuListener() {
        @Override
        public void buttonClicked(TouchMenuButton button) {
            Notification.show("Button clicked! " + button.getId());
            editButton(button);
        }
    };
}
