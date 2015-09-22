package org.vaadin.touchmenu.client.flow;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.vaadin.touchmenu.client.Direction;
import org.vaadin.touchmenu.client.button.TouchMenuButtonWidget;

import java.util.LinkedList;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public abstract class AbstractFlowView extends AbsolutePanel implements FlowView {

    public int rows, columns, step;

    protected LinkedList<Widget> widgets = new LinkedList<Widget>();

    public int endValue = 0;
    protected int down = 0;
    protected int start = 0;
    protected int end = 0;
    protected int maxValue = 0;

    public TouchMenuButtonWidget mouseDownButton;
    public Button navigateLeft, navigateRight;

    protected Direction buttonDirection = Direction.IN_FROM_SAME;

    protected boolean move = false;
    protected boolean dragged = false;
    AbsolutePanel touchView;

    public int firstVisibleColumn = 0;
    public int definedWidth, definedHeight;

    public boolean animate;
    public boolean useArrows = true;
    public boolean definedSizes = false;

    public AbstractFlowView(AbsolutePanel touchView) {
        this.touchView = touchView;
    }

    @Override
    public void clear() {
        super.clear();
        widgets.clear();
    }

    @Override
    public void setColumns(int columns) {
        this.columns = columns;
        layoutWidgets();
        transitionToColumn();

    }

    @Override
    public void setRows(int rows) {
        this.rows = rows;
        layoutWidgets();
        transitionToColumn();
    }

    /**
     * Check column amount fits the content area. Else update column amount so that we fit inside.
     */
    public void validateColumns() {
        if (!widgets.isEmpty() && columns * widgets.getFirst().getOffsetWidth() > touchView.getElement().getClientWidth()) {
            columns = touchView.getElement().getClientWidth() / widgets.getFirst().getOffsetWidth();
        }
    }

    /**
     * Check row amount fits the content area. Else update row amount so that we fit inside.
     */
    public void validateRows() {
        if (!widgets.isEmpty() && rows * widgets.getFirst().getOffsetHeight() > touchView.getElement().getClientHeight()) {
            rows = touchView.getElement().getClientHeight() / widgets.getFirst().getOffsetHeight();
        }
    }

    @Override
    public void add(Widget w) {
        super.add(w);
        widgets.add(w);
        w.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
    }

    public void transparentFirst() {
        switch (buttonDirection) {
            case IN_FROM_OPPOSITE:
                navigateRight.setEnabled(false);
                break;
            case IN_FROM_SAME:
                navigateLeft.setEnabled(false);
                break;
        }
    }

    public void transparentLast() {
        switch (buttonDirection) {
            case IN_FROM_OPPOSITE:
                navigateLeft.setEnabled(false);
                break;
            case IN_FROM_SAME:
                navigateRight.setEnabled(false);
                break;
        }
    }

    protected void addStyleVersions(Style style, String baseProperty, String value) {
        style.setProperty(baseProperty, value);

        // Make transition method first character uppercase
        char[] chars = baseProperty.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        baseProperty = new String(chars);

        style.setProperty("Moz" + baseProperty, value);
        style.setProperty("Webkit" + baseProperty, value);
    }

    protected void removeStyleVersions(Style style, String baseProperty) {
        style.clearProperty(baseProperty);

        // Make transition method first character uppercase
        char[] chars = baseProperty.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        baseProperty = new String(chars);

        style.clearProperty("Moz" + baseProperty);
        style.clearProperty("Webkit" + baseProperty);
        style.clearProperty("Ms" + baseProperty);
        style.clearProperty("O" + baseProperty);
    }


    protected void checkForButtonWidget(NativeEvent nativeEvent) {
        Element element = DOM.eventGetTarget(Event.as(nativeEvent));
        IsWidget widget = null;

        if (element.getClassName().contains(TouchMenuButtonWidget.CLASSNAME)) {
            widget = getWidget(element);
        } else if (element.getParentElement().getClassName().contains(TouchMenuButtonWidget.CLASSNAME)) {
            widget = getWidget(element.getParentElement());
        }

        if (widget != null) {
            mouseDownButton = (TouchMenuButtonWidget) widget.asWidget();
        }
    }

    /**
     * Get widget for element through it's associated eventListener.
     *
     * @param element Element to get widget for.
     * @return Widget if found else null.
     */
    public static IsWidget getWidget(Element element) {
        EventListener listener = DOM.getEventListener(element);

        // No listener attached to the element, so no widget exist for this
        // element
        if (listener == null) {
            return null;
        }
        if (listener instanceof Widget) {
            // GWT uses the widget as event listener
            return (Widget) listener;
        }
        return null;
    }

    protected void setTransitionToArea() {
        if (animate) {
            addStyleVersions(getElement().getStyle(), "transition", "all 1s ease");
            addStyleVersions(getElement().getStyle(), "transitionProperty", "left top");
        }
    }
}
