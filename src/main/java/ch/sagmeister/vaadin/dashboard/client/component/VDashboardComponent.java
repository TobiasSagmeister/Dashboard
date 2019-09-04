/*
 * Creator:
 * 17.05.18 15:44 Tobias Sagmeister
 *
 * Maintainer:
 * 17.05.18 15:44 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component;

import ch.sagmeister.vaadin.dashboard.client.component.util.DashboardUtil;
import ch.sagmeister.vaadin.dashboard.client.component.util.ElementUtil;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

public abstract class VDashboardComponent extends Widget {

    protected static final String DATA_COLUMN_INDEX_ATTR = "data-x";
    protected static final String DATA_ROW_INDEX_ATTR = "data-y";
    protected static final String DATA_COLUMN_SPAN_ATTR = "data-span-x";
    protected static final String DATA_ROW_SPAN_ATTR = "data-span-y";

    private final DashboardModel model;

    private boolean coordinatesLocked;

    private ComponentCoordinates coordinates;
    private ComponentAbsoluteCoordinates absoluteCoordinates;

    public VDashboardComponent(DashboardModel model) {
        this.model = model;
    }

    private double convertToPCTLeft(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= getModel().getColumns()) {
            return -1;
        }

        return (100d / getModel().getColumns()) * columnIndex;
    }

    private int convertToPxTop(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= getModel().getRows()) {
            return -1;
        }

        return rowIndex * getModel().getRowHeight();
    }

    private double convertToPCTWidth(int columnSpan) {
        if (columnSpan > getModel().getColumns()) {
            return -1;
        }

        return (100d / getModel().getColumns()) * columnSpan;
    }

    private int convertToPxHeight(int rowSpan) {
        if (rowSpan > getModel().getRows()) {
            return -1;
        }

        return getModel().getRowHeight() * rowSpan;
    }

    void setCoordinatesLocked(boolean coordinatesLocked) {
        this.coordinatesLocked = coordinatesLocked;
    }

    boolean areCoordinatesLocked() {
        return coordinatesLocked;
    }

    void moveTo(ComponentCoordinates coordinates) {
        if (!coordinatesLocked) {
            this.coordinates = coordinates;
            doMove(coordinates);
        }
    }

    protected void doMove(ComponentCoordinates coordinates) {
        if (DashboardUtil.validateCoordinates(coordinates, getModel())) {
            getElement().setAttribute(DATA_COLUMN_INDEX_ATTR, Integer.toString(coordinates.position.columnIndex));
            getElement().setAttribute(DATA_ROW_INDEX_ATTR, Integer.toString(coordinates.position.rowIndex));
            getElement().setAttribute(DATA_COLUMN_SPAN_ATTR, Integer.toString(coordinates.span.columnSpan));
            getElement().setAttribute(DATA_ROW_SPAN_ATTR, Integer.toString(coordinates.span.rowSpan));

            int pxTop = convertToPxTop(coordinates.position.rowIndex);
            double pctLeft = convertToPCTLeft(coordinates.position.columnIndex);
            double pctWidth = convertToPCTWidth(coordinates.span.columnSpan);
            int pxHeight = convertToPxHeight(coordinates.span.rowSpan);

            moveTo(new ComponentAbsoluteCoordinates(pxTop, pctLeft, pctWidth, pxHeight));

            getElement().getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            getElement().getStyle().setDisplay(Style.Display.NONE);
        }
    }

    public ComponentCoordinates getCoordinates() {
        return coordinates;
    }

    void moveTo(ComponentAbsoluteCoordinates absoluteCoordinates) {
        this.absoluteCoordinates = absoluteCoordinates;
        doAbsoluteMove(absoluteCoordinates);
    }

    public ComponentAbsoluteCoordinates getAbsoluteCoordinates() {
        return absoluteCoordinates;
    }

    protected void doAbsoluteMove(ComponentAbsoluteCoordinates absoluteCoordinates) {
        getElement().getStyle().setTop(absoluteCoordinates.pxTop, Style.Unit.PX);
        getElement().getStyle().setLeft(absoluteCoordinates.pctLeft, Style.Unit.PCT);
        getElement().getStyle().setHeight(absoluteCoordinates.pxHeight, Style.Unit.PX);
        getElement().getStyle().setWidth(absoluteCoordinates.pctWidth, Style.Unit.PCT);
    }

    protected DashboardModel getModel() {
        return model;
    }

    public float getComputedWidth() {
        return ElementUtil.getComputedWidth(getElement());
    }

    public float getComputedHeight() {
        return ElementUtil.getComputedHeight(getElement());
    }
}
