package org.vaadin.touchmenu;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import org.vaadin.touchmenu.client.Direction;
import org.vaadin.touchmenu.client.ScrollDirection;
import org.vaadin.touchmenu.client.TouchMenuState;

import java.util.Iterator;
import java.util.List;

/**
 * A visual menu component.
 *
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

    /**
     * Get the current amount of rows to be shown
     *
     * @return Current row amount
     */
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

    /**
     * Get the current amount of columns to be shown.
     *
     * @return Current column amount
     */
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

    /**
     * Get the direction from where the items will come when clicking the arrow button.
     *
     * @return Direction for incoming items for arrow button click
     */
    public Direction getDirection() {
        return getState().direction;
    }

    /**
     * Set the incoming item direction for arrow button click.
     *
     * @param direction Incoming item {@link Direction}
     */
    public void setDirection(Direction direction) {
        getState().direction = direction;
    }

    /**
     * Check if the menu positioning is animated.
     *
     * @return true if positioning is animated.
     */
    public boolean isAnimate() {
        return getState().animate;
    }

    /**
     * Set if item positioning should be animated or just snap into place.
     *
     * @param animate true for smooth animated positioning.
     */
    public void setAnimate(boolean animate) {
        getState().animate = animate;
    }

    /**
     * Navigation arrows enabled
     *
     * @return enabled button navigation.
     */
    public boolean isArrowNavigationEnabled() {
        return getState().arrowNavigationEnabled;
    }

    /**
     * Set arrow navigation enabled true to get the left and right navigation arrows to the menu and false to remove them
     *
     * @param arrowNavigationEnabled show/hide navigation arrows.
     */
    public void setArrowNavigationEnabled(boolean arrowNavigationEnabled) {
        getState().arrowNavigationEnabled = arrowNavigationEnabled;
    }

    /**
     * Set if the component should use given button size for positioning calculations,
     * or automatically use the size of the first button.
     *
     * @param useDefinedButtonSize Use given button size.
     */
    public void setUseDefinedButtonSize(boolean useDefinedButtonSize) {
        getState().useDefinedButtonSize = useDefinedButtonSize;
    }

    /**
     * Set the button size we want to calculate button positioning with.
     * Note! Setting button size will enable using defined button size.
     *
     * @param width  default button width
     * @param height default button height
     */
    public void setButtonSize(int width, int height) {
        getState().buttonWidth = width;
        getState().buttonHeight = height;

        setUseDefinedButtonSize(true);
    }

    public void setScrollDirection(ScrollDirection scrollDirection) {
        getState().scrollDirection = scrollDirection;
    }

    @Override
    protected TouchMenuState getState() {
        return (TouchMenuState) super.getState();
    }

    /**
     * Add component to TouchMenu. Only TouchMenuButton is accepted and any other component will result in a {@link ComponentNotSupportedException}.
     *
     * @param c TouchMenuButton to add to TouchMenu
     * @throws ComponentNotSupportedException Unsupported component
     */
    @Override
    public void addComponent(final Component c) throws ComponentNotSupportedException {
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

    /**
     * Replace old component with new component. Only TouchMenuComponent is supported and
     * all other components will throw a {@link ComponentNotSupportedException}
     *
     * @param oldComponent old component to be replaced
     * @param newComponent new component
     * @throws ComponentNotSupportedException Unsupported component
     */
    @Override
    public void replaceComponent(final Component oldComponent, final Component newComponent) throws ComponentNotSupportedException {
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

    /**
     * Attach a listener to TouchMenu
     *
     * @param listener Listener to attach
     * @return true if listener was added
     */
    public boolean addTouchMenuListener(TouchMenuListener listener) {
        return listenerList.add(listener);
    }

    /**
     * Remove an attached listener from TouchMenu
     *
     * @param listener Listener to remove
     * @return true if this menu contained the specified listener
     */
    public boolean removeTouchMenuListener(TouchMenuListener listener) {
        return listenerList.remove(listener);
    }

    /**
     * Interface for listening to TouchMenu events.
     */
    public interface TouchMenuListener {
        /**
         * Called when a {@link TouchMenuButton} has been clicked. A reference to the
         * {@link TouchMenuButton} is returned.
         *
         * @param button Clicked {@link TouchMenuButton}.
         */
        void buttonClicked(TouchMenuButton button);
    }
}
