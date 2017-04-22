package com.github.rahulsom.grooves.queries.internal;

import com.github.rahulsom.grooves.api.AggregateType;
import com.github.rahulsom.grooves.api.events.BaseEvent;
import com.github.rahulsom.grooves.api.events.Deprecates;
import com.github.rahulsom.grooves.api.snapshots.internal.BaseSnapshot;
import rx.Observable;

import java.util.List;

/**
 * Executes a query by controlling how events are applied
 *
 * @param <Aggregate>      The type of Aggregate
 * @param <EventIdType>    The type of Event Id
 * @param <EventType>      The type of Event
 * @param <SnapshotIdType> The type of Snapshot Id
 * @param <SnapshotType>   The type of Snapshot
 * @author Rahul Somasunderam
 */
public interface Executor<
        Aggregate extends AggregateType,
        EventIdType,
        EventType extends BaseEvent<Aggregate, EventIdType, EventType>,
        SnapshotIdType,
        SnapshotType extends BaseSnapshot<Aggregate, SnapshotIdType, EventIdType, EventType>
        > {
    /**
     * Applies reverts to a list of events and then returns forward events
     *
     * @param events The list of events
     * @return a list of events after the reverts have been applied
     */
    Observable<EventType> applyReverts(Observable<EventType> events);

    /**
     * Applies forward events on a snapshot
     *
     * @param query          The query that demands the events to be applied
     * @param snapshot       The snapshot to be mutated
     * @param events         The list of forward events
     * @param deprecatesList The list of Deprecate events
     * @param aggregates     The list of deprecated aggregates
     * @return The Snapshot that has been mutated
     */
    Observable<SnapshotType> applyEvents(
            BaseQuery<Aggregate, EventIdType, EventType, SnapshotIdType, SnapshotType> query,
            SnapshotType snapshot,
            Observable<EventType> events,
            List<Deprecates<Aggregate, EventIdType, EventType>> deprecatesList,
            List<Aggregate> aggregates);
}
