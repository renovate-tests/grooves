package com.github.rahulsom.grooves.grails

import com.github.rahulsom.grooves.api.AggregateType
import com.github.rahulsom.grooves.api.internal.BaseEvent
import com.github.rahulsom.grooves.queries.QueryUtil
import com.github.rahulsom.grooves.api.Snapshot
import org.grails.datastore.gorm.GormEntity

import static org.codehaus.groovy.runtime.InvokerHelper.invokeStaticMethod

/**
 * Gorm Support for Query Util. <br/>
 * This is the preferred way of writing Grooves applications with Grails.
 *
 * @param <A> The Aggregate type
 * @param <E> The Event type
 * @param <S> The snapshot type
 *
 * @author Rahul Somasunderam
 */
abstract class GormQueryUtil<
        A extends AggregateType & GormEntity<A>,
        E extends BaseEvent<A, E> & GormEntity<E>,
        S extends Snapshot<A, ?> & GormEntity<S>> implements QueryUtil<A, E, S> {

    final Class<A> aggregateClass
    final Class<E> eventClass
    final Class<S> snapshotClass

    GormQueryUtil(Class<A> aggregateClass, Class<E> eventClass, Class<S> snapshotClass) {
        this.aggregateClass = aggregateClass
        this.eventClass = eventClass
        this.snapshotClass = snapshotClass
    }

    public static final Map LATEST = [sort: 'lastEventPosition', order: 'desc', offset: 0, max: 1]
    public static final Map INCREMENTAL = [sort: 'position', order: 'asc']

    @Override
    final Optional<S> getSnapshot(long startWithEvent, A aggregate) {
        def snapshots = startWithEvent == Long.MAX_VALUE ?
                invokeStaticMethod(snapshotClass, 'findAllByAggregateId',
                        [aggregate.id, LATEST].toArray()) :
                invokeStaticMethod(snapshotClass, 'findAllByAggregateIdAndLastEventPositionLessThan',
                        [aggregate.id, startWithEvent, LATEST].toArray())
        (snapshots ? Optional.of(snapshots[0]) : Optional.empty()) as Optional<S>
    }

    @Override
    final Optional<S> getSnapshot(Date startAtTime, A aggregate) {
        def snapshots = startAtTime == null ?
                invokeStaticMethod(snapshotClass, 'findAllByAggregateId',
                        [aggregate.id, LATEST].toArray()) :
                invokeStaticMethod(snapshotClass, 'findAllByAggregateIdAndLastEventTimestampLessThan',
                        [aggregate.id, startAtTime, LATEST].toArray())
        (snapshots ? Optional.of(snapshots[0]) : Optional.empty()) as Optional<S>
    }

    @Override
    final void detachSnapshot(S retval) {
        if (retval.isAttached()) {
            retval.discard()
            retval.id = null
        }
    }

    @Override
    final List<E> getUncomputedEvents(A aggregate, S lastSnapshot, long version) {
        invokeStaticMethod(eventClass, 'findAllByAggregateAndPositionGreaterThanAndPositionLessThanEquals',
                [aggregate, lastSnapshot?.lastEventPosition ?: 0L, version, INCREMENTAL].toArray()) as List<E>
    }

    @Override
    final List<E> getUncomputedEvents(A aggregate, S lastSnapshot, Date snapshotTime) {
        lastSnapshot.lastEventTimestamp ?
                invokeStaticMethod(eventClass, 'findAllByAggregateAndTimestampGreaterThanAndTimestampLessThanEquals',
                        [aggregate, lastSnapshot.lastEventTimestamp, snapshotTime, INCREMENTAL].toArray()) as List<E> :
                invokeStaticMethod(eventClass, 'findAllByAggregateAndTimestampLessThanEquals',
                        [aggregate, snapshotTime, INCREMENTAL].toArray()) as List<E>
    }

    @Override
    final List<E> findEventsForAggregates(List<A> aggregates) {
        invokeStaticMethod(eventClass, 'findAllByAggregateInList', [aggregates, INCREMENTAL].toArray()) as List<? extends E>
    }

}