package com.example.cqrs.app.service;

import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Pipelinr;

import java.util.stream.Stream;

public class MainClass {

    public static void main2(String[] args) {
        Pipeline pipeline = new Pipelinr()
                .with(() -> Stream.of(new Pong()));
        String response = pipeline.send(new Ping("localhost"));
        System.out.println(response);
    }
}
