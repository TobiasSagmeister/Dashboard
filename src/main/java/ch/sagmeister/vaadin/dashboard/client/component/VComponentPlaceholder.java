/*
 * Creator:
 * 21.11.18 10:41 Tobias Sagmeister
 *
 * Maintainer:
 * 21.11.18 10:41 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component;

import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;

class VComponentPlaceholder extends VDashboardComponent {

    private static final String STYLE_PLACEHOLDER = VDashboard.STYLE_DASHBOARD + "-placeholder";

    VComponentPlaceholder(DashboardModel dashboardModel) {
        super(dashboardModel);
        init();
    }

    private void init() {
        setElement(Document.get().createDivElement());
        setStyleName(STYLE_PLACEHOLDER);

        display(false);
    }

    void display(boolean display) {
        if (display) {
            getElement().getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            getElement().getStyle().setDisplay(Style.Display.NONE);
        }
    }
}
