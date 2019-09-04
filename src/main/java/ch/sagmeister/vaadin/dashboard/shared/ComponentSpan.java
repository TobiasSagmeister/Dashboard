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

public class ComponentSpan implements Serializable {

    public int columnSpan;
    public int rowSpan;

    public ComponentSpan() {
    }

    public ComponentSpan(int columnSpan, int rowSpan) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
    }

    public String toString() {
        return "[" + columnSpan + "," + rowSpan + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComponentSpan)) return false;

        ComponentSpan that = (ComponentSpan) o;

        if (columnSpan != that.columnSpan) return false;
        return rowSpan == that.rowSpan;
    }

    @Override
    public int hashCode() {
        int result = columnSpan;
        result = 31 * result + rowSpan;
        return result;
    }
}
