package io.github.daggerok.eventsourcingfn.api;

public interface Identity<ID> {
    ID getAggregateId();
}
