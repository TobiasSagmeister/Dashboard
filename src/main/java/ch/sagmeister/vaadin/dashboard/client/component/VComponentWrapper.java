/*
 * Creator:
 * 17.05.18 14:55 Tobias Sagmeister
 *
 * Maintainer:
 * 17.05.18 14:55 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component;

import ch.sagmeister.vaadin.dashboard.client.component.resizer.VResizeOverlay;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import ch.sagmeister.vaadin.dashboard.shared.ComponentOptions;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.WidgetUtil;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventTarget;

public class VComponentWrapper extends VDashboardComponent {

    private static final String STYLE_WIDGET = VDashboard.STYLE_DASHBOARD + "-widget";
    private static final String STYLE_WIDGET_CONTENT = STYLE_WIDGET + "-content";
    private static final String STYLE_DRAG_SOURCE = STYLE_WIDGET + "-dragsource";
    private static final String STYLE_DRAGGABLE = "v-draggable";

    private final Widget widget;
    private final DragAndDropHandler dragAndDropHandler;
    private final ResizeHandler resizeHandler;
    private final String elementId;

    private DivElement contentContainer;
    private VResizeOverlay resizeOverlay;

    private boolean editable;
    private boolean active;

    private EventListener dragStartListener = this::onDragStart;
    private EventListener dragEndListener = this::onDragEnd;

    VComponentWrapper(Widget widget, boolean editable, DashboardModel model, DragAndDropHandler dragAndDropHandler, ResizeHandler resizeHandler) {
        super(model);

        this.widget = widget;
        this.dragAndDropHandler = dragAndDropHandler;
        this.resizeHandler = resizeHandler;
        this.elementId = STYLE_WIDGET + HTMLPanel.createUniqueId();

        setElement(Document.get().createDivElement());
        setStyleName(STYLE_WIDGET);

        this.contentContainer = Document.get().createDivElement();
        contentContainer.addClassName(STYLE_WIDGET_CONTENT);
        getElement().appendChild(contentContainer);

        setEditable(editable);
        setDraggable(true);
        setActive(true);
    }

    private void setDraggable(boolean draggable) {
        EventTarget widgetEventTarget = getElement().cast();

        if (draggable) {
            getElement().setAttribute("draggable", "true");
            addStyleName(STYLE_DRAG_SOURCE);
            addStyleName(STYLE_DRAGGABLE);

            widgetEventTarget.addEventListener(Event.DRAGSTART, dragStartListener);
            widgetEventTarget.addEventListener(Event.DRAGEND, dragEndListener);
        } else {
            getElement().setAttribute("draggable", "false");
            removeStyleName(STYLE_DRAG_SOURCE);
            removeStyleName(STYLE_DRAGGABLE);

            widgetEventTarget.removeEventListener(Event.DRAGSTART, dragStartListener);
            widgetEventTarget.removeEventListener(Event.DRAGEND, dragEndListener);
        }
    }

    private void initEvent(NativeEvent nativeEvent) {
        setEffectAllowed(nativeEvent.getDataTransfer(), "move");

        int x = WidgetUtil.getRelativeX(getElement(), nativeEvent);
        int y = WidgetUtil.getRelativeY(getElement(), nativeEvent);
        nativeEvent.getDataTransfer().setDragImage(contentContainer.getFirstChildElement(), x, y);

        nativeEvent.getDataTransfer().setData("text/plain", "text");
    }

    private void onDragStart(Event event) {
        event.stopImmediatePropagation();

        NativeEvent nativeEvent = (NativeEvent) event;
        initEvent(nativeEvent);

        // Hide element
        Timer t = new Timer() {

            @Override
            public void run() {
                getElement().getStyle().setProperty("transform", "translateX(-9999px)");
            }
        };
        t.schedule(0);

        dragAndDropHandler.onDragStart(event);
    }

    private void onDragEnd(Event event) {
        event.stopImmediatePropagation();
        dragAndDropHandler.onDragEnd(event);
    }

    private void removeResizeOverlay() {
        if (resizeOverlay != null) {
            resizeOverlay.getElement().removeFromParent();
        }

        this.resizeOverlay = null;
    }

    private void addResizeOverlay() {
        this.resizeOverlay = new VResizeOverlay(this, getModel(), resizeHandler);
        getElement().appendChild(resizeOverlay.getElement());
    }

    void setWidgetOptions(ComponentOptions componentOptions) {
        moveTo(componentOptions.coordinates);
    }

    void setEditable(boolean editable) {
        if (this.editable != editable) {
            this.editable = editable;

            if (editable) {
                addResizeOverlay();
            } else {
                removeResizeOverlay();
            }

            getElement().setAttribute("editable", String.valueOf(editable));

            setDraggable(editable);
        }
    }

    void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;

            if (active) {
                addStyleName("active");
                setDraggable(true);
            } else {
                removeStyleName("active");
                setDraggable(false);
            }
        }
    }

    public Widget getWidget() {
        return widget;
    }

    DivElement getContentContainer() {
        return contentContainer;
    }

    private static native void setEffectAllowed(DataTransfer dataTransfer, String effectAllowed)
        /*-{
            dataTransfer.effectAllowed = effectAllowed;
        }-*/;
}