package ch.sagmeister.vaadin.dashboard.client.model.event;

import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import com.google.gwt.user.client.ui.Widget;

public class ComponentMovedEvent extends AbstractModelEvent implements ComponentChangedEvent {

    private final Widget widget;
    private final ComponentCoordinates oldCoordinates;
    private final ComponentCoordinates coordinates;

    public ComponentMovedEvent(Widget widget, ComponentCoordinates oldCoordinates, ComponentCoordinates coordinates) {
        this.widget = widget;
        this.oldCoordinates = oldCoordinates;
        this.coordinates = coordinates;
    }

    public Widget getWidget() {
        return widget;
    }

    public ComponentCoordinates getCoordinates() {
        return coordinates;
    }

    public ComponentCoordinates getOldCoordinates() {
        return oldCoordinates;
    }
}