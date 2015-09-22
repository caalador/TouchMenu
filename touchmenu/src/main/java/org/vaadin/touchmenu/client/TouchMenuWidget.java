package org.vaadin.touchmenu.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.vaadin.client.VConsole;
import org.vaadin.touchmenu.client.button.TouchMenuButtonWidget;
import org.vaadin.touchmenu.client.flow.AbstractFlowView;
import org.vaadin.touchmenu.client.flow.HorizontalFlowView;
import org.vaadin.touchmenu.client.flow.VerticalFlowView;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class TouchMenuWidget extends AbsolutePanel {

    private static final String BASE_NAME = "touchmenu";

    public static final String CLASSNAME = "c-" + BASE_NAME;

    private Button navigateLeft, navigateRight;
    private AbsolutePanel touchView;
    private AbstractFlowView touchArea;

    protected Direction buttonDirection = Direction.IN_FROM_SAME;

    private List<HandlerRegistration> domHandlers = new LinkedList<HandlerRegistration>();
    private ScrollDirection flowView = ScrollDirection.HORIZONTAL;

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
                        touchArea.firstVisibleColumn++;
                        break;
                    case IN_FROM_SAME:
                        touchArea.firstVisibleColumn--;
                        break;
                }
                touchArea.transitionToColumn();
            }
        });
        navigateRight.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                switch (buttonDirection) {
                    case IN_FROM_OPPOSITE:
                        touchArea.firstVisibleColumn--;
                        break;
                    case IN_FROM_SAME:
                        touchArea.firstVisibleColumn++;
                        break;
                }
                touchArea.transitionToColumn();
            }
        });
        navigateLeft.getElement().setClassName("left-navigation");
        navigateRight.getElement().setClassName("right-navigation");

        navigateLeft.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        navigateLeft.getElement().getStyle().setWidth(40, Style.Unit.PX);
        navigateRight.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        navigateRight.getElement().getStyle().setWidth(40, Style.Unit.PX);


        touchView = new AbsolutePanel();
        touchView.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        touchView.setHeight("100%");

        setFlowView(flowView);

        add(navigateLeft);
        add(touchView);
        add(navigateRight);

        positionElements();
    }

    protected void positionElements() {
        navigateLeft.getElement().getStyle().setLeft(0, Style.Unit.PX);
        navigateLeft.getElement().getStyle().setTop(0, Style.Unit.PX);
        if(flowView.equals(ScrollDirection.HORIZONTAL)) {
            navigateLeft.getElement().getStyle().setHeight(100, Style.Unit.PCT);
            navigateLeft.getElement().getStyle().setWidth(40, Style.Unit.PX);
        }else{
            navigateLeft.getElement().getStyle().setWidth(100, Style.Unit.PCT);
            navigateLeft.getElement().getStyle().setHeight(40, Style.Unit.PX);
        }

        if(flowView.equals(ScrollDirection.HORIZONTAL)) {
            touchView.getElement().getStyle().setWidth(getOffsetWidth() - 80, Style.Unit.PX);
            touchView.getElement().getStyle().setHeight(getOffsetHeight(), Style.Unit.PX);
            touchView.getElement().getStyle().setLeft(40, Style.Unit.PX);
            touchView.getElement().getStyle().setTop(0, Style.Unit.PX);
        }else{
            touchView.getElement().getStyle().setHeight(getOffsetHeight() - 80, Style.Unit.PX);
            touchView.getElement().getStyle().setWidth(getOffsetWidth(), Style.Unit.PX);
            touchView.getElement().getStyle().setLeft(0, Style.Unit.PX);
            touchView.getElement().getStyle().setTop(40, Style.Unit.PX);
        }

        if(flowView.equals(ScrollDirection.HORIZONTAL)) {
            navigateRight.getElement().getStyle().setRight(0, Style.Unit.PX);
            navigateRight.getElement().getStyle().clearLeft();
            navigateRight.getElement().getStyle().setTop(0, Style.Unit.PX);
            navigateRight.getElement().getStyle().setHeight(100, Style.Unit.PCT);
            navigateRight.getElement().getStyle().setWidth(40, Style.Unit.PX);
        }else{
            navigateRight.getElement().getStyle().setLeft(0, Style.Unit.PX);
            navigateRight.getElement().getStyle().clearRight();
            navigateRight.getElement().getStyle().setTop(getOffsetHeight()-40, Style.Unit.PX);
            navigateRight.getElement().getStyle().setWidth(100, Style.Unit.PCT);
            navigateRight.getElement().getStyle().setHeight(40, Style.Unit.PX);
        }
    }

    public void setUseArrows(boolean useArrows) {
        touchArea.useArrows = useArrows;
        // TODO: Check for direction if navigation on top-bottom or on the sides
        if (useArrows) {
            positionElements();

            navigateLeft.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
            navigateRight.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        } else {
            touchView.getElement().getStyle().setLeft(0, Style.Unit.PX);
            touchView.getElement().getStyle().setTop(0, Style.Unit.PX);
            touchView.getElement().getStyle().setHeight(getOffsetHeight(), Style.Unit.PX);
            touchView.getElement().getStyle().setWidth(getOffsetWidth(), Style.Unit.PX);
            navigateLeft.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
            navigateRight.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
        touchArea.layoutWidgets();
        touchArea.transitionToColumn();
    }

    public void setColumns(int columns) {
        touchArea.setColumns(columns);
    }

    public void setRows(int rows) {
        touchArea.setRows(rows);
    }

    public void setUseDefinedSizes(boolean useDefinedButtonSize) {
        touchArea.definedSizes = useDefinedButtonSize;
        touchArea.layoutWidgets();
        touchArea.transitionToColumn();
    }

    public void clear() {
        touchArea.clear();
    }

    public void add(TouchMenuButtonWidget widget) {
        touchArea.add(widget);
    }

    public void setDirection(Direction direction) {
        buttonDirection = direction;
        touchArea.setDirection(direction);
    }

    public void validateRows() {
        touchArea.validateRows();
    }

    public void validateColumns() {
        touchArea.validateColumns();
    }

    public void layoutWidgets() {
        touchArea.layoutWidgets();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        touchArea.layoutWidgets();
    }

    public void setDefinedWidth(int definedWidth) {
        touchArea.definedWidth = definedWidth;
    }

    public void setDefinedHeight(int definedHeight) {
        touchArea.definedHeight = definedHeight;
    }

    public void setAnimate(boolean animate) {
        touchArea.animate = animate;
    }

    public void setFlowView(ScrollDirection flowView) {
        this.flowView = flowView;
        if (touchArea != null) {
            touchView.remove(touchArea);
        }
        for (HandlerRegistration handler : domHandlers) {
            handler.removeHandler();
        }
        domHandlers.clear();
        positionElements();

        switch (flowView) {
            case HORIZONTAL:
                touchArea = new HorizontalFlowView(touchView);
                navigateLeft.getElement().removeClassName("up-navigation");
                navigateRight.getElement().removeClassName("down-navigation");
                navigateLeft.getElement().setClassName("left-navigation");
                navigateRight.getElement().setClassName("right-navigation");
                break;
            case VERTICAL:
                touchArea = new VerticalFlowView(touchView);
                navigateLeft.getElement().removeClassName("left-navigation");
                navigateRight.getElement().removeClassName("right-navigation");
                navigateLeft.getElement().setClassName("up-navigation");
                navigateRight.getElement().setClassName("down-navigation");
                break;
        }

        touchArea.navigateLeft = navigateLeft;
        touchArea.navigateRight = navigateRight;

        touchArea.transparentFirst();

        // Add mouse event handlers
        domHandlers.add(touchView.addDomHandler(touchArea, MouseDownEvent.getType()));
        domHandlers.add(touchView.addDomHandler(touchArea, MouseMoveEvent.getType()));
        domHandlers.add(touchView.addDomHandler(touchArea, MouseUpEvent.getType()));
        domHandlers.add(touchView.addDomHandler(touchArea, MouseOutEvent.getType()));
        domHandlers.add(touchView.addDomHandler(touchArea, ClickEvent.getType()));
        if (TouchEvent.isSupported()) {
            // Add touch event handlers
            domHandlers.add(touchView.addDomHandler(touchArea, TouchStartEvent.getType()));
            domHandlers.add(touchView.addDomHandler(touchArea, TouchMoveEvent.getType()));
            domHandlers.add(touchView.addDomHandler(touchArea, TouchEndEvent.getType()));
        }

        touchView.add(touchArea);
        VConsole.log(" === added area");
        touchArea.layoutWidgets();
    }

    public ScrollDirection getScrollDirection() {
        return flowView;
    }
}
