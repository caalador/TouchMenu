package org.vaadin.touchmenu.client;

import com.vaadin.shared.AbstractComponentState;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class TouchMenuState extends AbstractComponentState {

    public int rows;
    public int columns;

    public Direction direction = Direction.IN_FROM_SAME;

    public boolean animate = true;
    public boolean arrowNavigationEnabled = true;

    public boolean useDefinedButtonSize = false;
    public int buttonWidth, buttonHeight;

}
