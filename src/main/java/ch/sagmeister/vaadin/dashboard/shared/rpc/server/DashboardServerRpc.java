/*
 * Creator:
 * 16.05.18 11:57 Tobias Sagmeister
 *
 * Maintainer:
 * 16.05.18 11:57 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.shared.rpc.server;

import ch.sagmeister.vaadin.dashboard.shared.ComponentCoordinates;
import com.vaadin.shared.Connector;
import com.vaadin.shared.communication.ServerRpc;

import java.io.Serializable;
import java.util.List;

public interface DashboardServerRpc extends ServerRpc {

    void commit(List<WidgetChangedEvent> widgetChangedEvents);

    final class WidgetChangedEvent implements Serializable {

        public Type type;
        public Connector connector;
        public ComponentCoordinates coordinates;

        public WidgetChangedEvent() {
        }

        public enum Type {
            ADD,
            REMOVE,
            MOVE
        }
    }
}
