package org.vaadin.touchmenu.client.flow;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public interface FlowView extends MouseDownHandler, MouseMoveHandler, MouseOutHandler, MouseUpHandler, ClickHandler, TouchStartHandler, TouchMoveHandler, TouchEndHandler {

    void moveEnd();

    void transitionToColumn();

    void layoutWidgets();

    void setColumns(int columns);

    void setRows(int rows);

    void validateRows();

    void validateColumns();
}
