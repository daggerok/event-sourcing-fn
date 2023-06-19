package io.github.daggerok.eventsourcingfn.counter;

import io.github.daggerok.eventsourcingfn.api.State;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CounterState implements State<UUID> {
    UUID aggregateId = UUID.randomUUID();
    int initialValue;
}
