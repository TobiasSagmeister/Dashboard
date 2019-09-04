package ch.sagmeister.vaadin.dashboard.client.model.event;

public class ColumnsChangedEvent extends AbstractModelEvent implements ModelChangedEvent {

    private final int oldColumns;
    private int columns;

    public ColumnsChangedEvent(int oldColumns, int columns) {
        this.oldColumns = oldColumns;
        this.columns = columns;
    }

    public int getOldColumns() {
        return oldColumns;
    }

    public int getColumns() {
        return columns;
    }
}