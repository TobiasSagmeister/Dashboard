/*
 * Creator:
 * 13.11.18 14:18 Tobias Sagmeister
 *
 * Maintainer:
 * 13.11.18 14:18 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.server.event;

import ch.sagmeister.vaadin.dashboard.shared.ComponentOptions;
import com.vaadin.ui.Component;

public class ComponentAddedEvent extends ComponentChangedEvent {

    private final ComponentOptions componentOptions;

    public ComponentAddedEvent(Component component, ComponentOptions componentOptions) {
        super(component);
        this.componentOptions = componentOptions;
    }

    public ComponentOptions getComponentOptions() {
        return componentOptions;
    }
}
