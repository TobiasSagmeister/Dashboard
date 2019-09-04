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

import ch.sagmeister.vaadin.dashboard.client.component.VComponentWrapper;
import ch.sagmeister.vaadin.dashboard.client.component.util.ElementUtil;
import ch.sagmeister.vaadin.dashboard.client.component.util.Location;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.util.ResizeUtil;
import ch.sagmeister.vaadin.dashboard.client.component.util.DashboardUtil;

public class VEastResizeHandle extends VAbstractResizeHandle {

    public VEastResizeHandle(VComponentWrapper widgetWrapper, DashboardModel model) {
        super(widgetWrapper, model, Location.E);
    }

    @Override
    protected ResizeData convertToResizeData(ResizeArgs resizeArgs) {
        float dashboardComputedWidth = ElementUtil.getComputedWidth(DashboardUtil.getDashboard(getWidgetWrapper()).getElement());
        float newWidth = resizeArgs.startWidth + resizeArgs.deltaX;
        float newPCTWidth = newWidth / dashboardComputedWidth * 100f;

        return new ResizeData.Builder(
                getLocation(),
                ResizeUtil.computeSignByValues(getWidgetWrapper().getAbsoluteCoordinates().pctWidth, newPCTWidth))
                .setPxTop(getWidgetWrapper().getAbsoluteCoordinates().pxTop)
                .setPctLeft( getWidgetWrapper().getAbsoluteCoordinates().pctLeft)
                .setPctWidth(newPCTWidth)
                .setPxHeight(getWidgetWrapper().getAbsoluteCoordinates().pxHeight)
                .build();
    }
}
