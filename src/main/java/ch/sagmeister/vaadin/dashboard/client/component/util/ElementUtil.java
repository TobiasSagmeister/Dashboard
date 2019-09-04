/*
 * Creator:
 * 09.07.18 14:54 Tobias Sagmeister
 *
 * Maintainer:
 * 09.07.18 14:54 Tobias Sagmeister
 *
 * Copyright (c) 2000 - 2018 ABACUS Research AG, All Rights Reserved
 */
package ch.sagmeister.vaadin.dashboard.client.component.util;

import com.google.gwt.dom.client.Element;

public class ElementUtil {

    public static float getComputedWidth(Element element) {
        String value = getComputedStyle(element, "width");
        return parseFloat(value);
    }

    public static float getComputedHeight(Element element) {
        String value = getComputedStyle(element, "height");
        return parseFloat(value);
    }

    public static native String getComputedStyle(Element el, String p)
        /*-{
            try {

                if (el.currentStyle) {
                    // IE
                    return el.currentStyle[p];
                } else if (window.getComputedStyle) {
                    // Sa, FF, Opera
                    var view = el.ownerDocument.defaultView;
                    return view.getComputedStyle(el, null).getPropertyValue(p);
                } else {
                    // fall back for non IE, Sa, FF, Opera
                    return "";
                }
            } catch (e) {
                return "";
            }

        }-*/;

    public static native float parseFloat(String value)
        /*-{
            return $wnd.parseFloat(value, 10);

        }-*/;

    public static native float getX(Element element)
        /*-{
            return element.getBoundingClientRect().x;
        }-*/;

    public static native float getY(Element element)
        /*-{
            return element.getBoundingClientRect().y;
        }-*/;
}
