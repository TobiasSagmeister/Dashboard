/*
 * Creator:
 * 14.05.18 13:44 Tobias Sagmeister
 *
 * Maintainer:
 * 14.05.18 13:44 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.shared;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.AbstractComponentContainerState;

import java.util.HashMap;
import java.util.Map;

public class DashboardState extends AbstractComponentContainerState {

    public int columns = 12;
    public int rowHeight = 100;
    public boolean editable = true;

    public Map<Connector, ComponentOptions> componentOptions = new HashMap<>();
}
