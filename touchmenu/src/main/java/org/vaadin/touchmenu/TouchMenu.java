package org.vaadin.touchmenu;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import org.vaadin.touchmenu.client.Direction;
import org.vaadin.touchmenu.client.TouchMenuState;

import java.util.Iterator;
import java.util.List;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class TouchMenu extends AbstractComponentContainer implements HasComponents, TouchMenuButton.ButtonListener {

    List<Component> componentList = Lists.newLinkedList();
    List<TouchMenuListener> listenerList = Lists.newLinkedList();


    public TouchMenu(int rows, int columns) {
        setRows(rows);
        setColumns(columns);
        setWidth("500px");
        setHeight("300px");
    }

    public int getRows() {
        return getState().rows;
    }

    /**
     * Set the amount of rows the buttons should be arranged into.
     */
    public void setRows(int rows) {
        getState().rows = rows;
        markAsDirty();
    }

    public int getColumns() {
        return getState().columns;
    }

    /**
     * Set the amount of columns to be shown.
     */
    public void setColumns(int columns) {
        getState().columns = columns;
        markAsDirty();
    }

    public Direction getDirection() {
        return getState().direction;
    }

    public void setDirection(Direction direction) {
        getState().direction = direction;
    }

    public String getSelected() {
        return getState().selected;
    }

    public void setSelected(String selected) {
        getState().selected = selected;
    }

    public boolean isAnimate() {
        return getState().animate;
    }

    public void setAnimate(boolean animate) {
        getState().animate = animate;
    }

    public boolean isArrowNavigationEnabled() {
        return getState().arrowNavigationEnabled;
    }

    public void setArrowNavigationEnabled(boolean arrowNavigationEnabled) {
        getState().arrowNavigationEnabled = arrowNavigationEnabled;
    }

    @Override
    protected TouchMenuState getState() {
        return (TouchMenuState) super.getState();
    }

    @Override
    public void addComponent(final Component c) {
        if (c instanceof TouchMenuButton) {
            componentList.add(c);
            super.addComponent(c);
            markAsDirty();

            ((TouchMenuButton) c).addButtonClickedListener(this);
        } else {
            throw new ComponentNotSupportedException();
        }
    }

    @Override
    public void removeComponent(final Component c) {
        componentList.remove(c);
        super.removeComponent(c);
        markAsDirty();

        if (c instanceof TouchMenuButton) {
            ((TouchMenuButton) c).removeButtonClickedListener(this);
        }

    }

    @Override
    public void replaceComponent(final Component oldComponent, final Component newComponent) {
        if (!(newComponent instanceof TouchMenuButton)) {
            throw new ComponentNotSupportedException();
        }

        final int index = componentList.indexOf(oldComponent);
        if (index != -1) {
            componentList.remove(index);
            componentList.add(index, newComponent);
            fireComponentDetachEvent(oldComponent);
            fireComponentAttachEvent(newComponent);
            markAsDirty();
        }
    }

    @Override
    public int getComponentCount() {
        return componentList.size();
    }

    @Override
    public Iterator<Component> iterator() {
        return componentList.iterator();
    }

    @Override
    public void buttonClicked(TouchMenuButton button) {
        for (TouchMenuListener listener : listenerList) {
            listener.buttonClicked(button);
        }
    }

    public boolean addTouchMenuListener(TouchMenuListener listener) {
        return listenerList.add(listener);
    }

    public boolean removeTouchMenuListener(TouchMenuListener listener) {
        return listenerList.remove(listener);
    }

    public interface TouchMenuListener {
        void buttonClicked(TouchMenuButton button);
    }
}
