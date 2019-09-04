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

public class VWestResizeHandle extends VAbstractResizeHandle {

    public VWestResizeHandle(VComponentWrapper widgetWrapper, DashboardModel model) {
        super(widgetWrapper, model, Location.W);
    }

    @Override
    protected ResizeData convertToResizeData(ResizeArgs resizeArgs) {
        float oldWidth = DashboardUtil.computeWidgetWidthInPX(getWidgetWrapper().getCoordinates().span.columnSpan, getModel().getColumns(), DashboardUtil.getDashboard(getWidgetWrapper()));
        float newWidth;

        if (resizeArgs.deltaX >= 0) {
            newWidth = oldWidth - resizeArgs.deltaX;
        } else {
            newWidth = oldWidth + Math.abs(resizeArgs.deltaX);
        }

        float dashboardComputedWidth = ElementUtil.getComputedWidth(DashboardUtil.getDashboard(getWidgetWrapper()).getElement());

        float newPCTWidth = newWidth / dashboardComputedWidth * 100f;

        float newLeft = DashboardUtil.computeWidgetLeftInPx(getWidgetWrapper().getCoordinates().position.columnIndex, getModel().getColumns(), DashboardUtil.getDashboard(getWidgetWrapper())) + resizeArgs.deltaX;
        float newLeftPCT = newLeft / dashboardComputedWidth * 100f;

        return new ResizeData.Builder(
                getLocation(),
                ResizeUtil.computeSignByValues(getWidgetWrapper().getAbsoluteCoordinates().pctLeft, newLeftPCT))
                .setPxTop(getWidgetWrapper().getAbsoluteCoordinates().pxTop)
                .setPctLeft(newLeftPCT)
                .setPctWidth(newPCTWidth)
                .setPxHeight(getWidgetWrapper().getAbsoluteCoordinates().pxHeight)
                .build();
    }
}
