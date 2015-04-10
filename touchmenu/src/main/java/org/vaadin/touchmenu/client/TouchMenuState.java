package org.vaadin.touchmenu.client;

import com.vaadin.shared.AbstractComponentState;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class TouchMenuState extends AbstractComponentState {

    public int rows;
    public int columns;

    public Direction direction;

    // Button ID
    public String selected;

    public boolean animate;
    public boolean arrowNavigationEnabled;

}
