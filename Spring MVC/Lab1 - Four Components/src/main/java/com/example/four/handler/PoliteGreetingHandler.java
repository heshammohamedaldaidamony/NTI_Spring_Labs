package com.example.four.handler;

public class PoliteGreetingHandler implements GreetingHandler {

    public PoliteGreetingHandler() {
    }

    @Override
    public String greet(String name) {
        return "Good day to you, " + name + ".";
    }
}