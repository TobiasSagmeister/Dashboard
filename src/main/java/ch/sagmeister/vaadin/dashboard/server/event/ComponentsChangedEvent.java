/*
 * Creator:
 * 16.05.18 12:08 Tobias Sagmeister
 *
 * Maintainer:
 * 16.05.18 12:08 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.server.event;

import ch.sagmeister.vaadin.dashboard.server.Dashboard;
import com.vaadin.ui.Component;

import java.util.Collections;
import java.util.List;

public class ComponentsChangedEvent extends Component.Event {

    private final List<ComponentChangedEvent> events;

    public ComponentsChangedEvent(Dashboard source, List<ComponentChangedEvent> events) {
        super(source);
        this.events = Collections.unmodifiableList(events);
    }

    public List<ComponentChangedEvent> getEvents() {
        return events;
    }
}
