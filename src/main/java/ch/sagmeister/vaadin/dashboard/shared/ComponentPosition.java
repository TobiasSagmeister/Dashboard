/*
 * Creator:
 * 15.05.18 11:07 Tobias Sagmeister
 *
 * Maintainer:
 * 15.05.18 11:07 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.shared;

import java.io.Serializable;

public class ComponentPosition implements Serializable {

    public int columnIndex;
    public int rowIndex;

    public ComponentPosition() {
    }

    public ComponentPosition(int columnIndex, int rowIndex) {
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
    }
    public String toString() {
        return "[" + columnIndex + "," + rowIndex + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComponentPosition)) return false;

        ComponentPosition that = (ComponentPosition) o;

        if (columnIndex != that.columnIndex) return false;
        return rowIndex == that.rowIndex;
    }

    @Override
    public int hashCode() {
        int result = columnIndex;
        result = 31 * result + rowIndex;
        return result;
    }
}
