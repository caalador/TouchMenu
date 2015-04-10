package org.vaadin.touchmenu;


import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbstractComponent;
import org.vaadin.touchmenu.client.button.TouchMenuButtonRpc;
import org.vaadin.touchmenu.client.button.TouchMenuButtonState;

import java.util.List;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class TouchMenuButton extends AbstractComponent {

    private final TouchMenuButtonRpc rpc = new TouchMenuButtonRpc() {

        @Override
        public void buttonClicked() {
            for (ButtonListener listener : listeners) {
                listener.buttonClicked(TouchMenuButton.this);
            }
        }
    };

    private List<ButtonListener> listeners = Lists.newLinkedList();

    public TouchMenuButton(String caption) {
        super();
        registerRpc(rpc);
        setCaption(caption);
        setWidth(50, Unit.PIXELS);
        setHeight(50, Unit.PIXELS);
    }

    @Override
    protected TouchMenuButtonState getState() {
        return (TouchMenuButtonState) super.getState();
    }

    /**
     * Add a new buttonClickedListener
     */
    public boolean addButtonClickedListener(ButtonListener listener) {
        return listeners.add(listener);
    }

    /**
     * Remove buttonClickedListener
     */
    public boolean removeButtonClickedListener(ButtonListener listener) {
        return listeners.remove(listener);
    }

    public interface ButtonListener {
        void buttonClicked(TouchMenuButton button);
    }
}
