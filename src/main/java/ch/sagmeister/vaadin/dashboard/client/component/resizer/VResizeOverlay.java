/*
 * Creator:
 * 22.05.18 14:51 Tobias Sagmeister
 *
 * Maintainer:
 * 22.05.18 14:51 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component.resizer;

import ch.sagmeister.vaadin.dashboard.client.component.ResizeHandler;
import ch.sagmeister.vaadin.dashboard.client.component.VComponentWrapper;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.VAbstractResizeHandle;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.VSouthEastResizeHandle;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.VSouthResizeHandle;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.VSouthWestResizeHandle;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.ResizeListener;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.VEastResizeHandle;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.VWestResizeHandle;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

public class VResizeOverlay extends Widget {

    public static final String STYLE_NAME = "resize-overlay";

    public VResizeOverlay(VComponentWrapper resizeSource, DashboardModel model, ResizeHandler resizeHandler) {
        setElement(Document.get().createDivElement());
        setStyleName(STYLE_NAME);

        addResizeHandle(new VEastResizeHandle(resizeSource, model), new EastResizeListener(resizeSource, model, resizeHandler));
        addResizeHandle(new VSouthResizeHandle(resizeSource, model), new SouthResizeListener(resizeSource, model, resizeHandler));
        addResizeHandle(new VWestResizeHandle(resizeSource, model), new WestResizeListener(resizeSource, model, resizeHandler));
        addResizeHandle(new VSouthWestResizeHandle(resizeSource, model), new SouthWestResizeListener(resizeSource, model, resizeHandler));
        addResizeHandle(new VSouthEastResizeHandle(resizeSource, model), new SouthEastResizeListener(resizeSource, model, resizeHandler));
    }

    private void addResizeHandle(VAbstractResizeHandle resizeHandle, ResizeListener resizeListener) {
        resizeHandle.addResizeListener(resizeListener);
        getElement().appendChild(resizeHandle.getElement());
    }
}
