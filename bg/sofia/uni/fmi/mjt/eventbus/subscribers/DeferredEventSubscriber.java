package bg.sofia.uni.fmi.mjt.eventbus.subscribers;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class DeferredEventSubscriber<T extends Event<?>> implements Subscriber<T>, Iterable<T> {

    List<T> allSavedEvents;

    public DeferredEventSubscriber() {
        allSavedEvents = new ArrayList<>();
    }

    /**
     * Store an event for processing at a later time.
     *
     * @param event the event to be processed
     * @throws IllegalArgumentException if the event is null
     */
    @Override
    public void onEvent(T event) {
        if (event == null) {
            throw new IllegalArgumentException();
        }

        allSavedEvents.add(event);
    }

    /**
     * Get an iterator for the unprocessed events. The iterator should provide the events sorted
     * by priority, with higher-priority events first (lower priority number = higher priority).
     * For events with equal priority, earlier events (by timestamp) come first.
     *
     * @return an iterator for the unprocessed events
     */
    @Override
    public Iterator<T> iterator() {

        Comparator<Event<?>> sortedByPriority = new Comparator<Event<?>>() {
            @Override
            public int compare(Event<?> o1, Event<?> o2) {

                int comparisonByPriority = Integer.compare(o1.getPriority(), o2.getPriority());
                if (comparisonByPriority == 0) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }

                return comparisonByPriority;
            }
        };

        Queue<T> events = new PriorityQueue<>(sortedByPriority);
        events.addAll(allSavedEvents);

        return events.iterator();
    }

    /**
     * Check if there are unprocessed events.
     *
     * @return true if there are unprocessed events, false otherwise
     */
    public boolean isEmpty() {
        return allSavedEvents.isEmpty();
    }
}