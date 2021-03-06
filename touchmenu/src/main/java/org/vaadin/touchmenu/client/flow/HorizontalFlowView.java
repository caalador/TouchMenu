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
public class HorizontalFlowView extends AbstractFlowView {

    public HorizontalFlowView(AbsolutePanel touchView) {
        super(touchView);

        setHeight("100%");

        getElement().setClassName("touch-area");
        getElement().getStyle().setTop(0, Style.Unit.PX);
        getElement().getStyle().setLeft(0, Style.Unit.PX);
        getElement().getStyle().setOverflow(Style.Overflow.VISIBLE);
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

        int columnMargin = (int) Math.ceil((touchViewWidth / columns - itemWidth) / 2);
        int rowMargin = (int) Math.ceil((touchViewHeight / rows - itemHeight) / 2);

        step = 2 * columnMargin + itemWidth;

        int left = columnMargin;

        int item = 0;
        maxValue = 0;

        getElement().getStyle().setLeft(-(firstVisibleColumn * step), Style.Unit.PX);

        // Position buttons into touchArea.
        // No extra positioning needed as we move touchArea instead of the buttons.
        for (Widget button : widgets) {
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
            if (start > end && (start - end) < step) {
                firstVisible++;
            }

            firstVisibleColumn = firstVisible;

            transitionToColumn();
        }
    }

    @Override
    public void transitionToColumn() {
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
        getElement().getStyle().setLeft(-value, Style.Unit.PX);

        navigateLeft.setEnabled(true);
        navigateRight.setEnabled(true);

        if (firstVisibleColumn == 0) {
            transparentFirst();
        } else if (firstVisibleColumn == maxValue) {
            transparentLast();
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
        down = mouseDownEvent.getClientX();
        start = mouseDownEvent.getClientX();
    }

    @Override
    public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
        if (move) {
            int current = getElement().getOffsetLeft();
            current += mouseMoveEvent.getClientX() - down;
            down = mouseMoveEvent.getClientX();

            getElement().getStyle().setLeft(current, Style.Unit.PX);
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

    @Override
    public void onTouchStart(TouchStartEvent touchStartEvent) {
        checkForButtonWidget(touchStartEvent.getNativeEvent());

        removeStyleVersions(getElement().getStyle(), "transition");
        removeStyleVersions(getElement().getStyle(), "transitionProperty");
        touchStartEvent.preventDefault();
        Touch touch = touchStartEvent.getTouches().get(0);
        down = touch.getPageX();
        start = touch.getPageX();

    }

    @Override
    public void onTouchEnd(TouchEndEvent touchEndEvent) {
        move = false;
        Touch touch = touchEndEvent.getTouches().get(0);
        if (touch != null) {
            end = touch.getPageX();
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
        int current = getElement().getOffsetLeft();
        Touch touch = touchMoveEvent.getTouches().get(0);
        if (Math.abs(touch.getPageX() - down) < 5) {
            return;

        }
        dragged = true;

        current += touch.getPageX() - down;
        down = touch.getPageX();

        getElement().getStyle().setLeft(current, Style.Unit.PX);
        if (mouseDownButton != null && !mouseDownButton.isIgnoreClick()) {
            mouseDownButton.ignoreClick(true);
        }
    }
}
