package ch.sagmeister.vaadin.dashboard.shared;

import java.io.Serializable;

public class ComponentOptions implements Serializable {

    public ComponentCoordinates coordinates = new ComponentCoordinates();

    public static ComponentOptions of(ComponentCoordinates coordinates) {
        ComponentOptions componentOptions = new ComponentOptions();

        componentOptions.coordinates.position.columnIndex = coordinates.position.columnIndex;
        componentOptions.coordinates.position.rowIndex = coordinates.position.rowIndex;
        componentOptions.coordinates.span.columnSpan = coordinates.span.columnSpan;
        componentOptions.coordinates.span.rowSpan = coordinates.span.rowSpan;

        return componentOptions;
    }
}