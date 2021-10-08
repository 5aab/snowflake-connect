package com.example.cqrs.app.domain.ping;

import an.awesome.pipelinr.Command;
import org.springframework.stereotype.Component;

@Component
public class Pong implements Command.Handler<Ping, String> {

    @Override
    public String handle(Ping command) {
        return "Pong from " + command.getHost();
    }
}
