/*
 * Creator:
 * 13.11.18 11:48 Tobias Sagmeister
 *
 * Maintainer:
 * 13.11.18 11:48 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.model;

import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import ch.sagmeister.vaadin.dashboard.shared.ComponentOptions;
import com.google.gwt.user.client.ui.Widget;

public final class ComponentModel {

    private final Widget widget;
    private ComponentOptions componentOptions;

    public ComponentModel(Widget widget, ComponentOptions componentOptions) {
        this.widget = widget;
        this.componentOptions = componentOptions;
    }

    // Copy constructor
    public ComponentModel(ComponentModel componentModel) {
        this.widget = componentModel.getWidget();

        this.componentOptions = new ComponentOptions();
        componentOptions.coordinates = new ComponentCoordinates(componentModel.componentOptions.coordinates);
    }

    public Widget getWidget() {
        return widget;
    }

    public ComponentOptions getComponentOptions() {
        return componentOptions;
    }

    public void setComponentOptions(ComponentOptions componentOptions) {
        this.componentOptions = componentOptions;
    }

    @Override
    public String toString() {
        return widget.toString() + ": " + componentOptions.coordinates.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ComponentModel) {
            ComponentModel other = (ComponentModel) obj;
            return other.widget == widget;
        }

        return false;
    }
}
