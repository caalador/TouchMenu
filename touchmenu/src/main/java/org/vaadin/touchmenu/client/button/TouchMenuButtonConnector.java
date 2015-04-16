package org.vaadin.touchmenu.client.button;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.Icon;
import com.vaadin.shared.ui.Connect;
import org.vaadin.touchmenu.TouchMenuButton;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
@Connect(TouchMenuButton.class)
public class TouchMenuButtonConnector extends AbstractComponentConnector {

    private final TouchMenuButtonRpc rpc = RpcProxy.create(TouchMenuButtonRpc.class, this);

    @Override
    protected void init() {
        super.init();

        getWidget().setListener(new TouchMenuButtonWidget.MenuClickListener() {
            @Override
            public void buttonClicked() {
                rpc.buttonClicked();
            }
        });
    }

    @Override
    protected TouchMenuButtonWidget createWidget() {
        return GWT.create(TouchMenuButtonWidget.class);
    }

    @Override
    public TouchMenuButtonWidget getWidget() {
        return (TouchMenuButtonWidget) super.getWidget();
    }

    @Override
    public TouchMenuButtonState getState() {
        return (TouchMenuButtonState) super.getState();
    }

    @OnStateChange("caption")
    void setCaption() {
        getWidget().setCaption(getState().caption);
    }

    @OnStateChange("resources")
    void onResourceChange() {
        Icon icon = getIcon();
        if (icon != null) {
            getWidget().setIcon(icon);
        }
    }
}
