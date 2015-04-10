package org.vaadin.touchmenu.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Widget;
import org.vaadin.touchmenu.client.button.TouchMenuButtonWidget;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class TouchMenuWidget extends Widget implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, TouchStartHandler, TouchMoveHandler, TouchEndHandler {

    private static final String BASE_NAME = "touchmenu";

    public static final String CLASSNAME = "c-" + BASE_NAME;

    private ButtonElement navigateLeft, navigateRight;
    private DivElement touchArea, touchView;

    private List<TouchMenuButtonWidget> widgets = new LinkedList<TouchMenuButtonWidget>();

    protected int columns, rows;
    protected int selected = 0;
    private boolean useArrows = true;

    public TouchMenuWidget() {
        super();
        setElement(Document.get().createDivElement());
        getElement().getStyle().setPosition(Style.Position.RELATIVE);

        setStyleName(CLASSNAME);

        // Add mouse event handlers
        addDomHandler(this, MouseDownEvent.getType());
        addDomHandler(this, MouseMoveEvent.getType());
        addDomHandler(this, MouseUpEvent.getType());
        if (TouchEvent.isSupported()) {
            // Add touch event handlers
            addDomHandler(this, TouchStartEvent.getType());
            addDomHandler(this, TouchMoveEvent.getType());
            addDomHandler(this, TouchEndEvent.getType());
        }


        navigateLeft = Document.get().createButtonElement();
        navigateRight = Document.get().createButtonElement();

        navigateLeft.setClassName("left-navigation");
        navigateRight.setClassName("right-navigation");

        navigateLeft.getStyle().setPosition(Style.Position.ABSOLUTE);
        navigateLeft.getStyle().setWidth(40, Style.Unit.PX);
        navigateRight.getStyle().setPosition(Style.Position.ABSOLUTE);
        navigateRight.getStyle().setWidth(40, Style.Unit.PX);

        setTransparent(navigateLeft);

        touchArea = Document.get().createDivElement();
        touchArea.setClassName("touch-area");
        touchArea.getStyle().setPosition(Style.Position.ABSOLUTE);
        touchArea.getStyle().setHeight(100, Style.Unit.PCT);
        touchArea.getStyle().setOverflow(Style.Overflow.VISIBLE);

        touchView = Document.get().createDivElement();
        touchView.getStyle().setPosition(Style.Position.ABSOLUTE);
        touchView.getStyle().setOverflow(Style.Overflow.HIDDEN);
        touchView.getStyle().setHeight(100, Style.Unit.PCT);

        touchView.appendChild(touchArea);

        getElement().appendChild(navigateLeft);
        getElement().appendChild(touchView);
        getElement().appendChild(navigateRight);

        positionElements();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
            }
        });
    }

    private void positionElements() {
        navigateLeft.getStyle().setLeft(0, Style.Unit.PX);
        navigateLeft.getStyle().setHeight(100, Style.Unit.PCT);//getOffsetHeight(), Style.Unit.PX);

//        touchArea.getStyle().setLeft(40, Style.Unit.PX);
        touchView.getStyle().setLeft(40, Style.Unit.PX);

        navigateRight.getStyle().setRight(0, Style.Unit.PX);
        navigateRight.getStyle().setHeight(100, Style.Unit.PCT);//getOffsetHeight(), Style.Unit.PX);
    }

    public void setUseArrows(boolean useArrows) {
        this.useArrows = useArrows;
        if (useArrows) {
            positionElements();
            touchView.getStyle().setWidth(getOffsetWidth() - 80, Style.Unit.PX);
            navigateLeft.getStyle().setVisibility(Style.Visibility.VISIBLE);
            navigateRight.getStyle().setVisibility(Style.Visibility.VISIBLE);
        } else {
//            touchArea.getStyle().setLeft(0, Style.Unit.PX);
            touchView.getStyle().setLeft(0, Style.Unit.PX);
            touchView.getStyle().setWidth(getOffsetWidth(), Style.Unit.PX);
            navigateLeft.getStyle().setVisibility(Style.Visibility.HIDDEN);
            navigateRight.getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
        layoutWidgets();
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
        layoutWidgets();
    }

    /**
     * Mouse and Touch event handling.
     */


    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {

    }

    @Override
    public void onMouseMove(MouseMoveEvent mouseMoveEvent) {

    }

    @Override
    public void onMouseUp(MouseUpEvent mouseUpEvent) {

    }

    @Override
    public void onTouchEnd(TouchEndEvent touchEndEvent) {

    }

    @Override
    public void onTouchMove(TouchMoveEvent touchMoveEvent) {

    }

    @Override
    public void onTouchStart(TouchStartEvent touchStartEvent) {

    }

    public void clear() {
        widgets.clear();
        touchArea.removeAllChildren();
    }

    public void add(TouchMenuButtonWidget widget) {
        widgets.add(widget);
        widget.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        touchArea.appendChild(widget.getElement());
    }

    public void setViewSize() {
        int touchViewWidth = useArrows ? getElement().getClientWidth() - 80: getElement().getClientWidth();
        touchView.getStyle().setWidth(touchViewWidth, Style.Unit.PX);
    }

    /**
     * Check column amount fits the content area. Else update column amount so that we fit inside.
     */
    public void validateColumns() {
        if (columns * widgets.get(0).getOffsetWidth() > touchView.getClientWidth()) {
            columns = touchArea.getClientWidth() / widgets.get(0).getOffsetWidth();
        }
    }

    /**
     * Check row amount fits the content area. Else update row amount so that we fit inside.
     */
    public void validateRows() {
        if (rows * widgets.get(0).getOffsetHeight() > touchView.getClientHeight()) {
            rows = touchArea.getClientHeight() / widgets.get(0).getOffsetHeight();
        }
    }

    public void layoutWidgets() {
        int touchViewWidth = useArrows ? getElement().getClientWidth() - 80: getElement().getClientWidth();
        int touchViewHeight = getElement().getClientHeight();
        touchView.getStyle().setWidth(touchViewWidth, Style.Unit.PX);

        int itemWidth = widgets.isEmpty() ? 50 : widgets.get(0).getElement().getClientWidth();
        int itemHeight = widgets.isEmpty() ? 50 : widgets.get(0).getElement().getClientHeight();

        int columnMargin = (int) Math.ceil((touchViewWidth / columns - itemWidth) / 2);
        int rowMargin = (int) Math.ceil((touchViewHeight / rows - itemHeight) / 2);

        int step = 2 * columnMargin + itemWidth;

        int left = columnMargin;
        int item = 0;

        // Position buttons into touchArea.
        // No extra positioning needed as we move touchArea instead of the buttons.
        for (TouchMenuButtonWidget button : widgets) {
            Style style = button.getElement().getStyle();
            style.setLeft(left, Style.Unit.PX);
            style.setTop(rowMargin + ((item % rows) * (2 * rowMargin + itemHeight)), Style.Unit.PX);

            if (rows == 1 || (item > 0 && item % rows == 0)) {
                left += step;
            }
            item++;
        }
    }

    /**
     * Make element transparent
     */
    private void setTransparent(final Element target) {
        final Style targetStyle = target.getStyle();
        targetStyle.setProperty("opacity", "0.3");
        targetStyle.setProperty("filter", "alpha(opacity=30)");
    }

    /**
     * Make element opaque
     */
    private void removeTransparent(final Element target) {
        final Style targetStyle = target.getStyle();
        targetStyle.setProperty("opacity", "1");
        targetStyle.setProperty("filter", "alpha(opacity=100)");
    }
}
