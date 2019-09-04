/*
 * Creator:
 * 23.05.18 10:39 Tobias Sagmeister
 *
 * Maintainer:
 * 23.05.18 10:39 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component.resizer.util;

import ch.sagmeister.vaadin.dashboard.client.component.resizer.handle.ResizeSign;

public class ResizeUtil {

    public static ResizeSign computeSignByValues(double startValue, double endValue) {
        if (startValue > endValue) {
            return ResizeSign.NEGATIV;
        } else if (endValue > startValue) {
            return ResizeSign.POSITIV;
        } else {
            return ResizeSign.EQUAL;
        }
    }
}
