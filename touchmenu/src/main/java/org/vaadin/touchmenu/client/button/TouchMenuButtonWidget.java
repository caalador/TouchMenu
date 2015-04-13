package org.vaadin.touchmenu.client.button;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.Icon;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class TouchMenuButtonWidget extends Widget implements ClickHandler {

    public static final String CLASSNAME = "touch-menu-button";

    private MenuClickListener listener;

    public TouchMenuButtonWidget() {

        setElement(Document.get().createDivElement());
        getElement().addClassName(CLASSNAME);

        addDomHandler(this, ClickEvent.getType());

        getElement().getStyle().setBackgroundColor("ORANGE");
    }

    public void setListener(MenuClickListener listener) {
        this.listener = listener;
    }

    public void setIcon(Icon icon) {
        getElement().removeAllChildren();
        getElement().appendChild(icon.getElement());
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        VConsole.log("Clicked button");
        clickEvent.stopPropagation();
        if(listener != null){
            listener.buttonClicked();
        }
    }

    public interface MenuClickListener {
        void buttonClicked();
    }
}
