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

    // the key is the reference of the Event class
    // the value is a set of subscribers who are subscribed to the event which is the key for the map
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

        // if the key doesnt exists it is added to the map with set of subscribers
        if (!allEvents.containsKey(eventType)) {
            allEvents.put(eventType, new HashSet<>());
        }

        // adding the subscriber to the set
        // This is allowed, since the set expects object who implements the Subscriber Set
        allEvents.get(eventType).add(subscriber);
    }

    @Override
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber)
            throws MissingSubscriptionException {
        if (eventType == null) {
            throw new IllegalArgumentException("When unsubscribing, null value for eventType is not valid");
        }

        if (subscriber == null) {
            throw new IllegalArgumentException("When unsubscribing, null value for subscriber is not valid");
        }

        // A subscriber cannot be unsubscribed to a key that doesn't exists in the Map
        if (!allEvents.containsKey(eventType)) {
            throw new MissingSubscriptionException("The event is not in the EventBus");
        }

        // If no such subscriber is subscribed to the event, exception is thrown
        if (!allEvents.get(eventType).contains(subscriber)) {
            throw new MissingSubscriptionException("The subscriber is not in the EventBus");
        }

        // unsubscribing the subscriber to the event
        allEvents.get(eventType).remove(subscriber);
    }

    @Override
    public <T extends Event<?>> void publish(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Other subscribers cannot be informed on null event");
        }

        // saving the published event in the history of all events
        allOccuredEvents.add(event);

        // if no such even event exists in the map, the function is terminated
        if (!allEvents.containsKey(event.getClass())) {
            return;
        }

        // This Set will hold the subscribers which are subscribed to the event
        Set<Subscriber<?>> allSubscribersToEvent = allEvents.get(event.getClass());

        // In case the event doesnt have subscribers the function is terminated
        if (allSubscribersToEvent.isEmpty()) {
            return;
        }

        // Every subscriber is informed that they have to respond to event
        for (Subscriber<?> subscriber : allSubscribersToEvent) {

            // Argument: After Type Erasure it will become Subscriber instead of Subscriber<T>
            // Due to Map it is guaranteed that only subscribers who subscribed to T event will be called to respond
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
            throw new IllegalArgumentException("Event type cannot be null when getting event logs");
        }

        if (from == null) {
            throw new IllegalArgumentException("Time for 'from' argument cannot be null");
        }

        if (to == null) {
            throw new IllegalArgumentException("Time for 'to' argument cannot be null");
        }

        // storing all the events that happend in publish() in the period [from, to), equal to eventType
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
            throw new IllegalArgumentException("eventType as null is invalid argument");
        }

        // if no such event exists in the Map, empty ArrayList is returned
        if (!allEvents.containsKey(eventType)) {
            return Collections.unmodifiableCollection(new ArrayList<>());
        }

        return Collections.unmodifiableCollection(allEvents.get(eventType));
    }

}
