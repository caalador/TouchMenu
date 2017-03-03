package org.vaadin.touchmenu.client;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ui.AbstractComponentContainerState;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class TouchMenuState extends AbstractComponentContainerState {

    public int rows;
    public int columns;

    public Direction direction = Direction.IN_FROM_SAME;
    public ScrollDirection scrollDirection = ScrollDirection.HORIZONTAL;

    public boolean animate = true;
    public boolean arrowNavigationEnabled = true;

    public boolean useDefinedButtonSize = false;
    public int buttonWidth, buttonHeight;

}
