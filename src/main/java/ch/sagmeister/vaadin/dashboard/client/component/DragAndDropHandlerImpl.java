package ch.sagmeister.vaadin.dashboard.client.component;

import ch.sagmeister.vaadin.dashboard.client.component.util.DashboardUtil;
import ch.sagmeister.vaadin.dashboard.client.component.util.ElementUtil;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import ch.sagmeister.vaadin.dashboard.shared.ComponentOptions;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.WidgetUtil;
import elemental.events.Event;

import java.util.logging.Logger;

class DragAndDropHandlerImpl implements DragAndDropHandler {

    private static final Logger LOG = Logger.getLogger(DragAndDropHandlerImpl.class.getName());

    private final DashboardModel dashboardModel;
    private final VComponentPlaceholder placeHolder;

    private boolean dragging;
    private int dragColumnIndex;
    private int dragRowIndex;

    public DragAndDropHandlerImpl(DashboardModel dashboardModel, VComponentPlaceholder placeHolder) {
        this.dashboardModel = dashboardModel;
        this.placeHolder = placeHolder;
    }

    @Override
    public void onDragStart(Event event) {
        DragHandler.dragStart(event);
        dashboardModel.removeComponent(DragHandler.getDragSource().getWidget());
        this.dragging = true;
    }

    @Override
    public void onDragEnter(Event event) {
        // Ignore for now
    }

    @Override
    public void onDragOver(Event event) {
        if (DragHandler.isDragging()) {
            ComponentCoordinates coordinates = getCoordinatesFromEvent(event);
            if (DashboardUtil.validateCoordinates(coordinates, dashboardModel)) {
                if (coordinates.position.columnIndex != dragColumnIndex || coordinates.position.rowIndex != dragRowIndex) {
                    dashboardModel.rollback();

                    if (dragging) {
                        dashboardModel.removeComponent(DragHandler.getDragSource().getWidget());
                    }

                    dashboardModel.addComponent(placeHolder, ComponentOptions.of(coordinates));

                    this.dragColumnIndex = coordinates.position.columnIndex;
                    this.dragRowIndex = coordinates.position.rowIndex;
                }
            } else {
                this.dragColumnIndex = -1;
                this.dragRowIndex = -1;
            }
        } else {
            LOG.severe("No active drag source");
        }
    }

    private ComponentCoordinates getCoordinatesFromEvent(Event event) {
        Element target = (Element) event.getTarget();
        NativeEvent nativeEvent = (NativeEvent) event;

        VDashboard dashboard = DashboardUtil.getDashboard(WidgetUtil.findWidget(target));
        if(dashboard == null) {
            throw new IllegalStateException("Parent must be instance of dashboard");
        }

        VComponentWrapper dragSource = DragHandler.getDragSource();
        ComponentCoordinates dragSourceCoordinates = dragSource.getCoordinates();
        VDashboard originDashboard = WidgetUtil.findWidget(dragSource.getElement(), VDashboard.class);
        int originColumnWidthInPx = (int) (ElementUtil.getComputedWidth(originDashboard.getElement()) / originDashboard.getColumns());
        float dragSourceWidthInPx = dragSourceCoordinates.span.columnSpan * originColumnWidthInPx;
        float xRatio = DragHandler.getRelativeX() / dragSourceWidthInPx;

        float originColumnSpan = dragSourceCoordinates.span.columnSpan;
        float originColumns = originDashboard.getColumns();
        float originColumnRatio = originColumnSpan / originColumns;
        int columnSpan = Math.max(Math.round(dashboardModel.getColumns() * originColumnRatio), 1);

        float columnWidthInPx = (ElementUtil.getComputedWidth(dashboard.getElement()) / dashboardModel.getColumns());
        float cursorX = columnSpan * columnWidthInPx * xRatio;

        int relativeX = WidgetUtil.getRelativeX(target, nativeEvent);
        float x = relativeX - cursorX;
        float y = WidgetUtil.getRelativeY(target, nativeEvent) - DragHandler.getRelativeY();

        float colX = x / columnWidthInPx;
        int columnIndex = Math.round(colX);

        float rowY = y / dashboardModel.getRowHeight();
        int rowIndex = Math.round(rowY);

        int rowSpan = dragSourceCoordinates.span.rowSpan;

        ComponentCoordinates coordinates = new ComponentCoordinates();
        coordinates.position.columnIndex = columnIndex;
        coordinates.position.rowIndex = rowIndex;
        coordinates.span.columnSpan = columnSpan;
        coordinates.span.rowSpan = rowSpan;

        return coordinates;
    }

    @Override
    public void onDragLeave(Event event) {
        if (DragHandler.isDragging()) {
            if (dragging) {
                dashboardModel.rollback();
                dashboardModel.removeComponent(DragHandler.getDragSource().getWidget());
            } else {
                dashboardModel.rollback();
            }

            this.dragColumnIndex = -1;
            this.dragRowIndex = -1;
        } else {
            LOG.severe("No active drag source");
        }
    }

    @Override
    public void onDrop(Event event) {
        if (DragHandler.isDragging()) {
            Widget dragSourceWidget = DragHandler.getDragSource().getWidget();
            DragHandler.dropped();

            if (dashboardModel.containsWidget(placeHolder)) {
                dashboardModel.replaceComponent(placeHolder, dragSourceWidget);
                dashboardModel.commit(true);
            } else {
                dashboardModel.rollback();
            }

            this.dragColumnIndex = -1;
            this.dragRowIndex = -1;
        } else {
            LOG.severe("No active drag source");
        }
    }

    @Override
    public void onDragEnd(Event event) {
        boolean dropped = DragHandler.isDropped();
        DragHandler.dragEnd(event);

        if (dropped) {
            dashboardModel.commit(true);
        } else {
            dashboardModel.rollback();
        }

        this.dragging = false;
        this.dragColumnIndex = -1;
        this.dragRowIndex = -1;
    }
}