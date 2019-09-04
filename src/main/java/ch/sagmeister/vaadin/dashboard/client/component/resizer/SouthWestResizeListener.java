/*
 * Creator:
 * 18.06.18 11:15 Tobias Sagmeister
 *
 * Maintainer:
 * 18.06.18 11:15 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component.resizer;

import ch.sagmeister.vaadin.dashboard.client.component.ResizeHandler;
import ch.sagmeister.vaadin.dashboard.client.component.VComponentWrapper;
import ch.sagmeister.vaadin.dashboard.client.component.ComponentAbsoluteCoordinates;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.ResizeData;
import ch.sagmeister.vaadin.dashboard.client.component.util.DashboardUtil;

class SouthWestResizeListener extends AbstractResizeListener {

    public SouthWestResizeListener(VComponentWrapper widgetWrapper, DashboardModel model, ResizeHandler resizeHandler) {
        super(widgetWrapper, model, resizeHandler);
    }

    @Override
    protected ResizeHandler.ResizeCoordinates convertToResizeCoordinates(ResizeData resizeData) {
        VComponentWrapper resizeSource = getResizeSource();

        double pctWidth = resizeData.pctWidth;
        double pctLeft = resizeData.pctLeft;
        double pxHeight = resizeData.pxHeight;

        if (pctLeft < 0 || pctWidth < 0 || pxHeight < 0) {
            return null;
        }

        double columnPCTWidth = DashboardUtil.getDashboardColumnWidthInPct(getModel().getColumns());

        double remainLeft = pctLeft / columnPCTWidth;
        int nextColumnIndex = (int) (Math.round(remainLeft));

        double remainWidth = pctWidth / columnPCTWidth;
        int nextColumnSpan = remainWidth >= 1 ? (int) Math.round(remainWidth) : 0;

        double remainHeight = pxHeight / (getModel().getRowHeight() * 1d);
        int nextRowSpan = remainHeight >= 1 ? (int) Math.round(remainHeight) : 0;

        ComponentCoordinates componentCoordinates = new ComponentCoordinates();
        componentCoordinates.position.rowIndex = resizeSource.getCoordinates().position.rowIndex;
        componentCoordinates.position.columnIndex = nextColumnIndex;
        componentCoordinates.span.columnSpan = nextColumnSpan;
        componentCoordinates.span.rowSpan = nextRowSpan;

        return new ResizeHandler.ResizeCoordinates(componentCoordinates, new ComponentAbsoluteCoordinates(getResizeSource().getAbsoluteCoordinates().pxTop, pctLeft, pctWidth, pxHeight));
    }

}
