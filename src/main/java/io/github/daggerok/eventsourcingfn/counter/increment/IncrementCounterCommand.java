package io.github.daggerok.eventsourcingfn.counter.increment;

import io.github.daggerok.eventsourcingfn.api.Command;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IncrementCounterCommand implements Command<IncrementCounterCommand> {
    String name;
}
