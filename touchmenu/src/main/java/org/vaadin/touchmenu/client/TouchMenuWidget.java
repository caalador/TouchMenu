package org.vaadin.touchmenu.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.VConsole;
import org.vaadin.touchmenu.client.button.TouchMenuButtonWidget;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class TouchMenuWidget extends AbsolutePanel implements MouseDownHandler, MouseMoveHandler, MouseOutHandler, MouseUpHandler, ClickHandler, TouchStartHandler, TouchMoveHandler, TouchEndHandler {

    private static final String BASE_NAME = "touchmenu";

    public static final String CLASSNAME = "c-" + BASE_NAME;

    private Button navigateLeft, navigateRight;
    private AbsolutePanel touchArea, touchView;

    private List<TouchMenuButtonWidget> widgets = new LinkedList<TouchMenuButtonWidget>();

    protected int columns, rows;

    protected int firstVisibleColumn = 0;
    protected Direction buttonDirection = Direction.IN_FROM_SAME;

    private boolean useArrows = true;
    private boolean move = false;
    private boolean dragged = false;
    private boolean definedSizes = false;
    protected boolean animate;

    private int endValue = 0;
    private int xDown = 0;
    private int start = 0;
    private int end = 0;
    private int maxValue = 0;

    public TouchMenuButtonWidget mouseDownButton;
    protected int definedWidth, definedHeight;


    public TouchMenuWidget() {
        super();
        getElement().getStyle().setPosition(Style.Position.RELATIVE);

        setStyleName(CLASSNAME);

        navigateLeft = new Button();
        navigateRight = new Button();
        navigateLeft.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                switch (buttonDirection) {
                    case IN_FROM_OPPOSITE:
                        firstVisibleColumn++;
                        break;
                    case IN_FROM_SAME:
                        firstVisibleColumn--;
                        break;
                }
                transitionToColumn();
            }
        });
        navigateRight.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                switch (buttonDirection) {
                    case IN_FROM_OPPOSITE:
                        firstVisibleColumn--;
                        break;
                    case IN_FROM_SAME:
                        firstVisibleColumn++;
                        break;
                }
                transitionToColumn();
            }
        });
        navigateLeft.getElement().setClassName("left-navigation");
        navigateRight.getElement().setClassName("right-navigation");

        navigateLeft.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        navigateLeft.getElement().getStyle().setWidth(40, Style.Unit.PX);
        navigateRight.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        navigateRight.getElement().getStyle().setWidth(40, Style.Unit.PX);

        transparentFirst();

        touchArea = new AbsolutePanel();
        touchArea.setHeight("100%");
        touchArea.getElement().setClassName("touch-area");
        touchArea.getElement().getStyle().setOverflow(Style.Overflow.VISIBLE);

        touchView = new AbsolutePanel();
        touchView.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        touchView.setHeight("100%");

        // Add mouse event handlers
        touchView.addDomHandler(this, MouseDownEvent.getType());
        touchView.addDomHandler(this, MouseMoveEvent.getType());
        touchView.addDomHandler(this, MouseUpEvent.getType());
        touchView.addDomHandler(this, MouseOutEvent.getType());
        touchView.addDomHandler(this, ClickEvent.getType());
        if (TouchEvent.isSupported()) {
            // Add touch event handlers
            touchView.addDomHandler(this, TouchStartEvent.getType());
            touchView.addDomHandler(this, TouchMoveEvent.getType());
            touchView.addDomHandler(this, TouchEndEvent.getType());
        }

        touchView.add(touchArea);

        add(navigateLeft);
        add(touchView);
        add(navigateRight);

        positionElements();
    }

    private void positionElements() {
        navigateLeft.getElement().getStyle().setLeft(0, Style.Unit.PX);
        navigateLeft.getElement().getStyle().setTop(0, Style.Unit.PX);
        navigateLeft.getElement().getStyle().setHeight(100, Style.Unit.PCT);

        touchView.getElement().getStyle().setLeft(40, Style.Unit.PX);

        navigateRight.getElement().getStyle().setRight(0, Style.Unit.PX);
        navigateRight.getElement().getStyle().setTop(0, Style.Unit.PX);
        navigateRight.getElement().getStyle().setHeight(100, Style.Unit.PCT);
    }

    public void setUseArrows(boolean useArrows) {
        this.useArrows = useArrows;
        if (useArrows) {
            positionElements();
            touchView.getElement().getStyle().setWidth(getOffsetWidth() - 80, Style.Unit.PX);
            navigateLeft.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
            navigateRight.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        } else {
            touchView.getElement().getStyle().setLeft(0, Style.Unit.PX);
            touchView.getElement().getStyle().setWidth(getOffsetWidth(), Style.Unit.PX);
            navigateLeft.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
            navigateRight.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
        layoutWidgets();
        transitionToColumn();
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
        layoutWidgets();
        transitionToColumn();
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
        layoutWidgets();
        transitionToColumn();
    }

    public void setUseDefinedSizes(boolean useDefinedButtonSize) {
        definedSizes = useDefinedButtonSize;
        layoutWidgets();
        transitionToColumn();
    }

    /**
     * Mouse and Touch event handling.
     */

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        if (mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
            return;
        }
        Element relativeElement = mouseDownEvent.getRelativeElement();
        if (!relativeElement.equals(navigateLeft) && !relativeElement.equals(navigateRight)) {
            checkForButtonWidget(mouseDownEvent.getNativeEvent());

            removeStyleVersions(touchArea.getElement().getStyle(), "transition");
            removeStyleVersions(touchArea.getElement().getStyle(), "transitionProperty");
            mouseDownEvent.preventDefault();
            move = true;
            xDown = mouseDownEvent.getClientX();
            start = mouseDownEvent.getClientX();
        }
    }

    private void checkForButtonWidget(NativeEvent nativeEvent) {
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

    @Override
    public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
        if (move) {
            int current = touchArea.getElement().getOffsetLeft();
            current += mouseMoveEvent.getClientX() - xDown;
            xDown = mouseMoveEvent.getClientX();

            touchArea.getElement().getStyle().setLeft(current, Style.Unit.PX);
            if (mouseDownButton != null && !mouseDownButton.isIgnoreClick()) {
                mouseDownButton.ignoreClick(true);
            }
            dragged = true;
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent mouseUpEvent) {
        move = false;
        end = mouseUpEvent.getClientX();
        if (dragged)
            moveEnd();
    }

    @Override
    public void onMouseOut(MouseOutEvent mouseOutEvent) {
        move = false;
        end = mouseOutEvent.getClientX();
        if (dragged)
            moveEnd();
    }

    @Override
    public void onClick(ClickEvent event) {
        if (mouseDownButton != null) {
            event.stopPropagation();
            mouseDownButton.ignoreClick(false);
            mouseDownButton = null;
        }
    }

    private void moveEnd() {
        dragged = false;

        if (touchArea.getElement().getOffsetLeft() > 0) {
            firstVisibleColumn = 0;
            transitionToColumn();
        } else if (touchArea.getElement().getOffsetLeft() < -(endValue - touchView.getOffsetWidth())) {
            firstVisibleColumn = maxValue;
            transitionToColumn();
        } else {
            int firstVisible = Math.abs(touchView.getWidgetLeft(touchArea) / step);

            // scroll forward column if moved "forward" a bit but not over one column
            if (start > end && (start - end) < step) {
                firstVisible++;
            }

            firstVisibleColumn = firstVisible;

            transitionToColumn();
        }
    }

    private void transitionToColumn() {
        if (firstVisibleColumn < 0) {
            firstVisibleColumn = 0;
        } else if (firstVisibleColumn > maxValue) {
            firstVisibleColumn = maxValue;
        }

        setTransitionToArea();

        int value = firstVisibleColumn * step;

        if (value > (endValue - touchView.getOffsetWidth())) {
            value = (endValue - touchView.getOffsetWidth());
        }
        touchArea.getElement().getStyle().setLeft(-value, Style.Unit.PX);

        navigateLeft.setEnabled(true);
        navigateRight.setEnabled(true);

        if (firstVisibleColumn == 0) {
            transparentFirst();
        } else if (firstVisibleColumn == maxValue) {
            transparentLast();
        }
    }

    private void transparentFirst() {
        switch (buttonDirection) {
            case IN_FROM_OPPOSITE:
                navigateRight.setEnabled(false);
                break;
            case IN_FROM_SAME:
                navigateLeft.setEnabled(false);
                break;
        }
    }

    private void transparentLast() {
        switch (buttonDirection) {
            case IN_FROM_OPPOSITE:
                navigateLeft.setEnabled(false);
                break;
            case IN_FROM_SAME:
                navigateRight.setEnabled(false);
                break;
        }
    }

    private void setTransitionToArea() {
        if (animate) {
            addStyleVersions(touchArea.getElement().getStyle(), "transition", "all 1s ease");
            addStyleVersions(touchArea.getElement().getStyle(), "transitionProperty", "left");
        }
    }

    @Override
    public void onTouchStart(TouchStartEvent touchStartEvent) {
        VConsole.log(" === TouchStart");
        Element relativeElement = touchStartEvent.getRelativeElement();
        checkForButtonWidget(touchStartEvent.getNativeEvent());

        removeStyleVersions(touchArea.getElement().getStyle(), "transition");
        removeStyleVersions(touchArea.getElement().getStyle(), "transitionProperty");
        touchStartEvent.preventDefault();
        Touch touch = touchStartEvent.getTouches().get(0);
        xDown = touch.getPageX();
        start = touch.getPageX();

    }

    @Override
    public void onTouchEnd(TouchEndEvent touchEndEvent) {
        move = false;
        Touch touch = touchEndEvent.getTouches().get(0);
        if(touch != null) {
            end = touch.getPageX();
        }
        if (dragged) {
            touchEndEvent.preventDefault();
            VConsole.log(" === TouchEnd");
            moveEnd();
        }
    }

    @Override
    public void onTouchMove(TouchMoveEvent touchMoveEvent) {
        int current = touchArea.getElement().getOffsetLeft();
        Touch touch = touchMoveEvent.getTouches().get(0);
        if (Math.abs(touch.getPageX() - xDown) < 5) {
            return;

        }
        dragged = true;
        VConsole.log(" === TouchMove");

        current += touch.getPageX() - xDown;
        xDown = touch.getPageX();

        touchArea.getElement().getStyle().setLeft(current, Style.Unit.PX);
        if (mouseDownButton != null && !mouseDownButton.isIgnoreClick()) {
            mouseDownButton.ignoreClick(true);
        }
    }

    public void clear() {
        widgets.clear();
        touchArea.clear();
    }

    public void add(TouchMenuButtonWidget widget) {
        widgets.add(widget);
        widget.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        touchArea.add(widget);
    }

    public void setViewSize() {
        int touchViewWidth = useArrows ? getElement().getClientWidth() - 80 : getElement().getClientWidth();
        touchView.getElement().getStyle().setWidth(touchViewWidth, Style.Unit.PX);
    }

    /**
     * Check column amount fits the content area. Else update column amount so that we fit inside.
     */
    public void validateColumns() {
        if (columns * widgets.get(0).getOffsetWidth() > touchView.getElement().getClientWidth()) {
            columns = touchArea.getElement().getClientWidth() / widgets.get(0).getOffsetWidth();
        }
    }

    /**
     * Check row amount fits the content area. Else update row amount so that we fit inside.
     */
    public void validateRows() {
        if (rows * widgets.get(0).getOffsetHeight() > touchView.getElement().getClientHeight()) {
            rows = touchArea.getElement().getClientHeight() / widgets.get(0).getOffsetHeight();
        }
    }

    private int step, column;

    public void layoutWidgets() {
        int touchViewWidth = useArrows ? getElement().getClientWidth() - 80 : getElement().getClientWidth();
        int touchViewHeight = getElement().getClientHeight();
        touchView.getElement().getStyle().setWidth(touchViewWidth, Style.Unit.PX);

        if (widgets.isEmpty()) {
            return;
        }

        int itemWidth;
        int itemHeight;
        if (definedSizes) {
            itemWidth = definedWidth;
            itemHeight = definedHeight;
        } else {
            itemWidth = widgets.get(0).getElement().getClientWidth();
            itemHeight = widgets.get(0).getElement().getClientHeight();
        }

        int columnMargin = (int) Math.ceil((touchViewWidth / columns - itemWidth) / 2);
        int rowMargin = (int) Math.ceil((touchViewHeight / rows - itemHeight) / 2);

        step = 2 * columnMargin + itemWidth;

        int left = columnMargin;
        column = step + columnMargin;

        int item = 0;
        maxValue = 0;

        // Position buttons into touchArea.
        // No extra positioning needed as we move touchArea instead of the buttons.
        for (TouchMenuButtonWidget button : widgets) {
            if (item > 0 && item % rows == 0) {
                left += step;
                maxValue++;
            }
            int buttonLeft = left;
            int buttonTop = rowMargin + ((item % rows) * (2 * rowMargin + itemHeight));

            int buttonWidth = button.getElement().getClientWidth();
            if (buttonWidth != itemWidth) {
                if (buttonWidth > itemWidth) {
                    buttonLeft -= (buttonWidth - itemWidth) / 2;
                } else {
                    buttonLeft += (itemWidth - buttonWidth) / 2;
                }
            }

            int buttonHeight = button.getElement().getClientHeight();
            if (buttonHeight != itemHeight) {
                if (buttonHeight > itemHeight) {
                    buttonTop -= (buttonHeight - itemHeight) / 2;
                } else {
                    buttonTop += (itemHeight - buttonHeight) / 2;
                }
            }

            Style style = button.getElement().getStyle();
            style.setLeft(buttonLeft, Style.Unit.PX);
            style.setTop(buttonTop, Style.Unit.PX);

            item++;
        }

        endValue = left + step - columnMargin;
        maxValue -= columns - 1;
    }

    private void addStyleVersions(Style style, String baseProperty, String value) {
        style.setProperty(baseProperty, value);

        // Make transition method first character uppercase
        char[] chars = baseProperty.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        baseProperty = new String(chars);

        style.setProperty("Moz" + baseProperty, value);
        style.setProperty("Webkit" + baseProperty, value);
    }

    private void removeStyleVersions(Style style, String baseProperty) {
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

    public void setDirection(Direction direction) {
        buttonDirection = direction;
        int maxValue = (endValue - touchView.getOffsetWidth()) / step;

        navigateLeft.setEnabled(true);
        navigateRight.setEnabled(true);

        if (firstVisibleColumn == 0) {
            transparentFirst();
        } else if (firstVisibleColumn == maxValue) {
            transparentLast();
        }
    }
}
