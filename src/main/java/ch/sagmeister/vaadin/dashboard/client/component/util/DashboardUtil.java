/*
 * Creator:
 * 11.10.18 13:52 Tobias Sagmeister
 *
 * Maintainer:
 * 11.10.18 13:52 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component.util;

import ch.sagmeister.vaadin.dashboard.client.component.VDashboard;
import ch.sagmeister.vaadin.dashboard.client.component.VComponentWrapper;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.WidgetUtil;

public class DashboardUtil {

    public static VComponentWrapper findComponentWrapper(Widget widget) {
        Element wrapperElement = widget.getElement().getParentElement().getParentElement();
        return WidgetUtil.findWidget(wrapperElement, VComponentWrapper.class);
    }

    public static boolean validateCoordinates(ComponentCoordinates coordinates, DashboardModel model) {
        int colIndex = coordinates.position.columnIndex;
        int rowIndex = coordinates.position.rowIndex;
        int colSpan = coordinates.span.columnSpan;
        int rowSpan = coordinates.span.rowSpan;

        return colIndex >= 0 && (colIndex + colSpan) <= model.getColumns() && rowIndex >= 0 && colSpan > 0 && rowSpan > 0;
    }

    public static double getDashboardColumnWidthInPct(int dashboardColumns) {
        return 100d / dashboardColumns;
    }

    public static int getDashboardColumnWidthInPx(Widget dashboard, int dashboardColumns) {
        return (int) (ElementUtil.getComputedWidth(dashboard.getElement()) / dashboardColumns);
    }

    public static VDashboard getDashboard(Widget widget) {
        Widget w = widget;
        while ((w = w.getParent()) != null) {
            if (w instanceof VDashboard) {
                return (VDashboard) w;
            }
        }

        return null;
    }

    public static int computeWidgetHeightInPX(int rowSpan, int rowHeight) {
        return rowHeight * rowSpan;
    }

    public static float computeWidgetWidthInPX(int columnSpan, int dashboardColumns, Widget dashboard) {
        return ElementUtil.getComputedWidth(dashboard.getElement()) / dashboardColumns * columnSpan;
    }

    public static float computeWidgetWidthInPCT(int columnSpan, int dashboardColumns) {
        return 100f / dashboardColumns * columnSpan;
    }

    public static float computeWidgetLeftInPx(int columnIndex, int dashboardColumns, Widget dashboard) {
        return ElementUtil.getComputedWidth(dashboard.getElement()) / dashboardColumns * columnIndex;
    }

    public static float computeWidgetLeftInPCT(int columnIndex, int dashboardColumns) {
        return 100f / dashboardColumns * columnIndex;
    }

    public static int computeWidgetTopInPx(int rowIndex, int rowHeight) {
        return rowIndex * rowHeight;
    }
}
