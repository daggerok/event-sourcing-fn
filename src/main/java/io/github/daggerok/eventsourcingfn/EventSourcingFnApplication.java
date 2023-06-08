package io.github.daggerok.eventsourcingfn;

import io.github.daggerok.eventsourcingfn.counter.CounterState;
import io.github.daggerok.eventsourcingfn.counter.create.CreateCounterCommand;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class EventSourcingFnApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventSourcingFnApplication.class, args);
    }
}

@Log4j2
@RestController
@RequiredArgsConstructor
class CreateCounterResource {

    @PostMapping("/counter/create")
    CounterState createCounter(@RequestBody CreateCounterCommand command) {
        log.info("createCounter(command={})", command);
        return new CounterState()
                .setValue(UUID.fromString(command.getName()))
                .setInitialValue(command.getInitialValue());
    }
}

