package ch.sagmeister.vaadin.dashboard.client.component;

import ch.abacus.java.annotation.Reviewed;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.vaadin.client.WidgetUtil;
import elemental.events.Event;
import elemental.events.EventRemover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Reviewed("CR-MYABA-15")
public final class DragHandler {

    private static final Set<DragListener> DRAG_LISTENERS = new HashSet<>();

    private static VComponentWrapper DRAG_SOURCE = null;
    private static boolean DROPPED = false;
    private static int RELATIVE_X = -1;
    private static int RELATIVE_Y = -1;

    public static void dragStart(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;

        VComponentWrapper dragSource = WidgetUtil.findWidget((Element) event.getTarget(), VComponentWrapper.class);
        int x = WidgetUtil.getRelativeX(dragSource.getElement(), nativeEvent);
        int y = WidgetUtil.getRelativeY(dragSource.getElement(), nativeEvent);

        DRAG_SOURCE = dragSource;
        RELATIVE_X = x;
        RELATIVE_Y = y;

        for (DragListener dragListener : DRAG_LISTENERS) {
            dragListener.onDragStart(event);
        }
    }

    public static void dragEnd(Event event) {
        DRAG_SOURCE = null;
        RELATIVE_X = -1;
        RELATIVE_Y = -1;

        for (DragListener dragListener : new ArrayList<>(DRAG_LISTENERS)) {
            dragListener.onDragEnd(event);
        }

        DROPPED = false;
    }

    public static VComponentWrapper getDragSource() {
        return DRAG_SOURCE;
    }

    public static int getRelativeX() {
        return RELATIVE_X;
    }

    public static int getRelativeY() {
        return RELATIVE_Y;
    }

    public static boolean isDragging() {
        return DRAG_SOURCE != null;
    }

    public static boolean isDropped() {
        return DROPPED;
    }

    public static EventRemover addDragListener(DragListener dragListener) {
        DRAG_LISTENERS.add(dragListener);
        return () -> DRAG_LISTENERS.remove(dragListener);
    }

    public static void dropped() {
        DROPPED = true;
        DRAG_SOURCE = null;
        RELATIVE_X = -1;
        RELATIVE_Y = -1;
    }

    public interface DragListener {

        void onDragStart(Event event);

        void onDragEnd(Event event);
    }
}