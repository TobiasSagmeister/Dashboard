package ch.sagmeister.vaadin.dashboard.client.component.resizer.handle;

public interface ResizeListener {

    void onResizeStart();

    void onResize(ResizeData resizeData);

    void onResizeEnd();
}