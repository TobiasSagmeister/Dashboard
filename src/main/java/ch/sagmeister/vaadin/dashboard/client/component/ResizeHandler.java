/*
 * Creator:
 * 07.11.18 10:04 Tobias Sagmeister
 *
 * Maintainer:
 * 07.11.18 10:04 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component;

import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;

public interface ResizeHandler {

    void onResizeStart(VComponentWrapper resizeSource);

    void onResize(ResizeCoordinates resizeData);

    void onResizeEnd();

    void addResizeListener(ResizeListener listener);

    interface ResizeListener {

        void onResized();
    }

    class ResizeCoordinates {

        private final ComponentCoordinates coordinates;
        private final ComponentAbsoluteCoordinates absoluteCoordinates;

        public ResizeCoordinates(ComponentCoordinates coordinates, ComponentAbsoluteCoordinates absoluteCoordinates) {
            this.coordinates = coordinates;
            this.absoluteCoordinates = absoluteCoordinates;
        }

        public ComponentCoordinates getCoordinates() {
            return coordinates;
        }

        public ComponentAbsoluteCoordinates getAbsoluteCoordinates() {
            return absoluteCoordinates;
        }
    }
}
