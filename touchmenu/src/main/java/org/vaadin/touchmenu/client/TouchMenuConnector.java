package org.vaadin.touchmenu.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.shared.ui.Connect;
import org.vaadin.touchmenu.TouchMenu;
import org.vaadin.touchmenu.client.button.TouchMenuButtonWidget;

import java.util.List;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
@Connect(TouchMenu.class)
public class TouchMenuConnector extends AbstractHasComponentsConnector implements SimpleManagedLayout {


    @Override
    protected Widget createWidget() {
        return GWT.create(TouchMenuWidget.class);
    }

    @Override
    public TouchMenuWidget getWidget() {
        return (TouchMenuWidget) super.getWidget();
    }

    @Override
    public TouchMenuState getState() {
        return (TouchMenuState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().rows = getState().rows;
        getWidget().columns = getState().columns;
        getWidget().layoutWidgets();
    }


    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {

        final List<ComponentConnector> children = getChildComponents();
        final TouchMenuWidget widget = getWidget();
        widget.clear();
        for (final ComponentConnector connector : children) {
            widget.add((TouchMenuButtonWidget) connector.getWidget());
        }
    }

    @Override
    public void updateCaption(ComponentConnector componentConnector) {
        // NOOP
    }

    @Override
    public void layout() {
        getWidget().setViewSize();
        getWidget().validateColumns();
        getWidget().validateRows();
        getWidget().layoutWidgets();
    }
}
