package ch.sagmeister.vaadin.dashboard.client.model.event;

import ch.sagmeister.vaadin.dashboard.shared.ComponentOptions;
import com.google.gwt.user.client.ui.Widget;

public class ComponentAddedEvent extends AbstractModelEvent implements ComponentChangedEvent {

    private final Widget widget;
    private final ComponentOptions componentOptions;

    public ComponentAddedEvent(Widget widget, ComponentOptions componentOptions) {
        this.widget = widget;
        this.componentOptions = componentOptions;
    }

    public Widget getWidget() {
        return widget;
    }

    public ComponentOptions getComponentOptions() {
        return componentOptions;
    }
}