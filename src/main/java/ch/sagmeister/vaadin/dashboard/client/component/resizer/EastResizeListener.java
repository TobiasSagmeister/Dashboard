/*
 * Creator:
 * 15.06.18 15:08 Tobias Sagmeister
 *
 * Maintainer:
 * 15.06.18 15:08 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component.resizer;

import ch.sagmeister.vaadin.dashboard.client.component.ComponentAbsoluteCoordinates;
import ch.sagmeister.vaadin.dashboard.client.component.ResizeHandler;
import ch.sagmeister.vaadin.dashboard.client.component.VComponentWrapper;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.ResizeData;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.client.component.util.DashboardUtil;
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;

class EastResizeListener extends AbstractResizeListener {

    public EastResizeListener(VComponentWrapper widgetWrapper, DashboardModel model, ResizeHandler resizeHandler) {
        super(widgetWrapper, model, resizeHandler);
    }

    @Override
    protected ResizeHandler.ResizeCoordinates convertToResizeCoordinates(ResizeData resizeData) {
        VComponentWrapper resizeSource = getResizeSource();

        double pctWidth = resizeData.pctWidth;
        if (pctWidth < 0) {
            return null;
        }

        double columnPCTWidth = DashboardUtil.getDashboardColumnWidthInPct(getModel().getColumns());
        double remainWidth = pctWidth / columnPCTWidth;
        int nextColumnSpan = remainWidth >= 1 ? (int) Math.round(remainWidth) : 0;
        if ((resizeSource.getCoordinates().position.columnIndex + remainWidth) > getModel().getColumns()) {
            nextColumnSpan = (int) Math.ceil(remainWidth);
        }

        ComponentCoordinates componentCoordinates = new ComponentCoordinates();
        componentCoordinates.position.rowIndex = resizeSource.getCoordinates().position.rowIndex;
        componentCoordinates.position.columnIndex = resizeSource.getCoordinates().position.columnIndex;
        componentCoordinates.span.columnSpan = nextColumnSpan;
        componentCoordinates.span.rowSpan = resizeSource.getCoordinates().span.rowSpan;

        return new ResizeHandler.ResizeCoordinates(componentCoordinates, new ComponentAbsoluteCoordinates(getResizeSource().getAbsoluteCoordinates().pxTop, getResizeSource().getAbsoluteCoordinates().pctLeft, pctWidth, getResizeSource().getAbsoluteCoordinates().pxHeight));
    }
}
