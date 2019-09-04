package ch.sagmeister.vaadin.dashboard.client.model.event;

public class RowsChangedEvent extends AbstractModelEvent implements ModelChangedEvent {

    private int oldRows;
    private int rows;

    public RowsChangedEvent(int oldRows, int rows) {
        this.oldRows = oldRows;
        this.rows = rows;
    }

    public int getOldRows() {
        return oldRows;
    }

    public int getRows() {
        return rows;
    }
}