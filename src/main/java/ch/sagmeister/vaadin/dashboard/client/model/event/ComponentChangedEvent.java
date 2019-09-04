package ch.sagmeister.vaadin.dashboard.client.model.event;

import com.google.gwt.user.client.ui.Widget;

public interface ComponentChangedEvent extends ModelChangedEvent {

    Widget getWidget();
}
