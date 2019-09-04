package ch.sagmeister.vaadin.dashboard.client.model.event;

import java.util.List;

public class ModelCommitEvent extends AbstractModelEvent {

    private final List<ModelChangedEvent> modelChangedEvents;

    public ModelCommitEvent(List<ModelChangedEvent> modelChangedEvents) {
        this.modelChangedEvents = modelChangedEvents;
    }

    public List<ModelChangedEvent> getModelChangedEvents() {
        return modelChangedEvents;
    }
}