# event-sourcing-fn [![tests](https://github.com/daggerok/event-sourcing-fn/actions/workflows/tests.yml/badge.svg)](https://github.com/daggerok/event-sourcing-fn/actions/workflows/tests.yml)
Status: IN PROGRESS. This repository contains some thoughts regarding functional event-sourcing, DDD and CQRS. Please ignore

## idea

Whole idea around functional event-sourcing, ddd and cqrs is using of functions.

For example, applying command or event on top of aggregate means changing of that
aggregate representation state.

Let's assume we have "counterState" domain:
`CounterState` data object, which is holds "counterState" aggregate state:

```java
@Data
@Accessors(chain = true)
public class CounterState implements State<CounterState> {
    UUID value;
    int initialValue;
}
```

`CreateCounterCommand` chain builder (wither) to create / patch a CounterState:

```java

@Data
@Accessors(chain = true)
public class CreateCounterCommand implements Command<CreateCounterCommand> {
    String name = UUID.randomUUID().toString();
    int initialValue = 0;
}
```

and `CreateCounterCommandHandler` to handle `Counter` creation:

```java

@Component
public class CreateCounterCommandHandler implements CommandHandler<CreateCounterCommand, CounterState> {

    @Override
    public CounterState apply(Consumer<CreateCounterCommand> creation) {
        CreateCounterCommand command = new CreateCounterCommand();
        creation.accept(command);

        return new CounterState()
                .setInitialValue(command.getInitialValue())
                .setValue(UUID.fromString(command.getName()));
    }
}
```

Where:

`Command` is simple marker interface:

```java
public interface Command<T> { }
```

`State` is also marker interface:

```java
public interface State<T> { }
```

and `CommandHandler` is functional interface, which is implements regular java
functional interface, which is transforms command mutation to a state:

```java
@FunctionalInterface
public interface CommandHandler<C extends Command<C>, S extends State<S>> extends Function<Consumer<C>, S> {

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
```

Next steps:
* implement repository
* store mutated state in command handler

<!--

# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.0/maven-plugin/reference/html/#build-image)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.1.0/reference/htmlsingle/#web.reactive)
* [Spring Data R2DBC](https://docs.spring.io/spring-boot/docs/3.1.0/reference/htmlsingle/#data.sql.r2dbc)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/3.1.0/reference/htmlsingle/#appendix.configuration-metadata.annotation-processor)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing data with R2DBC](https://spring.io/guides/gs/accessing-data-r2dbc/)

### Additional Links

These additional references should also help you:

* [R2DBC Homepage](https://r2dbc.io)

-->
