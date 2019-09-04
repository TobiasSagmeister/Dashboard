/*
 * Creator:
 * 13.11.18 10:18 Tobias Sagmeister
 *
 * Maintainer:
 * 13.11.18 10:18 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component;

import ch.sagmeister.vaadin.dashboard.client.model.event.ModelChangedEvent;

import java.util.List;

@FunctionalInterface
public interface DashboardChangedListener {

    void onDashboardChanged(DashboardChangedEvent event);

    final class DashboardChangedEvent {

        private final List<ModelChangedEvent> modelChangedEvents;

        public DashboardChangedEvent(List<ModelChangedEvent> modelChangedEvents) {
            this.modelChangedEvents = modelChangedEvents;
        }

        public List<ModelChangedEvent> getModelChangedEvents() {
            return modelChangedEvents;
        }
    }
}
