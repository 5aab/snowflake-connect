package com.example.cqrs.app.domain;

import lombok.Builder;

@Builder
public record Person(String name, String address){}
