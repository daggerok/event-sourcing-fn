package io.github.daggerok.eventsourcingfn;

import io.github.daggerok.eventsourcingfn.counter.CounterState;
import io.github.daggerok.eventsourcingfn.counter.create.CreateCounterCommand;
import io.github.daggerok.eventsourcingfn.counter.create.CreateCounterCommandHandler;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
class CreateCounter implements Function<Consumer<CreateCounterCommand>, CounterState> {

    @Override
    public CounterState apply(Consumer<CreateCounterCommand> counter) {
        CreateCounterCommand createCounterCommand = new CreateCounterCommand().setName(UUID.randomUUID().toString());
        log.info("default command: {}", createCounterCommand);
        counter.accept(createCounterCommand);
        log.info("patched command: {}", createCounterCommand);
        return new CounterState().setValue(UUID.fromString(createCounterCommand.getName()))
                .setInitialValue(createCounterCommand.getInitialValue());
    }
}

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventSourcingFnApplicationTests {

    WebTestClient webTestClient;
    CreateCounterCommandHandler createCounterCommandHandler;

    @Test
    void should_create_counter_with_default_initial_value() {
        // given
        var requestBody = new CreateCounterCommand().setName("0-0-0-0-1");

        // when
        webTestClient.post().uri("/counter/create")
                .bodyValue(requestBody)
                .exchange()

                // then
                .expectBody(new ParameterizedTypeReference<CounterState>() {
                })
                .consumeWith(counterIdEntityExchangeResult -> {
                    log.info("counterIdEntityExchangeResult: {}", counterIdEntityExchangeResult);
                    var counterId = counterIdEntityExchangeResult.getResponseBody();
                    assertThat(counterId).isNotNull();
                    assertThat(counterId.getValue()).isEqualTo(UUID.fromString("0-0-0-0-1"));
                    assertThat(counterId.getInitialValue()).isEqualTo(0);
                });
    }

    @Test
    void should_create_counter_with_initial_value_123() {
        // given
        var request = new CreateCounterCommand().setName("0-0-0-0-2").setInitialValue(123);

        // when
        webTestClient.post().uri("/counter/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()

                // then
                .expectBody(new ParameterizedTypeReference<CounterState>() {
                })
                .consumeWith(counterIdEntityExchangeResult -> {
                    log.info("counterIdEntityExchangeResult: {}", counterIdEntityExchangeResult);
                    var counterId = counterIdEntityExchangeResult.getResponseBody();
                    assertThat(counterId).isNotNull();
                    assertThat(counterId.getValue()).isEqualTo(UUID.fromString("0-0-0-0-2"));
                    assertThat(counterId.getInitialValue()).isEqualTo(123);
                });
    }

    @Test
    void should_create_modifiable_complete_custom_counter_using_spring() {
        // when
        CounterState counterState = createCounterCommandHandler.apply(command ->
                command.setName("0-0-0-0-5")
                        .setInitialValue(123)
        );
        log.info("modifiable complete custom counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(123);
        assertThat(counterState.getValue()).isEqualTo(UUID.fromString("0-0-0-0-5"));
    }

    @Test
    void should_create_modifiable_partial_custom_counter_using_spring() {
        // when
        CounterState counterState = createCounterCommandHandler.apply(command -> command.setName("0-0-0-0-4"));
        log.info("modifiable partial custom counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(0);
        assertThat(counterState.getValue()).isEqualTo(UUID.fromString("0-0-0-0-4"));
    }

    @Test
    void should_create_modifiable_default_empty_counter_using_spring_handle() {
        // when
        CounterState counterState = createCounterCommandHandler.handle();
        log.info("modifiable default empty counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(0);
        assertThat(counterState.getValue()).isNotNull();
    }

    @Test
    void should_create_modifiable_default_empty_counter_using_spring_handle_empty() {
        // when
        CounterState counterState = createCounterCommandHandler.handle(createCounterCommand -> {
        });
        log.info("modifiable default empty counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(0);
        assertThat(counterState.getValue()).isNotNull();
    }

    @Test
    void should_create_modifiable_default_empty_counter_using_spring_apply() {
        // when
        CounterState counterState = createCounterCommandHandler.apply();
        log.info("modifiable default empty counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(0);
        assertThat(counterState.getValue()).isNotNull();
    }

    @Test
    void should_create_modifiable_default_empty_counter_using_spring_apply_empty() {
        // when
        CounterState counterState = createCounterCommandHandler.apply(createCounterCommand -> {
        });
        log.info("modifiable default empty counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(0);
        assertThat(counterState.getValue()).isNotNull();
    }

    @Test
    void should_create_modifiable_complete_custom_counter_using_abstraction() {
        // given
        CreateCounter createCounter = new CreateCounter();

        // when
        CounterState counterState = createCounter.apply(command ->
                command.setName("0-0-0-0-3")
                        .setInitialValue(123)
        );
        log.info("modifiable complete custom counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(123);
        assertThat(counterState.getValue()).isEqualTo(UUID.fromString("0-0-0-0-3"));
    }

    @Test
    void should_create_modifiable_partial_custom_counter_using_abstraction() {
        // given
        CreateCounter createCounter = new CreateCounter();

        // when
        CounterState counterState = createCounter.apply(command -> command.setName("0-0-0-0-2"));
        log.info("modifiable partial custom counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(0);
        assertThat(counterState.getValue()).isEqualTo(UUID.fromString("0-0-0-0-2"));
    }

    @Test
    void should_create_modifiable_default_empty_counter_using_abstraction() {
        // given
        CreateCounter createCounter = new CreateCounter();

        // when
        CounterState counterState = createCounter.apply(createCounterCommand -> {
        });
        log.info("modifiable default empty counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(0);
        assertThat(counterState.getValue()).isNotNull();
    }

    @Test
    void should_create_modifiable_complete_custom_counter_using_function() {
        // setup infrastructure
        Function<Consumer<CreateCounterCommand>, CounterState> createCounter = createCounterCommandConsumer -> {
            CreateCounterCommand createCounterCommand = new CreateCounterCommand()
                    .setName(UUID.randomUUID().toString());
            createCounterCommandConsumer.accept(createCounterCommand);
            return new CounterState()
                    .setInitialValue(createCounterCommand.getInitialValue())
                    .setValue(UUID.fromString(createCounterCommand.getName()));
        };

        // when
        CounterState counterState = createCounter.apply(command ->
                command.setName("0-0-0-0-3")
                        .setInitialValue(123)
        );
        log.info("modifiable complete custom counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(123);
        assertThat(counterState.getValue()).isEqualTo(UUID.fromString("0-0-0-0-3"));
    }

    @Test
    void should_create_modifiable_partial_custom_counter_using_function() {
        // setup infrastructure
        Function<Consumer<CreateCounterCommand>, CounterState> createCounter = createCounterCommandConsumer -> {
            CreateCounterCommand createCounterCommand = new CreateCounterCommand()
                    .setName(UUID.randomUUID().toString());
            createCounterCommandConsumer.accept(createCounterCommand);
            return new CounterState()
                    .setInitialValue(createCounterCommand.getInitialValue())
                    .setValue(UUID.fromString(createCounterCommand.getName()));
        };

        // when
        CounterState counterState = createCounter.apply(command -> command.setName("0-0-0-0-2"));
        log.info("modifiable partial custom counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(0);
        assertThat(counterState.getValue()).isEqualTo(UUID.fromString("0-0-0-0-2"));
    }

    @Test
    void should_create_modifiable_default_empty_counter_using_function() {
        // setup infrastructure
        Function<Consumer<CreateCounterCommand>, CounterState> createCounter = createCounterCommandConsumer -> {
            CreateCounterCommand createCounterCommand = new CreateCounterCommand()
                    .setName(UUID.randomUUID().toString());
            createCounterCommandConsumer.accept(createCounterCommand);
            return new CounterState()
                    .setInitialValue(createCounterCommand.getInitialValue())
                    .setValue(UUID.fromString(createCounterCommand.getName()));
        };

        // when
        CounterState counterState = createCounter.apply(createCounterCommand -> {
        });
        log.info("modifiable default empty counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(0);
        assertThat(counterState.getValue()).isNotNull();
    }

    @Test
    void should_create_custom_counter_using_function() {
        // setup infrastructure
        Function<CreateCounterCommand, CounterState> createCounter = createCounterCommand ->
                new CounterState()
                        .setInitialValue(createCounterCommand.getInitialValue())
                        .setValue(
                                Optional.ofNullable(createCounterCommand.getName())
                                        .map(UUID::fromString).orElseGet(UUID::randomUUID)
                        );

        // when
        CounterState counterState = createCounter.apply(
                new CreateCounterCommand()
                        .setName("0-0-0-0-1")
                        .setInitialValue(123)
        );
        log.info("custom counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(123);
        assertThat(counterState.getValue()).isEqualTo(UUID.fromString("0-0-0-0-1"));
    }

    @Test
    void should_create_default_empty_counter_using_function() {
        // setup infrastructure
        Function<CreateCounterCommand, CounterState> createCounter = createCounterCommand ->
                new CounterState()
                        .setInitialValue(createCounterCommand.getInitialValue())
                        .setValue(
                                Optional.ofNullable(createCounterCommand.getName())
                                        .map(UUID::fromString).orElseGet(UUID::randomUUID)
                        );

        // when
        CounterState counterState = createCounter.apply(new CreateCounterCommand());
        log.info("default empty counter: {}", counterState);

        // then
        assertThat(counterState.getInitialValue()).isEqualTo(0);
        assertThat(counterState.getValue()).isNotNull();
    }
}
