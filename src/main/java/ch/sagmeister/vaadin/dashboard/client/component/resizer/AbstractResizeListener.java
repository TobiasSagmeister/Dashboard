/*
 * Creator:
 * 15.06.18 15:09 Tobias Sagmeister
 *
 * Maintainer:
 * 15.06.18 15:09 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component.resizer;

import ch.sagmeister.vaadin.dashboard.client.component.ResizeHandler;
import ch.sagmeister.vaadin.dashboard.client.component.VComponentWrapper;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.ResizeData;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.ResizeListener;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;

abstract class AbstractResizeListener implements ResizeListener {

    private final VComponentWrapper resizeSource;
    private final DashboardModel model;
    private final ResizeHandler resizeHandler;

    public AbstractResizeListener(VComponentWrapper resizeSource, DashboardModel model, ResizeHandler resizeHandler) {
        this.resizeSource = resizeSource;
        this.model = model;
        this.resizeHandler = resizeHandler;
    }

    protected DashboardModel getModel() {
        return model;
    }

    protected VComponentWrapper getResizeSource() {
        return resizeSource;
    }

    @Override
    public final void onResizeStart() {
        resizeHandler.onResizeStart(resizeSource);
    }

    @Override
    public final void onResize(ResizeData resizeData) {
        ResizeHandler.ResizeCoordinates resizeCoordinates = convertToResizeCoordinates(resizeData);

        if (resizeCoordinates != null) {
            resizeHandler.onResize(resizeCoordinates);
        }
    }

    protected abstract ResizeHandler.ResizeCoordinates convertToResizeCoordinates(ResizeData resizeData);

    @Override
    public final void onResizeEnd() {
        resizeHandler.onResizeEnd();
    }

}
