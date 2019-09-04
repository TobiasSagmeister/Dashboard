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
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.ResizeData;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;

class SouthResizeListener extends AbstractResizeListener {

    public SouthResizeListener(VComponentWrapper widgetWrapper, DashboardModel model, ResizeHandler resizeHandler) {
        super(widgetWrapper, model, resizeHandler);
    }

    @Override
    protected ResizeHandler.ResizeCoordinates convertToResizeCoordinates(ResizeData resizeData) {
        VComponentWrapper resizeSource = getResizeSource();

        double pxHeight = resizeData.pxHeight;
        if (pxHeight < 0) {
            return null;
        }

        double remainHeight = pxHeight / (getModel().getRowHeight() * 1d);
        int nextRowSpan = remainHeight >= 1 ? (int) Math.round(remainHeight) : 0;

        ComponentCoordinates componentCoordinates = new ComponentCoordinates();
        componentCoordinates.position.rowIndex = resizeSource.getCoordinates().position.rowIndex;
        componentCoordinates.position.columnIndex = resizeSource.getCoordinates().position.columnIndex;
        componentCoordinates.span.columnSpan = resizeSource.getCoordinates().span.columnSpan;
        componentCoordinates.span.rowSpan = nextRowSpan;

        return new ResizeHandler.ResizeCoordinates(componentCoordinates, new ComponentAbsoluteCoordinates(getResizeSource().getAbsoluteCoordinates().pxTop, getResizeSource().getAbsoluteCoordinates().pctLeft, getResizeSource().getAbsoluteCoordinates().pctWidth, pxHeight));
    }

}
