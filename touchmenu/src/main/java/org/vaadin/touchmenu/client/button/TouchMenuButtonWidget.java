package org.vaadin.touchmenu.client.button;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.Icon;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class TouchMenuButtonWidget extends Widget implements ClickHandler, TouchEndHandler {

    public static final String CLASSNAME = "touch-menu-button";

    private MenuClickListener listener;
    private boolean ignoreClick = false;
    private String caption = "";
    private Element captionLabel;

    public TouchMenuButtonWidget() {

        setElement(Document.get().createDivElement());
        getElement().addClassName(CLASSNAME);

        captionLabel = Document.get().createDivElement();
        captionLabel.setClassName(CLASSNAME + "-caption");
        captionLabel.setInnerText(caption);

        getElement().appendChild(captionLabel);

        addDomHandler(this, ClickEvent.getType());
        if (TouchEvent.isSupported()) {
            addDomHandler(this, TouchEndEvent.getType());
        }
    }

    protected void setListener(MenuClickListener listener) {
        this.listener = listener;
    }

    public void setIcon(Icon icon) {
        getElement().removeAllChildren();
        getElement().appendChild(captionLabel);
        getElement().appendChild(icon.getElement());
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        if (ignoreClick) {
            return;
        }

        clickEvent.stopPropagation();
        if (listener != null) {
            listener.buttonClicked();
        }
    }

    @Override
    public void onTouchEnd(TouchEndEvent touchEndEvent) {
        if (ignoreClick) {
            return;
        }

        touchEndEvent.stopPropagation();
        if (listener != null) {
            listener.buttonClicked();
        }
    }

    public void setCaption(String caption) {
        this.caption = caption;
        captionLabel.setInnerText(caption);
    }

    protected interface MenuClickListener {
        void buttonClicked();
    }

    public void ignoreClick(boolean ignoreClick) {
        this.ignoreClick = ignoreClick;
    }

    public boolean isIgnoreClick() {
        return ignoreClick;
    }
}
