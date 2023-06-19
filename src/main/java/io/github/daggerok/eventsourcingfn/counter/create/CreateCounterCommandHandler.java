package io.github.daggerok.eventsourcingfn.counter.create;

import io.github.daggerok.eventsourcingfn.api.CommandHandler;
import io.github.daggerok.eventsourcingfn.counter.CounterState;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class CreateCounterCommandHandler implements CommandHandler<CreateCounterCommand, CounterState, UUID> {

    @Override
    public CounterState apply(Consumer<CreateCounterCommand> creation) {
        CreateCounterCommand command = new CreateCounterCommand();
        creation.accept(command);

        return new CounterState()
                .setInitialValue(command.getInitialValue())
                .setAggregateId(command.getAggregateId());
    }
}
