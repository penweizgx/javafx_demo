package com.example.app.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandler {

    public static void handle(Throwable e, String context) {
        log.error("{}: {}", context, e.getMessage(), e);
    }

    public static void handle(Throwable e) {
        handle(e, "Error");
    }
}
