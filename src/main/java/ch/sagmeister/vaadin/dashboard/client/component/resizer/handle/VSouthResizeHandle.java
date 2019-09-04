/*
 * Creator:
 * 22.05.18 15:49 Tobias Sagmeister
 *
 * Maintainer:
 * 22.05.18 15:49 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component.resizer.handle;

import ch.sagmeister.vaadin.dashboard.client.component.util.Location;
import ch.sagmeister.vaadin.dashboard.client.component.VComponentWrapper;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.util.ResizeUtil;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.client.component.util.DashboardUtil;

public class VSouthResizeHandle extends VAbstractResizeHandle {

    public VSouthResizeHandle(VComponentWrapper widgetWrapper, DashboardModel model) {
        super(widgetWrapper, model, Location.S);
    }

    @Override
    protected ResizeData convertToResizeData(ResizeArgs resizeArgs) {
        int oldHeight = DashboardUtil.computeWidgetHeightInPX(getWidgetWrapper().getCoordinates().span.rowSpan, getModel().getRowHeight());
        int newHeight = oldHeight + resizeArgs.deltaY;

        return new ResizeData.Builder(
                getLocation(),
                ResizeUtil.computeSignByValues(getWidgetWrapper().getAbsoluteCoordinates().pxHeight, newHeight))
                .setPxTop(getWidgetWrapper().getAbsoluteCoordinates().pxTop)
                .setPctLeft(getWidgetWrapper().getAbsoluteCoordinates().pctLeft)
                .setPctWidth(getWidgetWrapper().getAbsoluteCoordinates().pctWidth)
                .setPxHeight(newHeight)
                .build();
    }

}