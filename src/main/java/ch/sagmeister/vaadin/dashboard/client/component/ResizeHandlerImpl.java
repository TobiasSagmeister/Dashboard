/*
 * Creator:
 * 07.11.18 10:07 Tobias Sagmeister
 *
 * Maintainer:
 * 07.11.18 10:07 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component;

import ch.sagmeister.vaadin.dashboard.client.component.util.DashboardUtil;
import ch.sagmeister.vaadin.dashboard.client.model.ComponentModel;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import ch.sagmeister.vaadin.dashboard.shared.ComponentOptions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

class ResizeHandlerImpl implements ResizeHandler {

    private final Logger LOG = Logger.getLogger(ResizeHandlerImpl.class.getName());

    private final DashboardModel model;
    private final VComponentPlaceholder placeholder;

    private boolean onResizing;
    private VComponentWrapper resizeSource;

    private int resizeColumnSpan;
    private int resizeRowSpan;

    private Set<ResizeListener> resizeListeners = new HashSet<>();

    public ResizeHandlerImpl(DashboardModel model, VComponentPlaceholder placeHolder) {
        this.model = model;
        this.placeholder = placeHolder;
        reset();
    }

    private void fireResizedEvent() {
        for (ResizeListener listener : resizeListeners) {
            listener.onResized();
        }
    }

    @Override
    public void addResizeListener(ResizeListener listener) {
        resizeListeners.add(listener);
    }

    @Override
    public void onResizeStart(VComponentWrapper resizeSource) {
        if (!onResizing) {
            placeholder.display(true);

            this.onResizing = true;
            this.resizeSource = resizeSource;
            resizeSource.setCoordinatesLocked(true);

            setWidgetsActive(false);
        } else {
            LOG.severe("ResizeHandler is still on resizing");
        }
    }

    private void setWidgetsActive(boolean active) {
        for (ComponentModel componentModel : model.getComponents()) {
            VComponentWrapper widgetWrapper = DashboardUtil.findComponentWrapper(componentModel.getWidget());

            if (widgetWrapper == resizeSource) {
                continue;
            }

            widgetWrapper.setActive(active);
        }
    }

    @Override
    public void onResize(ResizeCoordinates resizeData) {
        if (onResizing) {
            ComponentCoordinates coordinates = resizeData.getCoordinates();

            if (coordinatesChanged(coordinates)) {
                handle(coordinates);
            }

            ComponentAbsoluteCoordinates absoluteCoordinates = resizeData.getAbsoluteCoordinates();

            if (!model.intersects(coordinates, Collections.singleton(resizeSource.getWidget()))){
                if (DashboardUtil.validateCoordinates(coordinates, model)) {
                    if (coordinates.span.columnSpan >= 1 && coordinates.span.rowSpan >= 1) {
                        resizeSource.moveTo(absoluteCoordinates);
                        fireResizedEvent();
                    }
                }
            }
        } else {
            LOG.severe("ResizeHandler is not on resizing");
        }
    }

    private void handle(ComponentCoordinates coordinates) {
        if (DashboardUtil.validateCoordinates(coordinates, model) && coordinates.span.columnSpan >= 1 && coordinates.span.rowSpan >= 1) {
            model.moveComponent(resizeSource.getWidget(), coordinates);

            ComponentCoordinates resizeSourceCoordinates = model.getComponentOptions(resizeSource.getWidget()).coordinates;
            placeholder.moveTo(resizeSourceCoordinates);
        }

        this.resizeColumnSpan = coordinates.span.columnSpan;
        this.resizeRowSpan = coordinates.span.rowSpan;
    }

    private boolean coordinatesChanged(ComponentCoordinates coordinates) {
        int columnSpan = coordinates.span.columnSpan;
        int rowSpan = coordinates.span.rowSpan;

        return this.resizeColumnSpan != columnSpan || this.resizeRowSpan != rowSpan;
    }

    @Override
    public void onResizeEnd() {
        if (onResizing) {
            placeholder.display(false);

            resizeSource.setCoordinatesLocked(false);
            ComponentOptions componentOptions = model.getComponentOptions(resizeSource.getWidget());
            resizeSource.setWidgetOptions(componentOptions);

            setWidgetsActive(true);

            model.commit(true);

            reset();

            fireResizedEvent();
        } else {
            LOG.severe("ResizeHandler is not on resizing");
        }
    }

    private void reset() {
        this.onResizing = false;
        this.resizeSource = null;
        this.resizeColumnSpan = -1;
        this.resizeRowSpan = -1;
    }
}
