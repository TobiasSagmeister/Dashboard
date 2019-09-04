/*
 * Creator:
 * 22.05.18 15:49 Tobias Sagmeister
 *
 * Maintainer:
 * 22.05.18 15:49 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component.resizer.handle;

import ch.sagmeister.vaadin.dashboard.client.component.VComponentWrapper;
import ch.sagmeister.vaadin.dashboard.client.component.util.Location;
import ch.sagmeister.vaadin.dashboard.client.model.DashboardModel;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.Widget;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventTarget;

import java.util.ArrayList;
import java.util.List;

public abstract class VAbstractResizeHandle extends Widget {

    private final VComponentWrapper widgetWrapper;
    private final DashboardModel model;
    private final Location location;

    private ResizeStartArgs resizeStartArgs;
    private List<ResizeListener> resizeListeners = new ArrayList<>();
    private EventListener resizeMouseMoveListener = this::onResize;
    private EventListener resizeMouseUpListener = this::onResizeEnd;

    public VAbstractResizeHandle(VComponentWrapper widgetWrapper, DashboardModel model, Location location) {
        this.widgetWrapper = widgetWrapper;
        this.model = model;
        this.location = location;

        setElement(Document.get().createDivElement());
        setStyleName("resize-handle");
        addStyleName("resize-handle-" + location.name().toLowerCase());

        EventTarget resizerTarget = getElement().cast();
        resizerTarget.addEventListener(Event.MOUSEDOWN, this::onResizeStart, false);
    }

    private void onResizeStart(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;

        addStyleName("resizing");

        int clientX = nativeEvent.getClientX();
        int clientY = nativeEvent.getClientY();

        this.resizeStartArgs = new ResizeStartArgs(clientX, clientY, (int) widgetWrapper.getComputedWidth(), (int) widgetWrapper.getComputedHeight());

        EventTarget documentEventTarget = Document.get().getDocumentElement().cast();
        documentEventTarget.addEventListener(Event.MOUSEMOVE, resizeMouseMoveListener);
        documentEventTarget.addEventListener(Event.MOUSEUP, resizeMouseUpListener);

        event.stopPropagation();
        event.preventDefault();

        fireResizeStart();
    }

    private void fireResizeStart() {
        for (ResizeListener resizeListener : resizeListeners) {
            resizeListener.onResizeStart();
        }
    }

    private void onResize(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;

        int clientX = nativeEvent.getClientX();
        int clientY = nativeEvent.getClientY();

        ResizeArgs resizeArgs = new ResizeArgs(clientX - resizeStartArgs.startX, clientY - resizeStartArgs.startY, resizeStartArgs.startWidth, resizeStartArgs.startHeight);
        ResizeData resizeData = convertToResizeData(resizeArgs);
        fireResize(resizeData);

        event.stopPropagation();
        event.preventDefault();
    }

    protected Location getLocation() {
        return location;
    }

    protected abstract ResizeData convertToResizeData(ResizeArgs resizeArgs);

    private void fireResize(ResizeData resizeData) {
        for (ResizeListener resizeListener : resizeListeners) {
            resizeListener.onResize(resizeData);
        }
    }

    private void onResizeEnd(Event event) {
        removeStyleName("resizing");

        EventTarget documentEventTarget = Document.get().getDocumentElement().cast();
        documentEventTarget.removeEventListener(Event.MOUSEMOVE, resizeMouseMoveListener);
        documentEventTarget.removeEventListener(Event.MOUSEUP, resizeMouseUpListener);

        fireResizeEnd();

        event.stopPropagation();
        event.preventDefault();
    }

    private void fireResizeEnd() {
        for (ResizeListener resizeListener : resizeListeners) {
            resizeListener.onResizeEnd();
        }
    }

    protected VComponentWrapper getWidgetWrapper() {
        return widgetWrapper;
    }

    protected DashboardModel getModel() {
        return model;
    }

    public void addResizeListener(ResizeListener resizeListener) {
        resizeListeners.add(resizeListener);
    }

    private class ResizeStartArgs {

        public final int startX;
        public final int startY;

        public final int startWidth;
        public final int startHeight;

        public ResizeStartArgs(int startX, int startY, int startWidth, int startHeight) {
            this.startX = startX;
            this.startY = startY;
            this.startWidth = startWidth;
            this.startHeight = startHeight;
        }
    }
}
