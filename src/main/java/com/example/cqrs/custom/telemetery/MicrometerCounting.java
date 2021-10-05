package com.example.cqrs.custom.telemetery;

import an.awesome.pipelinr.Command;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Order(2)
@AllArgsConstructor
public class MicrometerCounting implements Command.Middleware {

    private final static Map<Class<? extends Command>, Counter> counters = new ConcurrentHashMap<>();

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        Counter.Builder counterBuilder = Counter.builder("command.status.counter").description("command error counter");
        try {
            R result = next.invoke();
            counters.computeIfAbsent(command.getClass(),
                    commandClass -> counterBuilder.tag("command", commandClass.getSimpleName())
                            .tag("status", "success")
                            .tag("error-type", EMPTY)
                            .register(Metrics.globalRegistry)).increment();
            return result;
        } catch (Exception e) {
            counters.computeIfAbsent(command.getClass(),
                    commandClass -> counterBuilder.tag("command", commandClass.getSimpleName())
                            .tag("status", "error")
                            .tag("error-type", e.getClass().getSimpleName())
                            .register(Metrics.globalRegistry)).increment();
            throw e;
        }
    }
}
