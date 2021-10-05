package com.example.cqrs.custom.telemetery;

import an.awesome.pipelinr.Command;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import org.springframework.core.annotation.Order;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Order(3)
public class MicrometerTiming implements Command.Middleware {

    private final Clock clock;
    private final static Map<Class<? extends Command>, Timer> timers = new ConcurrentHashMap<>();

    public MicrometerTiming() {
        this.clock = Clock.systemDefaultZone();
    }

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {

        Timer timer = timers.computeIfAbsent(command.getClass(),
                commandClass -> Timer.builder("command.timer")
                        .description("command execution timer")
                        .tag("command", commandClass.getSimpleName())
                        .register(Metrics.globalRegistry));
        Instant now = Instant.now(clock);
        R result = next.invoke();
        timer.record(Duration.between(now, Instant.now(clock)));
        return result;
    }
}
