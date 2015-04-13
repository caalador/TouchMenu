package org.vaadin.touchmenu.client;

import com.vaadin.shared.AbstractComponentState;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class TouchMenuState extends AbstractComponentState {

    public int rows;
    public int columns;

    public Direction direction = Direction.IN_FROM_SAME;

    // Button ID
    public String selected;

    public boolean animate = true;
    public boolean arrowNavigationEnabled = true;

}
