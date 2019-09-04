package ch.sagmeister.vaadin.dashboard.client.model.event;

public class RowHeightChangedEvent extends AbstractModelEvent implements ModelChangedEvent {

    private final int oldRowHeight;
    private final int rowHeight;

    public RowHeightChangedEvent(int oldRowHeight, int rowHeight) {
        this.oldRowHeight = oldRowHeight;
        this.rowHeight = rowHeight;
    }

    public int getOldRowHeight() {
        return oldRowHeight;
    }

    public int getRowHeight() {
        return rowHeight;
    }
}