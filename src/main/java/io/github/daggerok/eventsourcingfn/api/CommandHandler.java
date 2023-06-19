package io.github.daggerok.eventsourcingfn.api;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface CommandHandler<C extends Command<ID>, S extends State<ID>, ID> extends Function<Consumer<C>, S> {

    default S handle(Consumer<C> modification) {
        return apply(modification);
    }

    default S handle() {
        return apply();
    }

    default S apply() {
        return apply(c -> {});
    }
}
