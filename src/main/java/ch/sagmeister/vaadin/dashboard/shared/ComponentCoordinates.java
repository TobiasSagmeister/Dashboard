package ch.sagmeister.vaadin.dashboard.shared;

import ch.abacus.java.annotation.Reviewed;

import java.io.Serializable;

@Reviewed("CR-MYABA-16")
public class ComponentCoordinates implements Serializable, Comparable<ComponentCoordinates> {

    public static final ComponentCoordinates ZERO = new ComponentCoordinates(0, 0, 0, 0);

    public ComponentPosition position = new ComponentPosition();
    public ComponentSpan span = new ComponentSpan();

    public ComponentCoordinates() {
    }

    public ComponentCoordinates(int columnIndex, int rowIndex, int columnSpan, int rowSpan) {
        this.position = new ComponentPosition(columnIndex, rowIndex);
        this.span = new ComponentSpan(columnSpan, rowSpan);
    }

    public ComponentCoordinates(ComponentPosition position, ComponentSpan span) {
        this.position = position;
        this.span = span;
    }

    //Copy constructor
    public ComponentCoordinates(ComponentCoordinates coordinates) {
        this.position = new ComponentPosition();
        this.position.columnIndex = coordinates.position.columnIndex;
        this.position.rowIndex = coordinates.position.rowIndex;

        this.span = new ComponentSpan();
        this.span.columnSpan = coordinates.span.columnSpan;
        this.span.rowSpan = coordinates.span.rowSpan;
    }

    public String toString() {
        return "[" + position.toString() + "," + span.toString() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComponentCoordinates)) return false;

        ComponentCoordinates that = (ComponentCoordinates) o;

        if (!position.equals(that.position)) return false;
        return span.equals(that.span);
    }

    @Override
    public int hashCode() {
        return position.hashCode() + span.hashCode();
    }

    @Override
    public int compareTo(ComponentCoordinates other) {
        int area = span.columnSpan * span.rowSpan;
        int areaOther = other.span.columnSpan * other.span.rowSpan;

        if (area > areaOther) {
            return 1;
        } else if (area < areaOther) {
            return -1;
        } else {
            return 0;
        }
    }

    public boolean intersects(ComponentCoordinates dashboardCoordinates) {
        int colIndex = dashboardCoordinates.position.columnIndex;
        int rowIndex = dashboardCoordinates.position.rowIndex;
        int colSpan = dashboardCoordinates.span.columnSpan;
        int rowSpan = dashboardCoordinates.span.rowSpan;

        return (colIndex + colSpan > position.columnIndex &&
                rowIndex + rowSpan > position.rowIndex &&
                colIndex < position.columnIndex + span.columnSpan &&
                rowIndex < position.rowIndex + span.rowSpan);
    }

    public ComponentCoordinates intersection(ComponentCoordinates dashboardCoordinates) {
        int x1 = Math.max(position.columnIndex, dashboardCoordinates.position.columnIndex);
        int y1 = Math.max(position.rowIndex, dashboardCoordinates.position.rowIndex);
        int x2 = Math.min(position.columnIndex + span.columnSpan, dashboardCoordinates.position.columnIndex + dashboardCoordinates.span.columnSpan);
        int y2 = Math.min(position.rowIndex + span.rowSpan, dashboardCoordinates.position.rowIndex + dashboardCoordinates.span.rowSpan);
        return new ComponentCoordinates(x1, y1, x2 - x1, y2 - y1);
    }
}
