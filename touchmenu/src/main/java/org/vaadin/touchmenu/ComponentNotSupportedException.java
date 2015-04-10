package org.vaadin.touchmenu;

/**
 * @author Mikael Grankvist - Vaadin }>
 */
public class ComponentNotSupportedException extends RuntimeException {

    public ComponentNotSupportedException() {
        super("Component not supported");
    }

    public ComponentNotSupportedException(Throwable cause) {
        super("Component not supported", cause);
    }
}
