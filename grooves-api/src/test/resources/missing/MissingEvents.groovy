package missing

import com.github.rahulsom.grooves.api.snapshots.Snapshot
import com.github.rahulsom.grooves.transformations.*
import com.github.rahulsom.grooves.api.*
import com.github.rahulsom.grooves.api.events.BaseEvent
import com.github.rahulsom.grooves.api.events.RevertEvent
import com.github.rahulsom.grooves.queries.QuerySupport
import groovy.transform.CompileStatic
import rx.Observable

@CompileStatic @Aggregate class Account implements AggregateType<Long> {
    Long id
}

@CompileStatic abstract class Transaction implements BaseEvent<Account, Long, Transaction> {
    Account aggregate
    RevertEvent<Account, Long, Transaction> revertedBy
    Long id, position
    Date timestamp
    String createdBy, audit
}

@CompileStatic @Event(Account) class CashDeposit extends Transaction {}

@CompileStatic @Event(Account) class CashWithdrawal extends Transaction {}

@CompileStatic class Balance implements Snapshot<Account, Long, Long, Transaction> {
    Long id
    Long lastEventPosition
    Date lastEventTimestamp
    Account aggregate, deprecatedBy
    Set<Account> deprecates
}

@CompileStatic @Query(aggregate = Account, snapshot = Balance)
class BalanceQuery implements QuerySupport<Account, Long, Transaction, Long, Balance> {
    @Override Balance createEmptySnapshot() { null }
    @Override Observable<Balance> getSnapshot(long maxPosition, Account aggregate) { Observable.empty() }
    @Override Observable<Balance> getSnapshot(Date maxTimestamp, Account aggregate) { Observable.empty() }
    @Override void detachSnapshot(Balance snapshot) {}
    @Override Observable<Transaction> getUncomputedEvents(Account aggregate, Balance lastSnapshot, long version) { Observable.empty() }
    @Override Observable<Transaction> getUncomputedEvents(Account aggregate, Balance lastSnapshot, Date snapshotTime) { Observable.empty() }
    @Override boolean shouldEventsBeApplied(Balance snapshot) { true }
    @Override Observable<Transaction> findEventsForAggregates(List<Account> aggregates) { Observable.empty() }
    @Override void addToDeprecates(Balance snapshot, Account deprecatedAggregate) {}
    @Override Transaction unwrapIfProxy(Transaction event) { event }
    @Override EventApplyOutcome onException(Exception e, Balance snapshot, Transaction event) {null}
}

new BalanceQuery().computeSnapshot(new Account(), 0)
