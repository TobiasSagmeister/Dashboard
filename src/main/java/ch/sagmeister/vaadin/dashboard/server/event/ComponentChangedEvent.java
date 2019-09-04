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

import com.vaadin.ui.Component;

public class ComponentChangedEvent extends Component.Event {

    public ComponentChangedEvent(Component source) {
        super(source);
    }
}
