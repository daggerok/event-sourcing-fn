package io.github.daggerok.eventsourcingfn.counter.create;

import io.github.daggerok.eventsourcingfn.api.Command;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCounterCommand implements Command<UUID> {
    UUID aggregateId = UUID.randomUUID();
    int initialValue = 0;
}
