package org.vaadin.touchmenu.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.VConsole;
import com.vaadin.client.annotations.OnStateChange;
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

    @OnStateChange({"rows", "columns"})
    void updateRowsAndColumns() {
        getWidget().setRows(getState().rows);
        getWidget().setColumns(getState().columns);
    }

    @OnStateChange("arrowNavigationEnabled")
    void setArrowNavigation() {
        getWidget().setUseArrows(getState().arrowNavigationEnabled);
    }

    @OnStateChange("direction")
    void setArrowNavigationDirection() {
        getWidget().setDirection(getState().direction);
    }

    @OnStateChange("animate")
    void setAnimationEnabled() {
        getWidget().setAnimate(getState().animate);
    }

    @OnStateChange("useDefinedButtonSize")
    void setDefinedButtonSizes() {
        getWidget().setDefinedWidth(getState().buttonWidth);
        getWidget().setDefinedHeight(getState().buttonHeight);
        getWidget().setUseDefinedSizes(getState().useDefinedButtonSize);
    }

    @OnStateChange("scrollDirection")
    void setFlowView() {
        if (getWidget().getScrollDirection().equals(getState().scrollDirection)) {
            return;
        }

        VConsole.log(" === scrolldirection");
        getWidget().setFlowView(getState().scrollDirection);

        getWidget().setRows(getState().rows);
        getWidget().setColumns(getState().columns);
        getWidget().setUseArrows(getState().arrowNavigationEnabled);
        getWidget().setDirection(getState().direction);
        getWidget().setAnimate(getState().animate);
        getWidget().setDefinedWidth(getState().buttonWidth);
        getWidget().setDefinedHeight(getState().buttonHeight);
        getWidget().setUseDefinedSizes(getState().useDefinedButtonSize);


        final List<ComponentConnector> children = getChildComponents();
        final TouchMenuWidget widget = getWidget();
        widget.clear();
        for (final ComponentConnector connector : children) {
            widget.add((TouchMenuButtonWidget) connector.getWidget());
        }
        getWidget().layoutWidgets();
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        VConsole.log(" === Adding children");
        final List<ComponentConnector> children = getChildComponents();
        final TouchMenuWidget widget = getWidget();
        widget.clear();
        for (final ComponentConnector connector : children) {
            widget.add((TouchMenuButtonWidget) connector.getWidget());
        }
        widget.layoutWidgets();
    }

    @Override
    public void updateCaption(ComponentConnector componentConnector) {
        // NOOP
    }

    @Override
    public void layout() {
        getWidget().positionElements();
        getWidget().validateColumns();
        getWidget().validateRows();
        getWidget().layoutWidgets();
    }
}
