package com.github.rahulsom.grooves.api.snapshots

import com.github.rahulsom.grooves.api.AggregateType
import com.github.rahulsom.grooves.api.events.BaseEvent
import com.github.rahulsom.grooves.api.snapshots.internal.BaseSnapshot

/**
 * Marks a class as a versioned snapshot.
 *
 * @param [AggregateIdT] The type of [AggregateT.id]
 * @param [AggregateT]   The Aggregate this snapshot works over
 * @param [SnapshotIdT]  The type for [BaseSnapshot.id]
 * @param [EventIdT]     The type for [EventT.id]
 * @param [EventT]       The base type for events that apply to [AggregateT]
 *
 * @author Rahul Somasunderam
 */
interface VersionedSnapshot<
        AggregateIdT,
        AggregateT : AggregateType<AggregateIdT>,
        SnapshotIdT,
        EventIdT,
        in EventT : BaseEvent<AggregateIdT, AggregateT, EventIdT, in EventT>> :
        BaseSnapshot<AggregateIdT, AggregateT, SnapshotIdT, EventIdT, EventT> {

    /**
     * The position of the last event that this snapshot represents.
     *
     * This is useful in finding a matching or suitable snapshot based on the version of a snapshot.
     */
    var lastEventPosition: Long?

    override fun setLastEvent(event: EventT) {
        this.lastEventPosition = event.position
    }
}