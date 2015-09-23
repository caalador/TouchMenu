package org.vaadin.touchmenu.client.flow;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class FreeFlowView extends AbstractFlowView {

    private Point down;
    private Point start;
    private Point end;

    private int firstVisibleRow = 0;
    private int stepY;

    public FreeFlowView(AbsolutePanel touchView) {
        super(touchView);

        setHeight("100%");

        getElement().setClassName("touch-area");
        getElement().getStyle().setTop(0, Style.Unit.PX);
        getElement().getStyle().setLeft(0, Style.Unit.PX);
        getElement().getStyle().setOverflow(Style.Overflow.VISIBLE);
    }

    @Override
    public void transitionToColumn() {
        if (firstVisibleColumn < 0) {
            firstVisibleColumn = 0;
        } else if (firstVisibleColumn > maxValue) {
            firstVisibleColumn = maxValue;
        }

        setTransitionToArea();

        int valueX = firstVisibleColumn * step;

        if (valueX > (endValue - touchView.getOffsetWidth())) {
            valueX = (endValue - touchView.getOffsetWidth());
        }

        if (firstVisibleRow < 0) {
            firstVisibleRow = 0;
        } else if (firstVisibleRow > maxValue - rows + 1) {
            firstVisibleRow = maxValue - rows + 1;
        }

        int valueY = firstVisibleRow * stepY;

        if (valueY > (endValue - touchView.getOffsetHeight())) {
            valueY = (endValue - touchView.getOffsetHeight());
        }

        getElement().getStyle().setLeft(-valueX, Style.Unit.PX);
        getElement().getStyle().setTop(-valueY, Style.Unit.PX);

    }

    @Override
    public void layoutWidgets() {

        int touchViewWidth = touchView.getElement().getClientWidth();
        int touchViewHeight = touchView.getElement().getClientHeight();

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

        int sideSize = getSideSize();

        int columnMargin = (int) Math.ceil((touchViewWidth / columns - itemWidth) / 2);
        int rowMargin = (int) Math.ceil((touchViewHeight / rows - itemHeight) / 2);

        step = 2 * columnMargin + itemWidth;
        stepY = 2 * rowMargin + itemHeight;

        int left = columnMargin;

        int item = 0;
        maxValue = 0;

        getElement().getStyle().setLeft(-(firstVisibleColumn * step), Style.Unit.PX);

        // Position buttons into touchArea.
        // No extra positioning needed as we move touchArea instead of the buttons.
        for (Widget button : widgets) {
            if (item > 0 && item % sideSize == 0) {
                left += step;
                maxValue++;
            }
            int buttonLeft = left;
            int buttonTop = rowMargin + ((item % sideSize) * (2 * rowMargin + itemHeight));

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
//        maxValue -= sideSize - 1;
    }


    @Override
    public void moveEnd() {
        dragged = false;

        if (getElement().getOffsetLeft() > 0) {
            firstVisibleColumn = 0;
            transitionToColumn();
        } else if (getElement().getOffsetLeft() < -(endValue - touchView.getOffsetWidth())) {
            firstVisibleColumn = maxValue;
            transitionToColumn();
        } else {
            int firstVisible = Math.abs(touchView.getWidgetLeft(this) / step);

            // scroll forward column if moved "forward" a bit but not over one column
            if (start.getX() > end.getX() && (start.getX() - end.getX()) < step + (step / 3)) {
                firstVisible++;
            }

            firstVisibleColumn = firstVisible;

            transitionToColumn();
        }

        if (getElement().getOffsetTop() > 0) {
            firstVisibleRow = 0;
            transitionToColumn();
        } else if (getElement().getOffsetTop() < -(endValue - touchView.getElement().getOffsetTop())) {
            firstVisibleRow = maxValue - rows + 1;
            transitionToColumn();
        } else {
            int firstVisible = Math.abs(touchView.getWidgetTop(this) / stepY);

            // scroll forward column if moved "forward" a bit but not over one column
            if (start.getY() > end.getY() && (start.getY() - end.getY()) < stepY + (stepY / 3)) {
                firstVisible++;
            }

            firstVisibleRow = firstVisible;

            transitionToColumn();
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        if (mouseDownEvent.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
            return;
        }
        checkForButtonWidget(mouseDownEvent.getNativeEvent());

        removeStyleVersions(getElement().getStyle(), "transition");
        removeStyleVersions(getElement().getStyle(), "transitionProperty");
        mouseDownEvent.preventDefault();
        move = true;
        down = new Point(mouseDownEvent.getClientX(), mouseDownEvent.getClientY());
        start = new Point(mouseDownEvent.getClientX(), mouseDownEvent.getClientY());
    }

    @Override
    public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
        if (move) {
            int currentX = getElement().getOffsetLeft();
            currentX += mouseMoveEvent.getClientX() - down.getX();
            int currentY = getElement().getOffsetTop();
            currentY += mouseMoveEvent.getClientY() - down.getY();

            down = new Point(mouseMoveEvent.getClientX(), mouseMoveEvent.getClientY());

            getElement().getStyle().setLeft(currentX, Style.Unit.PX);
            getElement().getStyle().setTop(currentY, Style.Unit.PX);
            if (mouseDownButton != null && !mouseDownButton.isIgnoreClick()) {
                mouseDownButton.ignoreClick(true);
            }
            dragged = true;
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent mouseUpEvent) {
        move = false;
        end = new Point(mouseUpEvent.getClientX(), mouseUpEvent.getClientY());
        if (dragged)
            moveEnd();
    }

    @Override
    public void onMouseOut(MouseOutEvent mouseOutEvent) {
        move = false;
        end = new Point(mouseOutEvent.getClientX(), mouseOutEvent.getClientY());
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

    @Override
    public void onTouchStart(TouchStartEvent touchStartEvent) {
        checkForButtonWidget(touchStartEvent.getNativeEvent());

        removeStyleVersions(getElement().getStyle(), "transition");
        removeStyleVersions(getElement().getStyle(), "transitionProperty");
        touchStartEvent.preventDefault();
        Touch touch = touchStartEvent.getTouches().get(0);

        down = new Point(touch.getPageX(), touch.getPageY());
        start = new Point(touch.getPageX(), touch.getPageY());

    }

    @Override
    public void onTouchEnd(TouchEndEvent touchEndEvent) {
        move = false;
        Touch touch = touchEndEvent.getTouches().get(0);
        if (touch != null) {
            end = new Point(touch.getPageX(), touch.getPageY());
        }
        if (mouseDownButton != null && mouseDownButton.isIgnoreClick()) {
            touchEndEvent.stopPropagation();
            mouseDownButton.ignoreClick(false);
            mouseDownButton = null;
        }
        if (dragged) {
            touchEndEvent.preventDefault();
            moveEnd();
        }
    }

    @Override
    public void onTouchMove(TouchMoveEvent touchMoveEvent) {
        int currentX = getElement().getOffsetLeft();
        int currentY = getElement().getOffsetTop();
        Touch touch = touchMoveEvent.getTouches().get(0);
        if (Math.abs(touch.getPageX() - down.getX()) < 5) {
            return;

        }
        dragged = true;

        currentX += touch.getPageX() - down.getX();
        currentY += touch.getPageY() - down.getY();
        down = new Point(touch.getPageX(), touch.getPageY());

        getElement().getStyle().setLeft(currentX, Style.Unit.PX);
        getElement().getStyle().setTop(currentY, Style.Unit.PX);
        if (mouseDownButton != null && !mouseDownButton.isIgnoreClick()) {
            mouseDownButton.ignoreClick(true);
        }
    }

    public int getSideSize() {
        int sideSize = 0;
        do {
            sideSize++;
        } while (sideSize * sideSize < widgets.size());

        return sideSize;
    }

    private class Point {
        int x = 0;
        int y = 0;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
