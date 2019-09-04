/*
 * Creator:
 * 12.11.18 15:24 Tobias Sagmeister
 *
 * Maintainer:
 * 12.11.18 15:24 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.model;

import ch.sagmeister.vaadin.dashboard.client.model.event.ColumnsChangedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ComponentAddedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ComponentMovedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ComponentRemovedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ModelChangedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ModelCommitEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ModelEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.RowHeightChangedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.RowsChangedEvent;
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import ch.sagmeister.vaadin.dashboard.shared.ComponentOptions;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DashboardModel {

    private int rowHeight = 100;
    private int rows;
    private int columns;
    private List<ComponentModel> components;

    private Set<ModelListener> modelListeners;

    private boolean isDirty;
    private Memento memento;

    public DashboardModel() {
        this.components = new ArrayList<>();
        this.modelListeners = new HashSet<>();
    }

    // Copy constructor
    public DashboardModel(DashboardModel dashboardModel) {
        this();

        this.rowHeight = dashboardModel.getRowHeight();
        this.rows = dashboardModel.getRows();
        this.columns = dashboardModel.getColumns();

        for (ComponentModel componentModel : dashboardModel.components) {
            components.add(new ComponentModel(componentModel));
        }
    }

    private void fireModelEvent(ModelEvent event) {
        modelListeners.forEach(listener -> listener.onModelEvent(event));
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        ensureMemento();

        int oldRowHeight = this.rowHeight;
        this.rowHeight = rowHeight;
        fireModelEvent(new RowHeightChangedEvent(oldRowHeight, rowHeight));
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        ensureMemento();

        int oldRows = this.rows;
        this.rows = rows;
        fireModelEvent(new RowsChangedEvent(oldRows, rows));
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        ensureMemento();

        int oldColumns = this.columns;
        this.columns = columns;
        fireModelEvent(new ColumnsChangedEvent(oldColumns, columns));
    }

    public void addComponent(Widget widget, ComponentOptions componentOptions) {
        if (!containsWidget(widget)) {
            ensureMemento();

            components.add(new ComponentModel(widget, componentOptions));
            fireModelEvent(new ComponentAddedEvent(widget, componentOptions));

            fixDependents(widget);

            rearrangeDashboard();
            ensureRows();
        }
    }

    private void ensureMemento() {
        if (!isDirty()) {
            this.memento = Memento.of(this);
            this.isDirty = true;
        }
    }

    public void moveComponent(Widget widget, ComponentCoordinates coordinates) {
        moveComponent(widget, coordinates, true);
    }

    private void moveComponent(Widget widget, ComponentCoordinates coordinates, boolean pack) {
        ensureMemento();

        ComponentModel componentModel = findComponentModel(widget);

        ComponentOptions oldComponentOptions = componentModel.getComponentOptions();
        ComponentOptions componentOptions = ComponentOptions.of(coordinates);

        componentModel.setComponentOptions(componentOptions);

        fireModelEvent(new ComponentMovedEvent(widget, oldComponentOptions.coordinates, componentOptions.coordinates));

        if (pack) {
            fixDependents(widget);
            rearrangeDashboard();
            ensureRows();
        }
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void commit(boolean userOriginated) {
        if (isDirty()) {
            if (userOriginated) {
                List<ModelChangedEvent> modelChangedEvents = collectChanges(this, memento.getDashboardModel());
                clearMemento();
                if (!modelChangedEvents.isEmpty()) {
                    fireModelEvent(new ModelCommitEvent(modelChangedEvents));
                }
            } else {
                clearMemento();
            }
        }
    }

    public void rollback() {
        if (isDirty()) {
            memento.apply(this);
            clearMemento();
        }
    }

    private static List<ModelChangedEvent> collectChanges(DashboardModel newModel, DashboardModel oldModel) {
        final List<ModelChangedEvent> modelChangedEvents = new ArrayList<>();

        if (newModel.rows != oldModel.rows) {
            modelChangedEvents.add(new RowsChangedEvent(oldModel.rows, newModel.rows));
        }

        if (newModel.columns != oldModel.columns) {
            modelChangedEvents.add(new ColumnsChangedEvent(oldModel.columns, newModel.columns));
        }

        if (newModel.rowHeight != oldModel.rowHeight) {
            modelChangedEvents.add(new RowHeightChangedEvent(oldModel.rowHeight, newModel.rowHeight));
        }

        final List<ComponentModel> newComponentModels = newModel.components;

        // Copy old list for secure modifications
        final List<ComponentModel> oldComponentModels = new ArrayList<>(oldModel.components);

        for (ComponentModel componentModel : newComponentModels) {
            // Component added
            if (!oldModel.containsWidget(componentModel.getWidget())) {
                ComponentAddedEvent addEvent = new ComponentAddedEvent(componentModel.getWidget(), componentModel.getComponentOptions());
                modelChangedEvents.add(addEvent);
            } else {
                ComponentCoordinates oldCoordinates = oldModel.getComponentOptions(componentModel.getWidget()).coordinates;
                ComponentCoordinates coordinates = componentModel.getComponentOptions().coordinates;

                if (!coordinates.equals(oldCoordinates)) {
                    ComponentMovedEvent componentMovedEvent = new ComponentMovedEvent(componentModel.getWidget(), oldCoordinates, coordinates);
                    modelChangedEvents.add(componentMovedEvent);
                }
            }

            oldComponentModels.remove(componentModel);
        }

        // Remained components in old model are removed in new one
        for (ComponentModel componentModel : oldComponentModels) {
            ComponentRemovedEvent removeEvent = new ComponentRemovedEvent(componentModel.getWidget());
            modelChangedEvents.add(removeEvent);
        }

        return modelChangedEvents;
    }

    private void clearMemento() {
        this.memento = null;
        this.isDirty = false;
    }

    private Set<Widget> intersectsComponents(ComponentCoordinates coordinates, Set<Widget> excluded) {
        Set<Widget> intersectedWidgets = new LinkedHashSet<>();

        for (ComponentModel componentModel : getComponents()) {
            if (excluded.contains(componentModel.getWidget())) {
                continue;
            }

            if (componentModel.getComponentOptions().coordinates.intersects(coordinates)) {
                intersectedWidgets.add(componentModel.getWidget());
            }
        }

        return intersectedWidgets;
    }

    public boolean intersects(ComponentCoordinates coordinates, Set<Widget> excluded) {
        return !intersectsComponents(coordinates, excluded).isEmpty();
    }

    private void ensureRows() {
        int neededRows = getMaximumRowCountByWidgets();

        if (getRows() != neededRows) {
            setRows(neededRows);
        }
    }

    private int getMaximumRowCountByWidgets() {
        int rows = 0;
        for (ComponentModel componentModel : getComponents()) {
            ComponentCoordinates coordinates = getComponentOptions(componentModel.getWidget()).coordinates;
            int rowsNeeded = coordinates.position.rowIndex + coordinates.span.rowSpan;
            if (rowsNeeded > rows) {
                rows = rowsNeeded;
            }
        }
        return rows;
    }

    private void fixDependents(Widget widget) {
        Set<Widget> dependents = getDependents(widget);

        ComponentModel componentModel = findComponentModel(widget);
        int moveRows = componentModel.getComponentOptions().coordinates.position.rowIndex + componentModel.getComponentOptions().coordinates.span.rowSpan;

        for (Widget dependent : dependents) {
            ComponentModel dependentComponentModel = findComponentModel(dependent);

            ComponentOptions oldDependentComponentOptions = dependentComponentModel.getComponentOptions();
            ComponentCoordinates oldDependentCoordinates = oldDependentComponentOptions.coordinates;

            ComponentCoordinates newCoordinates = new ComponentCoordinates(oldDependentCoordinates.position.columnIndex, oldDependentCoordinates.position.rowIndex + moveRows, oldDependentCoordinates.span.columnSpan, oldDependentCoordinates.span.rowSpan);
            ComponentOptions newDependentComponentOptions = ComponentOptions.of(newCoordinates);
            dependentComponentModel.setComponentOptions(newDependentComponentOptions);

            fireModelEvent(new ComponentMovedEvent(dependent, oldDependentComponentOptions.coordinates, newDependentComponentOptions.coordinates));
        }
    }

    private Set<Widget> getDependents(Widget widget) {
        Set<Widget> dependents = new LinkedHashSet<>();
        dependents.add(widget);

        ComponentCoordinates coordinates = getComponentOptions(widget).coordinates;
        Set<Widget> comps = intersectsComponents(coordinates, Collections.singleton(widget));
        dependents.addAll(comps);

        for (Widget comp : comps) {
            collectDependents(comp, dependents);
        }

        dependents.remove(widget);

        return dependents;
    }

    private void collectDependents(Widget widget, Set<Widget> dependents) {
        dependents.add(widget);

        ComponentCoordinates coordinates = getComponentOptions(widget).coordinates;

        ComponentCoordinates newCoordinates = new ComponentCoordinates(coordinates.position.columnIndex, coordinates.position.rowIndex + 1, coordinates.span.columnSpan, coordinates.span.rowSpan);
        Set<Widget> comps = intersectsComponents(newCoordinates, Collections.singleton(widget));
        for (Widget comp : comps) {
            if (!dependents.contains(comp)) {
                collectDependents(comp, dependents);
            }
        }
    }

    private void rearrangeDashboard() {
        Collections.sort(components, new ComponentModelComparator(1));

        for (ComponentModel componentModel : getComponents()) {
            Widget widget = componentModel.getWidget();
            ComponentCoordinates coordinates = componentModel.getComponentOptions().coordinates;
            int rowIndex = coordinates.position.rowIndex;

            while (rowIndex > 0) {
                rowIndex--;

                ComponentCoordinates newCoordinates = new ComponentCoordinates(coordinates.position.columnIndex, rowIndex, coordinates.span.columnSpan, coordinates.span.rowSpan);
                if (!intersects(newCoordinates, Collections.singleton(widget))) {
                    moveComponent(widget, newCoordinates, false);
                } else {
                    break;
                }
            }
        }
    }

    private ComponentModel findComponentModel(Widget widget) {
        return components.stream().filter(w -> w.getWidget() == widget).findFirst().orElse(null);
    }

    public void removeComponent(Widget widget) {
        ensureMemento();

        ComponentModel componentModel = findComponentModel(widget);
        components.remove(componentModel);

        fireModelEvent(new ComponentRemovedEvent(widget));

        rearrangeDashboard();
        ensureRows();
    }

    public void removeAllComponents() {
        ensureMemento();

        for (ComponentModel componentModel : new ArrayList<>(components)) {
            components.remove(componentModel);
            fireModelEvent(new ComponentRemovedEvent(componentModel.getWidget()));
        }
    }

    public ComponentOptions getComponentOptions(Widget widget) {
        return findComponentModel(widget).getComponentOptions();
    }

    public boolean containsWidget(Widget widget) {
        return findComponentModel(widget) != null;
    }

    public List<ComponentModel> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public Registration addModelListener(ModelListener listener) {
        modelListeners.add(listener);
        return () -> modelListeners.remove(listener);
    }

    public void replaceComponent(Widget oldComponent, Widget newComponent) {
        ComponentModel oldComponentModel = findComponentModel(oldComponent);
        removeComponent(oldComponent);
        addComponent(newComponent, oldComponentModel.getComponentOptions());
    }

    @FunctionalInterface
    public interface ModelListener {

        void onModelEvent(ModelEvent event);
    }

    @FunctionalInterface
    public interface Registration {

        void remove();
    }

    private static final class Memento {

        private final DashboardModel dashboardModel;

        private Memento(final DashboardModel dashboardModel) {
            this.dashboardModel = dashboardModel;
        }

        DashboardModel getDashboardModel() {
            return dashboardModel;
        }

        static Memento of(DashboardModel dashboardModel) {
            return new Memento(new DashboardModel(dashboardModel));
        }

        void apply(DashboardModel dashboardModel) {
            final List<ModelChangedEvent> modelChangedEvents = collectChanges(this.dashboardModel, dashboardModel);
            for (ModelChangedEvent modelChangedEvent : modelChangedEvents) {
                if (modelChangedEvent instanceof RowsChangedEvent) {
                    dashboardModel.setRows(((RowsChangedEvent) modelChangedEvent).getRows());
                }

                if (modelChangedEvent instanceof ColumnsChangedEvent) {
                    dashboardModel.setColumns(((ColumnsChangedEvent) modelChangedEvent).getColumns());
                }

                if (modelChangedEvent instanceof RowHeightChangedEvent) {
                    dashboardModel.setRowHeight(((RowHeightChangedEvent) modelChangedEvent).getRowHeight());
                }

                if (modelChangedEvent instanceof ComponentAddedEvent) {
                    ComponentAddedEvent addedEvent = (ComponentAddedEvent) modelChangedEvent;
                    dashboardModel.addComponent(addedEvent.getWidget(), addedEvent.getComponentOptions());
                }

                if (modelChangedEvent instanceof ComponentRemovedEvent) {
                    ComponentRemovedEvent removedEvent = (ComponentRemovedEvent) modelChangedEvent;
                    dashboardModel.removeComponent(removedEvent.getWidget());
                }

                if (modelChangedEvent instanceof ComponentMovedEvent) {
                    ComponentMovedEvent movedEvent = (ComponentMovedEvent) modelChangedEvent;
                    dashboardModel.moveComponent(movedEvent.getWidget(), movedEvent.getCoordinates());
                }
            }
        }
    }

    private static class ComponentModelComparator implements Comparator<ComponentModel> {

        private final int direction;

        public ComponentModelComparator(int direction) {
            this.direction = direction;
        }

        @Override
        public int compare(ComponentModel o1, ComponentModel o2) {
            ComponentOptions componentOptionsA = o1.getComponentOptions();
            ComponentOptions componentOptionsB = o2.getComponentOptions();

            ComponentCoordinates coordinatesA = componentOptionsA.coordinates;
            ComponentCoordinates coordinatesB = componentOptionsB.coordinates;

            int aY = coordinatesA.position.rowIndex;
            int bY = coordinatesB.position.rowIndex;

            int comp = Integer.compare(aY, bY);
            if (comp == 0) {
                int aX = coordinatesA.position.columnIndex;
                int bX = coordinatesB.position.columnIndex;

                comp = Integer.compare(aX, bX);
            }

            return comp * direction;
        }
    }
}
