/*
 * Creator:
 * 14.05.18 13:36 Tobias Sagmeister
 *
 * Maintainer:
 * 14.05.18 13:36 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.server;

import ch.sagmeister.vaadin.dashboard.server.event.ComponentAddedEvent;
import ch.sagmeister.vaadin.dashboard.server.event.ComponentChangedEvent;
import ch.sagmeister.vaadin.dashboard.server.event.ComponentMovedEvent;
import ch.sagmeister.vaadin.dashboard.server.event.ComponentRemovedEvent;
import ch.sagmeister.vaadin.dashboard.server.event.ComponentsChangedEvent;
import ch.sagmeister.vaadin.dashboard.server.event.ComponentsChangedListener;
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import ch.sagmeister.vaadin.dashboard.shared.ComponentOptions;
import ch.sagmeister.vaadin.dashboard.shared.ComponentPosition;
import ch.sagmeister.vaadin.dashboard.shared.ComponentSpan;
import ch.sagmeister.vaadin.dashboard.shared.DashboardState;
import ch.sagmeister.vaadin.dashboard.shared.rpc.server.DashboardServerRpc;
import com.vaadin.shared.Connector;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dashboard component for arranging widgets in rows and columns via DnD
 */
public class Dashboard extends AbstractComponentContainer {

    private static final int DEFAULT_COLUMN_SPAN = 1;
    private static final int DEFAULT_ROW_SPAN = 1;

    public Dashboard() {
        registerRpc(new DashboardServerRpc() {

            @Override
            public void commit(List<WidgetChangedEvent> widgetChangedEvents) {
                final List<ComponentChangedEvent> events = new ArrayList<>();

                for (WidgetChangedEvent event : widgetChangedEvents) {
                    Component widget = (Component) event.connector;
                    ComponentCoordinates coordinates = event.coordinates;

                    switch (event.type) {
                        case ADD:
                            Dashboard.super.addComponent(widget);
                            getState().componentOptions.put(widget, ComponentOptions.of(coordinates));
                            events.add(new ComponentAddedEvent(widget, ComponentOptions.of(coordinates)));
                            break;

                        case REMOVE:
                            break;

                        case MOVE:
                            ComponentCoordinates oldCoordinates = getCoordinates(widget);

                            ComponentOptions componentOptions = getComponentOptions(widget, true);
                            componentOptions.coordinates = new ComponentCoordinates(coordinates);

                            if (!oldCoordinates.equals(coordinates)) {
                                events.add(new ComponentMovedEvent(widget, oldCoordinates, getCoordinates(widget)));
                            }
                            break;
                    }
                }

                if (!events.isEmpty()) {
                    ComponentsChangedEvent componentsChangedEvent = new ComponentsChangedEvent(Dashboard.this, events);
                    fireEvent(componentsChangedEvent);
                }
            }
        });
    }

    /**
     * Get coordinates of a component
     *
     * @param component
     * @return coordinates
     */
    @Nonnull
    public ComponentCoordinates getCoordinates(@Nonnull Component component) {
        ComponentOptions componentOptions = getComponentOptions(component, false);

        ComponentPosition position = new ComponentPosition(componentOptions.coordinates.position.columnIndex, componentOptions.coordinates.position.rowIndex);
        ComponentSpan span = new ComponentSpan(componentOptions.coordinates.span.columnSpan, componentOptions.coordinates.span.rowSpan);

        return new ComponentCoordinates(position, span);
    }

    private ComponentOptions getComponentOptions(@Nonnull Component component, boolean modify) {
        if (component.getParent() != this) {
            throw new IllegalArgumentException("Given component is not child of this layout");
        }

        ComponentOptions opt = getState(modify).componentOptions.get(component);
        return opt;
    }

    private boolean isAreaEmpty(int columnIndex, int rowIndex, int columnSpan, int rowSpan) {
        return isAreaEmpty(new ComponentCoordinates(new ComponentPosition(columnIndex, rowIndex), new ComponentSpan(columnSpan, rowSpan)));
    }

    private boolean isAreaEmpty(@Nonnull ComponentCoordinates coordinates) {
        for (int dx = 0; dx < coordinates.span.columnSpan; ++dx) {
            for (int dy = 0; dy < coordinates.span.rowSpan; ++dy) {
                Component occupant = getComponentByPosition(coordinates.position.columnIndex + dx, coordinates.position.rowIndex + dy, true);
                if (occupant != null) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean canComponentMove(@Nonnull Component component, int columnIndex, int rowIndex, int columnSpan, int rowSpan) {
        return canComponentMove(component, new ComponentCoordinates(new ComponentPosition(columnIndex, rowIndex), new ComponentSpan(columnSpan, rowSpan)));
    }

    private boolean canComponentMove(@Nonnull Component component, ComponentCoordinates coordinates) {
        for (int dx = 0; dx < coordinates.span.columnSpan; ++dx) {
            for (int dy = 0; dy < coordinates.span.rowSpan; ++dy) {
                Component occupant = getComponentByPosition(coordinates.position.columnIndex + dx, coordinates.position.rowIndex + dy, true);
                if (occupant != null && occupant != component) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected DashboardState getState() {
        return (DashboardState) super.getState();
    }

    @Override
    protected DashboardState getState(boolean markAsDirty) {
        return (DashboardState) super.getState(markAsDirty);
    }

    private void assertColumnSpanBound(int columnSpan) {
        if (getState(false).columns < columnSpan) {
            throw new IndexOutOfBoundsException("ColumnSpan must be smaller than number of columns");
        }
    }

    private void assertColumnBound(int columnIndex, int columnSpan) {
        if (columnIndex < 0) {
            throw new IndexOutOfBoundsException("ColumnIndex must be greater than zero");
        }

        if (getState(false).columns < (columnIndex + columnSpan)) {
            throw new IndexOutOfBoundsException("ColumnSpan plus ColumnIndex must be smaller than number of columns");
        }
    }

    /**
     * Search for an empty space by span
     *
     * @param columnSpan
     * @param rowSpan
     * @return
     */
    @Nonnull
    public ComponentPosition searchEmptySpaceForSpan(int columnSpan, int rowSpan) {
        assertColumnSpanBound(columnSpan);

        int columns = getState(false).columns;
        int rows = 0;

        while (true) {
            for (int rowIndex = 0; rowIndex <= rows; rowIndex++) {
                for (int columnIndex = 0; columnIndex <= (columns - columnSpan); columnIndex++) {
                    if (isAreaEmpty(columnIndex, rowIndex, columnSpan, rowSpan)) {
                        return new ComponentPosition(columnIndex, rowIndex);
                    }
                }
            }

            rows++;
        }
    }

    /**
     * Move component by coordinates
     *
     * @param widget
     * @param columnIndex
     * @param rowIndex
     * @param columnSpan
     * @param rowSpan
     * @return
     */
    public boolean moveComponent(@Nonnull Component widget, int columnIndex, int rowIndex, int columnSpan, int rowSpan) {
        assertColumnBound(columnIndex, columnSpan);

        if (canComponentMove(widget, columnIndex, rowIndex, columnSpan, rowSpan)) {
            ComponentOptions componentOptions = getComponentOptions(widget, true);
            ComponentCoordinates oldCoordinates = getCoordinates(widget);

            componentOptions.coordinates.position.columnIndex = columnIndex;
            componentOptions.coordinates.position.rowIndex = rowIndex;
            componentOptions.coordinates.span.columnSpan = columnSpan;
            componentOptions.coordinates.span.rowSpan = rowSpan;

            ComponentsChangedEvent componentsChangedEvent = new ComponentsChangedEvent(this, Collections.singletonList(new ComponentMovedEvent(widget, oldCoordinates, getCoordinates(widget))));
            fireEvent(componentsChangedEvent);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Bulk-Move of components by a list of moves
     *
     * @param componentMoves
     */
    public void moveComponents(@Nonnull List<ComponentMove> componentMoves) {
        final List<ComponentChangedEvent> events = new ArrayList<>();

        for (ComponentMove componentMove : componentMoves) {
            Component component = componentMove.component;

            ComponentOptions componentOptions = getComponentOptions(component, true);
            ComponentCoordinates oldCoordinates = getCoordinates(component);

            componentOptions.coordinates.position.columnIndex = componentMove.columnIndex;
            componentOptions.coordinates.position.rowIndex = componentMove.rowIndex;
            componentOptions.coordinates.span.columnSpan = componentMove.columnSpan;
            componentOptions.coordinates.span.rowSpan = componentMove.rowSpan;

            events.add(new ComponentMovedEvent(component, oldCoordinates, getCoordinates(component)));
        }

        ComponentsChangedEvent componentsChangedEvent = new ComponentsChangedEvent(Dashboard.this, events);
        fireEvent(componentsChangedEvent);
    }

    /**
     * Add a component by default span of 1, 1
     *
     * @param component
     */
    @Override
    public void addComponent(@Nonnull Component component) {
        addComponentBySpan(component, 1, 1);
    }

    /**
     * Add a component by position
     *
     * @param component
     * @param columnIndex
     * @param rowIndex
     */
    public void addComponentByPosition(@Nonnull Component component, int columnIndex, int rowIndex) {
        addComponent(component, columnIndex, rowIndex, DEFAULT_COLUMN_SPAN, DEFAULT_ROW_SPAN);
    }

    /**
     * Add a component by span
     *
     * @param component
     * @param columnSpan
     * @param rowSpan
     */
    public void addComponentBySpan(@Nonnull Component component, int columnSpan, int rowSpan) {
        ComponentPosition emptyArea = searchEmptySpaceForSpan(columnSpan, rowSpan);

        if (emptyArea != null) {
            addComponent(component, emptyArea.columnIndex, emptyArea.rowIndex, columnSpan, rowSpan);
        }
    }

    /**
     * Add a component by full coordinates
     *
     * @param component
     * @param columnIndex
     * @param rowIndex
     * @param columnSpan
     * @param rowSpan
     */
    public void addComponent(@Nonnull Component component, int columnIndex, int rowIndex, int columnSpan, int rowSpan) {
        assertColumnBound(columnIndex, columnSpan);

        if (isAreaEmpty(columnIndex, rowIndex, columnSpan, rowSpan)) {
            super.addComponent(component);

            ComponentOptions componentOptions = new ComponentOptions();
            componentOptions.coordinates.position.columnIndex = columnIndex;
            componentOptions.coordinates.position.rowIndex = rowIndex;
            componentOptions.coordinates.span.columnSpan = columnSpan;
            componentOptions.coordinates.span.rowSpan = rowSpan;

            getState().componentOptions.put(component, componentOptions);

            ComponentsChangedEvent componentsChangedEvent = new ComponentsChangedEvent(this, Collections.singletonList(new ComponentAddedEvent(component, componentOptions)));
            fireEvent(componentsChangedEvent);
        }
    }

    public int getColumns() {
        return getState().columns;
    }

    public void setColumns(int columns) {
        getState().columns = columns;
    }

    public int getRowHeight() {
        return getState().rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        getState().rowHeight = rowHeight;
    }

    public boolean isEditable() {
        return getState().editable;
    }

    /**
     * Set editable flag of dashboard for enable/disable DnD and resizing features
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        getState().editable = editable;
    }

    /**
     * Get component by position
     *
     * @param columnIndex
     * @param rowIndex
     * @return component or null if not existing
     */
    @Nullable
    public Component getComponentByPosition(int columnIndex, int rowIndex) {
        return getComponentByPosition(columnIndex, rowIndex, false);
    }

    @Nullable
    private Component getComponentByPosition(int columnIndex, int rowIndex, boolean acceptInsideHit) {
        for (Connector connector : getState().componentOptions.keySet()) {
            ComponentOptions info = getState().componentOptions.get(connector);
            if (acceptInsideHit) {
                if (columnIndex >= info.coordinates.position.columnIndex && columnIndex < (info.coordinates.position.columnIndex + info.coordinates.span.columnSpan) && rowIndex >= info.coordinates.position.rowIndex && rowIndex < (info.coordinates.position.rowIndex + info.coordinates.span.rowSpan)) {
                    return (Component) connector;
                }
            } else {
                if (info.coordinates.position.columnIndex == columnIndex && info.coordinates.position.rowIndex == rowIndex) {
                    return (Component) connector;
                }
            }
        }
        return null;
    }

    /**
     * Get component by full coordinates
     *
     * @param coordinates
     * @return component or null if not existing
     */
    @Nullable
    public Component getComponentByCoordinates(@Nonnull ComponentCoordinates coordinates) {
        for (Component widget : getComponents()) {
            if (getCoordinates(widget).equals(coordinates)) {
                return widget;
            }
        }

        return null;
    }

    /**
     * Get components of dashboard
     *
     * @return list of components
     */
    @Nonnull
    public List<Component> getComponents() {
        return getState(false).componentOptions.keySet().stream().map(component -> (Component) component).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    /**
     * Add a ComponentsChangedListener for handling events like move, add, remove
     *
     * @param listener
     * @return
     */
    public Registration addComponentsChangedListener(@Nonnull ComponentsChangedListener listener) {
        return addListener(ComponentsChangedEvent.class, listener, ComponentsChangedListener.ON_COMPONENTS_CHANGED_METHOD);
    }

    /**
     * Remove component
     *
     * @param component
     */
    @Override
    public void removeComponent(@Nonnull Component component) {
        getState().componentOptions.remove(component);
        super.removeComponent(component);
        ComponentsChangedEvent componentsChangedEvent = new ComponentsChangedEvent(this, Collections.singletonList(new ComponentRemovedEvent(component)));
        fireEvent(componentsChangedEvent);
    }

    /**
     * Replace component
     *
     * @param oldComponent
     * @param newComponent
     */
    @Override
    public void replaceComponent(@Nonnull Component oldComponent, @Nonnull Component newComponent) {
        ComponentOptions options = getComponentOptions(oldComponent, false);
        ComponentCoordinates componentCoordinates = options.coordinates;
        removeComponent(oldComponent);

        addComponent(newComponent, componentCoordinates.position.columnIndex, componentCoordinates.position.rowIndex, componentCoordinates.span.columnSpan, componentCoordinates.span.rowSpan);
    }

    /**
     * Get number of components
     *
     * @return
     */
    @Override
    public int getComponentCount() {
        return getState(false).componentOptions.size();
    }

    @Override
    public Iterator<Component> iterator() {
        return getComponents().iterator();
    }

    /**
     * Helper class for holding bulk-move data
     *
     * @see Dashboard#moveComponents(List)
     */
    public static class ComponentMove {

        private final Component component;
        private final int columnIndex;
        private final int rowIndex;
        private final int columnSpan;
        private final int rowSpan;

        public ComponentMove(@Nonnull Component component, int columnIndex, int rowIndex, int columnSpan, int rowSpan) {
            this.component = component;
            this.columnIndex = columnIndex;
            this.rowIndex = rowIndex;
            this.columnSpan = columnSpan;
            this.rowSpan = rowSpan;
        }
    }
}
