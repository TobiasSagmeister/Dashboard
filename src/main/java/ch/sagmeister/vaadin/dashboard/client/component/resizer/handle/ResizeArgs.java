package ch.sagmeister.vaadin.dashboard.client.component.resizer.handle;

public class ResizeArgs {

    public final int deltaX;
    public final int deltaY;
    public final int startWidth;
    public final int startHeight;

    public ResizeArgs(int deltaX, int deltaY, int startWidth, int startHeight) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.startWidth = startWidth;
        this.startHeight = startHeight;
    }
}