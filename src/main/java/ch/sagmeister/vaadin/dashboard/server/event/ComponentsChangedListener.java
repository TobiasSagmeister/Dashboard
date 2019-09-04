package ch.sagmeister.vaadin.dashboard.server.event;

import com.vaadin.event.SerializableEventListener;

import java.lang.reflect.Method;

@FunctionalInterface
public interface ComponentsChangedListener extends SerializableEventListener {

    Method ON_COMPONENTS_CHANGED_METHOD = ComponentsChangedListener.class.getDeclaredMethods()[0];

    void onComponentsChanged(ComponentsChangedEvent event);
}