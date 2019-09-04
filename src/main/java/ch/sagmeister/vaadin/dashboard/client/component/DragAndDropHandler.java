package ch.sagmeister.vaadin.dashboard.client.component;

import elemental.events.Event;

public interface DragAndDropHandler {

    void onDragStart(Event event);

    void onDragEnter(Event event);

    void onDragOver(Event event);

    void onDragLeave(Event event);

    void onDrop(Event event);

    void onDragEnd(Event event);
}