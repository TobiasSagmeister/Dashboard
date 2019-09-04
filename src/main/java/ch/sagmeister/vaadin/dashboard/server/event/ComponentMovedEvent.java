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

import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import com.vaadin.ui.Component;

public class ComponentMovedEvent extends ComponentChangedEvent {

    private final ComponentCoordinates oldCoordinates;
    private final ComponentCoordinates coordinates;

    public ComponentMovedEvent(Component component, ComponentCoordinates oldCoordinates, ComponentCoordinates coordinates) {
        super(component);
        this.oldCoordinates = oldCoordinates;
        this.coordinates = coordinates;
    }

    public ComponentCoordinates getOldCoordinates() {
        return oldCoordinates;
    }

    public ComponentCoordinates getCoordinates() {
        return coordinates;
    }
}
