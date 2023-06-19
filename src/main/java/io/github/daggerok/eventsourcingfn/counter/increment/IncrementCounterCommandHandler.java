package io.github.daggerok.eventsourcingfn.counter.increment;

import io.github.daggerok.eventsourcingfn.api.CommandHandler;
import io.github.daggerok.eventsourcingfn.counter.CounterState;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class IncrementCounterCommandHandler implements CommandHandler<IncrementCounterCommand, CounterState, UUID> {

    @Override
    public CounterState apply(Consumer<IncrementCounterCommand> mutation) {
        IncrementCounterCommand command = new IncrementCounterCommand();
        mutation.accept(command);

        CounterState state = new CounterState(); // TODO:
        // FIXME: fetch state by aggregate id using repository
        UUID value = state.getAggregateId();
        CounterState updated = state.setAggregateId(value).setInitialValue(state.getInitialValue() + 1); // TODO:
        // FIXME: save updated state using repository
        return updated;
    }
}
