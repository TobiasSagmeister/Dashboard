/*
 * Creator:
 * 14.05.18 13:36 Tobias Sagmeister
 *
 * Maintainer:
 * 14.05.18 13:36 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client;

import ch.sagmeister.vaadin.dashboard.client.component.VDashboard;
import ch.sagmeister.vaadin.dashboard.client.model.event.ComponentAddedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ComponentChangedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ComponentMovedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ComponentRemovedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ModelChangedEvent;
import ch.sagmeister.vaadin.dashboard.server.Dashboard;
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import ch.sagmeister.vaadin.dashboard.shared.ComponentOptions;
import ch.sagmeister.vaadin.dashboard.shared.DashboardState;
import ch.sagmeister.vaadin.dashboard.shared.rpc.server.DashboardServerRpc;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.Util;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Connect(Dashboard.class)
public class VDashboardConnector extends AbstractComponentContainerConnector {

    private final static int CONVERT_NEGATIVE_IN_COMPARE = 10000;

    private transient final Comparator<Connector> childConnectorComparator = (a, b) -> {
        ComponentOptions aOptions = getState().componentOptions.get(a);
        ComponentOptions bOptions = getState().componentOptions.get(b);

        int aY = aOptions.coordinates.position.rowIndex;
        if (aY < 0) {
            aY = CONVERT_NEGATIVE_IN_COMPARE;
        }

        int bY = bOptions.coordinates.position.rowIndex;
        if (bY < 0) {
            bY = CONVERT_NEGATIVE_IN_COMPARE;
        }

        int comp = Integer.compare(aY, bY);
        if (comp == 0) {

            int aX = aOptions.coordinates.position.columnIndex;
            if (aX < 0) {
                aX = CONVERT_NEGATIVE_IN_COMPARE;
            }

            int bX = bOptions.coordinates.position.columnIndex;
            if (bX < 0) {
                bX = CONVERT_NEGATIVE_IN_COMPARE;
            }

            comp = Integer.compare(aX, bX);
        }

        return comp;
    };

    @Override
    protected void init() {
        super.init();

        getWidget().setAbsoluteResizeHandler(() -> getLayoutManager().forceLayout());

        getWidget().addDashboardChangedListener(event -> {
            final List<DashboardServerRpc.WidgetChangedEvent> widgetChangedEvents = new ArrayList<>();

            for (ModelChangedEvent modelChangedEvent : event.getModelChangedEvents()) {
                if (modelChangedEvent instanceof ComponentChangedEvent) {
                    ComponentChangedEvent componentChangedEvent = (ComponentChangedEvent) modelChangedEvent;

                    Widget widget = componentChangedEvent.getWidget();
                    ComponentConnector connector = findConnectorForWidget(widget);
                    if (connector != null) {
                        if (modelChangedEvent instanceof ComponentMovedEvent) {
                            ComponentMovedEvent componentMovedEvent = (ComponentMovedEvent) modelChangedEvent;

                            ComponentCoordinates coordinates = componentMovedEvent.getCoordinates();

                            if (connector != null && coordinates != null) {
                                DashboardServerRpc.WidgetChangedEvent movedEvent = new DashboardServerRpc.WidgetChangedEvent();
                                movedEvent.type = DashboardServerRpc.WidgetChangedEvent.Type.MOVE;
                                movedEvent.connector = connector;
                                movedEvent.coordinates = coordinates;
                                widgetChangedEvents.add(movedEvent);
                            }
                        }

                        if (modelChangedEvent instanceof ComponentAddedEvent) {
                            ComponentAddedEvent widgetAddedEvent = (ComponentAddedEvent) modelChangedEvent;

                            ComponentCoordinates coordinates = widgetAddedEvent.getComponentOptions().coordinates;

                            if (connector != null && coordinates != null) {
                                DashboardServerRpc.WidgetChangedEvent addedEvent = new DashboardServerRpc.WidgetChangedEvent();
                                addedEvent.type = DashboardServerRpc.WidgetChangedEvent.Type.ADD;
                                addedEvent.connector = connector;
                                addedEvent.coordinates = coordinates;
                                widgetChangedEvents.add(addedEvent);
                            }
                        }

                        if (modelChangedEvent instanceof ComponentRemovedEvent) {
                            // Ignore
                        }
                    }
                }
            }

            if (!widgetChangedEvents.isEmpty()) {
                getRpcProxy(DashboardServerRpc.class).commit(widgetChangedEvents);
            }
        });
    }

    private static ComponentConnector findConnectorForWidget(Widget widget) {
        Widget parent = widget;
        while (parent != null) {
            ComponentConnector connector = Util.findConnectorFor(parent);
            if (connector != null) {
                return connector;
            }

            parent = parent.getParent();
        }

        return null;
    }

    private List<Connector> getChildConnectorsInCoordinateOrder() {
        List<Connector> list = new ArrayList<Connector>();
        for (Connector connector : getState().componentOptions.keySet()) {
            list.add(connector);
        }

        Collections.sort(list, childConnectorComparator);

        return list;
    }

    @OnStateChange("columns")
    void onDashboardColumnsChanged() {
        getWidget().setColumns(getState().columns);
    }

    @OnStateChange("rowHeight")
    void onDashboardRowHeightChanged() {
        getWidget().setRowHeight(getState().rowHeight);
    }

    @OnStateChange("editable")
    void onEditableChanged() {
        getWidget().setEditable(getState().editable);
    }

    @OnStateChange("componentOptions")
    void onComponentOptionsChanged() {
        getWidget().removeAllComponents();

        for (Connector connector : getChildConnectorsInCoordinateOrder()) {
            ComponentConnector componentConnector = (ComponentConnector) connector;
            Widget widget = componentConnector.getWidget();
            ComponentOptions componentOptions = getState().componentOptions.get(componentConnector);
            getWidget().addComponent(widget, componentOptions);
        }
    }

    @Override
    public VDashboard getWidget() {
        return (VDashboard) super.getWidget();
    }

    @Override
    public DashboardState getState() {
        return (DashboardState) super.getState();
    }

    @Override
    public void updateCaption(ComponentConnector connector) {
        //ignore for now
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        //ignore for now
    }
}
