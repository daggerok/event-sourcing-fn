package io.github.daggerok.eventsourcingfn.counter.create;

import io.github.daggerok.eventsourcingfn.api.Command;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCounterCommand implements Command<CreateCounterCommand> {
    String name = UUID.randomUUID().toString();
    int initialValue = 0;
}
