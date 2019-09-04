package ch.sagmeister.vaadin.dashboard.client.model.event;

import com.google.gwt.user.client.ui.Widget;

public class ComponentRemovedEvent extends AbstractModelEvent implements ComponentChangedEvent {

    private final Widget widget;

    public ComponentRemovedEvent(Widget widget) {
        this.widget = widget;
    }

    public Widget getWidget() {
        return widget;
    }
}