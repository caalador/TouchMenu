package org.vaadin.touchmenu.client.button;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public interface TouchMenuButtonRpc extends ServerRpc {

    /**
     * Touch menu button click.
     */
    void buttonClicked();
}
