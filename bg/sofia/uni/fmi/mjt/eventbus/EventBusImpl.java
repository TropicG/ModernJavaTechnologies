package bg.sofia.uni.fmi.mjt.eventbus;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.exception.MissingSubscriptionException;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.Subscriber;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventBusImpl implements EventBus {

    // the key is any type of class
    // the value is a object who extends Subcriber who has extended Event
    private final Map<Class<?>, Set<Subscriber<?>>> allEvents;
    private final List<Event<?>> allOccuredEvents;

    public EventBusImpl() {
        allEvents = new HashMap<>();
        allOccuredEvents = new ArrayList<>();
    }

    @Override
    public <T extends Event<?>> void subscribe(Class<T> eventType, Subscriber<? super T> subscriber) {
        if (eventType == null) {
            throw new IllegalArgumentException();
        }

        if (subscriber == null) {
            throw new IllegalArgumentException();
        }

        if (!allEvents.containsKey(eventType)) {
            allEvents.put(eventType, new HashSet<>());
        }

        allEvents.get(eventType).add(subscriber);
    }

    @Override
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber)
            throws MissingSubscriptionException {
        if (eventType == null) {
            throw new IllegalArgumentException();
        }

        if (subscriber == null) {
            throw new IllegalArgumentException();
        }

        if (!allEvents.containsKey(eventType)) {
            throw new MissingSubscriptionException("The subscriber is missing");
        }

        if (!allEvents.get(eventType).contains(subscriber)) {
            throw new MissingSubscriptionException("The subscriber is missing");
        }

        allEvents.get(eventType).remove(subscriber);
    }

    @Override
    public <T extends Event<?>> void publish(T event) {
        if (event == null) {
            throw new IllegalArgumentException();
        }

        allOccuredEvents.add(event);

        if (!allEvents.containsKey(event.getClass())) {
            return;
        }

        // PLACE COMMENT HERE TO EXPLAIN UNCHECKED CAST
        Set<Subscriber<?>> allSubscribersToEvent = allEvents.get(event.getClass());

        if (allSubscribersToEvent.isEmpty()) {
            return;
        }

        for (Subscriber<?> subscriber : allSubscribersToEvent) {
            Subscriber<T> checkedSubscriber = (Subscriber<T>) subscriber;
            checkedSubscriber.onEvent(event);
        }
    }

    @Override
    public void clear() {
        allEvents.clear();
        allOccuredEvents.clear();
    }

    @Override
    public Collection<? extends Event<?>> getEventLogs(Class<? extends Event<?>> eventType, Instant from,
                                                       Instant to) {

        if (eventType == null) {
            throw new IllegalArgumentException();
        }

        if (from == null) {
            throw new IllegalArgumentException();
        }

        if (to == null) {
            throw new IllegalArgumentException();
        }

        Collection<Event<?>> allOccurredEventsOfType = new ArrayList<>();
        for (Event<?> event : allOccuredEvents) {

            if ((event.getClass() == eventType) &&
                    !event.getTimestamp().isBefore(from) && event.getTimestamp().isBefore(to)) {
                allOccurredEventsOfType.add(event);
            }

        }

        return Collections.unmodifiableCollection(allOccurredEventsOfType);
    }

    @Override
    public <T extends Event<?>> Collection<Subscriber<?>> getSubscribersForEvent(Class<T> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException();
        }

        if (!allEvents.containsKey(eventType)) {
            return Collections.unmodifiableCollection(new ArrayList<>());
        }

        List<Subscriber<?>> allSubscriberForEvent = new ArrayList<>(allEvents.get(eventType));

        return Collections.unmodifiableCollection(allSubscriberForEvent);
    }

}
