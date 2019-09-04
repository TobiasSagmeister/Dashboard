/*
 * Creator:
 * 14.05.18 13:35 Tobias Sagmeister
 *
 * Maintainer:
 * 14.05.18 13:35 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component;

import ch.sagmeister.vaadin.dashboard.client.component.util.DashboardUtil;
import ch.sagmeister.vaadin.dashboard.client.model.ComponentModel;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.client.model.event.ColumnsChangedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ComponentAddedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ComponentChangedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ComponentMovedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ComponentRemovedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ModelChangedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ModelCommitEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.ModelEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.RowHeightChangedEvent;
import ch.sagmeister.vaadin.dashboard.client.model.event.RowsChangedEvent;
import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import ch.sagmeister.vaadin.dashboard.shared.ComponentOptions;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.VCssLayout;
import elemental.events.Event;
import elemental.events.EventRemover;
import elemental.events.EventTarget;

import java.util.HashSet;
import java.util.Set;

public class VDashboard extends ComplexPanel {

    public static final String STYLE_DASHBOARD = "v-dashboard";
    private static final String STYLE_CONTENT_PANE = STYLE_DASHBOARD + "-content";

    private final Set<DashboardChangedListener> dashboardChangedListeners = new HashSet<>();
    private final DashboardModel model;
    private final VComponentPlaceholder placeHolder;
    private final DragAndDropHandler dragAndDropHandler;
    private final ResizeHandler resizeHandler;

    private String elementId;
    private boolean editable;
    private VCssLayout contentPane;
    private AbsoluteResizeHandler absoluteResizeHandler;

    private EventRemover dragRegistration;

    public VDashboard() {
        this.model = new DashboardModel();
        model.addModelListener(this::onModelEvent);
        this.placeHolder = new VComponentPlaceholder(model);
        this.dragAndDropHandler = new DragAndDropHandlerImpl(model, placeHolder);
        this.resizeHandler = new ResizeHandlerImpl(model, placeHolder);
        resizeHandler.addResizeListener(() -> {
            if (absoluteResizeHandler != null) {
                absoluteResizeHandler.handle();
            }
        });

        init();
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        this.dragRegistration = DragHandler.addDragListener(new DragHandler.DragListener() {
            @Override
            public void onDragStart(Event event) {
                setWidgetsActive(false);
            }

            @Override
            public void onDragEnd(Event event) {
                setWidgetsActive(true);
            }
        });
    }

    private void setWidgetsActive(boolean active) {
        for (ComponentModel componentModel : model.getComponents()) {
            Widget widget = componentModel.getWidget();

            if (widget == placeHolder || (DragHandler.isDragging() && DragHandler.getDragSource().getWidget() == widget)) {
                continue;
            }

            VComponentWrapper widgetWrapper = DashboardUtil.findComponentWrapper(widget);
            widgetWrapper.setActive(active);
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        if (dragRegistration != null) {
            dragRegistration.remove();
        }
    }

    private void init() {
        DivElement mainPane = Document.get().createDivElement();
        setElement(mainPane);
        setStyleName(STYLE_DASHBOARD);

        this.elementId = "v-dashboard-" + HTMLPanel.createUniqueId();
        getElement().setId(elementId);

        this.contentPane = new VCssLayout();
        contentPane.setSize("100%", "100%");
        contentPane.setStyleName(STYLE_CONTENT_PANE);
        add(contentPane);

        contentPane.add(placeHolder);

        setEditable(true);

        EventTarget eventTarget = (EventTarget) contentPane.getElement();
        eventTarget.addEventListener(Event.DRAGENTER, evt -> {
            evt.preventDefault();
            evt.stopImmediatePropagation();
            dragAndDropHandler.onDragEnter(evt);
        });
        eventTarget.addEventListener(Event.DRAGOVER, evt -> {
            evt.preventDefault();
            evt.stopImmediatePropagation();
            dragAndDropHandler.onDragOver(evt);
        });
        eventTarget.addEventListener(Event.DRAGLEAVE, evt -> {
            evt.preventDefault();
            evt.stopImmediatePropagation();
            dragAndDropHandler.onDragLeave(evt);
        });
        eventTarget.addEventListener(Event.DROP, evt -> {
            evt.stopImmediatePropagation();
            evt.preventDefault();
            dragAndDropHandler.onDrop(evt);
        });
    }

    private void onModelEvent(ModelEvent event) {
        if (event instanceof ModelChangedEvent) {
            ModelChangedEvent modelChangedEvent = (ModelChangedEvent) event;

            if (event instanceof ComponentChangedEvent) {
                ComponentChangedEvent componentChangedEvent = (ComponentChangedEvent) event;

                Widget widget = componentChangedEvent.getWidget();
                if ((DragHandler.isDragging() && widget == DragHandler.getDragSource().getWidget())) {
                    return;
                }

                if (componentChangedEvent instanceof ComponentAddedEvent) {
                    ComponentAddedEvent addedEvent = (ComponentAddedEvent) componentChangedEvent;
                    ComponentCoordinates coordinates = addedEvent.getComponentOptions().coordinates;


                    if (widget == placeHolder) {
                        placeHolder.moveTo(coordinates);
                        placeHolder.display(true);
                    } else {
                        if (DragHandler.isDragging()) {
                            throw new IllegalStateException("Not allowed");
                        }

                        VComponentWrapper wrapper = new VComponentWrapper(widget, editable, model, dragAndDropHandler, resizeHandler);
                        wrapper.moveTo(coordinates);
                        addComponentWrapper(wrapper);
                    }
                }

                if (componentChangedEvent instanceof ComponentRemovedEvent) {
                    if (widget == placeHolder) {
                        placeHolder.moveTo(ComponentCoordinates.ZERO);
                        placeHolder.display(false);
                    } else {
                        if (DragHandler.isDragging()) {
                            throw new IllegalStateException("Not allowed");
                        }

                        VComponentWrapper wrapper = DashboardUtil.findComponentWrapper(widget);
                        if (wrapper != null) {
                            removeComponentWrapper(wrapper);
                        }
                    }
                }

                if (componentChangedEvent instanceof ComponentMovedEvent) {
                    ComponentMovedEvent updatedEvent = (ComponentMovedEvent) componentChangedEvent;
                    ComponentCoordinates coordinates = updatedEvent.getCoordinates();

                    if (widget == placeHolder) {
                        placeHolder.moveTo(coordinates);
                    } else {
                        VComponentWrapper wrapper = DashboardUtil.findComponentWrapper(widget);
                        if (wrapper != null) {
                            if (!wrapper.areCoordinatesLocked()) {
                                wrapper.setWidgetOptions(ComponentOptions.of(coordinates));
                            }
                        }
                    }
                }
            }

            if (modelChangedEvent instanceof RowsChangedEvent
                    || modelChangedEvent instanceof ColumnsChangedEvent
                    || modelChangedEvent instanceof RowHeightChangedEvent) {
                reloadWidgetOptions();
            }
        }

        if (event instanceof ModelCommitEvent) {
            ModelCommitEvent modelCommitEvent = (ModelCommitEvent) event;

            reRenderComponents();

            fireDashboardChangedEvent(new DashboardChangedListener.DashboardChangedEvent(modelCommitEvent.getModelChangedEvents()));
        }
    }

    private void reRenderComponents() {
        contentPane.clear();
        contentPane.add(placeHolder);
        placeHolder.moveTo(ComponentCoordinates.ZERO);

        for (ComponentModel componentModel : model.getComponents()) {
            Widget widget = componentModel.getWidget();

            VComponentWrapper wrapper = new VComponentWrapper(widget, editable, model, dragAndDropHandler, resizeHandler);
            wrapper.moveTo(componentModel.getComponentOptions().coordinates);
            addComponentWrapper(wrapper);
        }
    }

    private void fireDashboardChangedEvent(DashboardChangedListener.DashboardChangedEvent event) {
        dashboardChangedListeners.forEach(listener -> listener.onDashboardChanged(event));
    }

    private void reloadWidgetOptions() {
        for (ComponentModel componentModel : model.getComponents()) {
            Widget widget = componentModel.getWidget();

            if (widget == placeHolder) {
                placeHolder.moveTo(componentModel.getComponentOptions().coordinates);
            } else {
                VComponentWrapper wrapper = DashboardUtil.findComponentWrapper(componentModel.getWidget());
                if (wrapper != null) {
                    if (!wrapper.areCoordinatesLocked()) {
                        wrapper.setWidgetOptions(componentModel.getComponentOptions());
                    }
                }
            }
        }
    }

    private void addComponentWrapper(VComponentWrapper wrapper) {
        contentPane.add(wrapper);
        super.add(wrapper.getWidget(), wrapper.getContentContainer());
    }

    private void removeComponentWrapper(VComponentWrapper widgetWrapper) {
        widgetWrapper.removeFromParent();
        super.remove(widgetWrapper.getWidget());
    }

    @Override
    public void add(Widget widget) {
        widget.removeFromParent();
        getChildren().add(widget);
        DOM.appendChild(getElement(), widget.getElement());
        adopt(widget);
    }

    public void addComponent(Widget widget, ComponentOptions componentOptions) {
        model.addComponent(widget, componentOptions);
        model.commit(false);
    }

    public void removeComponent(Widget widget) {
        model.removeComponent(widget);
        model.commit(false);
    }

    public void removeAllComponents() {
        model.removeAllComponents();
        model.commit(false);
    }

    public void setAbsoluteResizeHandler(AbsoluteResizeHandler handler) {
        this.absoluteResizeHandler = handler;
    }

    public void addDashboardChangedListener(DashboardChangedListener listener) {
        dashboardChangedListeners.add(listener);
    }

    public void setEditable(boolean editable) {
        for (ComponentModel componentModel : model.getComponents()) {
            VComponentWrapper wrapper = DashboardUtil.findComponentWrapper(componentModel.getWidget());
            wrapper.setEditable(editable);
        }

        this.editable = editable;
    }

    public void setColumns(int columns) {
        model.setColumns(columns);
        model.commit(false);
    }

    public void setRowHeight(int rowHeight) {
        model.setRowHeight(rowHeight);
        model.commit(false);
    }

    public int getColumns() {
        return model.getColumns();
    }

    public interface AbsoluteResizeHandler {

        void handle();
    }
}
