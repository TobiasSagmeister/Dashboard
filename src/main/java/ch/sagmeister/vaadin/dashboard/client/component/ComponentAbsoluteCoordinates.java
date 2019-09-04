/*
 * Creator:
 * 07.11.18 14:21 Tobias Sagmeister
 *
 * Maintainer:
 * 07.11.18 14:21 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component;

public class ComponentAbsoluteCoordinates {

    public final double pxTop;
    public final double pctLeft;
    public final double pctWidth;
    public final double pxHeight;

    public ComponentAbsoluteCoordinates(double pxTop, double pctLeft, double pctWidth, double pxHeight) {
        this.pxTop = pxTop;
        this.pctLeft = pctLeft;
        this.pctWidth = pctWidth;
        this.pxHeight = pxHeight;
    }
}
