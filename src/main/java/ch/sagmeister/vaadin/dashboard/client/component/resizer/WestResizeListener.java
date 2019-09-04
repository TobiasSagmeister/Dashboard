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

import ch.sagmeister.vaadin.dashboard.client.component.ResizeHandler;
import ch.sagmeister.vaadin.dashboard.client.component.VComponentWrapper;
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import ch.sagmeister.vaadin.dashboard.client.component.ComponentAbsoluteCoordinates;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.ResizeData;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.client.component.util.DashboardUtil;

class WestResizeListener extends AbstractResizeListener {

    public WestResizeListener(VComponentWrapper widgetWrapper, DashboardModel model, ResizeHandler resizeHandler) {
        super(widgetWrapper, model, resizeHandler);
    }

    @Override
    protected ResizeHandler.ResizeCoordinates convertToResizeCoordinates(ResizeData resizeData) {
        double pctWidth = resizeData.pctWidth;
        double pctLeft = resizeData.pctLeft;

        if (pctLeft < 0 || pctWidth < 0) {
            return null;
        }

        double columnPCTWidth = DashboardUtil.getDashboardColumnWidthInPct(getModel().getColumns());

        double remainLeft = pctLeft / columnPCTWidth;
        int nextColumnIndex = (int) (Math.round(remainLeft));

        double remainWidth = pctWidth / columnPCTWidth;
        int nextColumnSpan = remainWidth >= 1 ? (int) Math.round(remainWidth) : 0;

        ComponentCoordinates componentCoordinates = new ComponentCoordinates();
        componentCoordinates.position.rowIndex = getResizeSource().getCoordinates().position.rowIndex;
        componentCoordinates.position.columnIndex = nextColumnIndex;
        componentCoordinates.span.columnSpan = nextColumnSpan;
        componentCoordinates.span.rowSpan = getResizeSource().getCoordinates().span.rowSpan;

        return new ResizeHandler.ResizeCoordinates(componentCoordinates, new ComponentAbsoluteCoordinates(getResizeSource().getAbsoluteCoordinates().pxTop, pctLeft, pctWidth, getResizeSource().getAbsoluteCoordinates().pxHeight));
    }

}
