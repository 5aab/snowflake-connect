package com.example.cqrs.app.service;

import an.awesome.pipelinr.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class Ping implements Command<String> {

    public final String host;
}
