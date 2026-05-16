package com.example.app.guard;

import lombok.Data;

import java.util.function.Consumer;

@Data
public class GuardResult {

    private boolean allowed;
    private String redirectPath;
    private String message;
    private Consumer<Boolean> onConfirm;

    public static GuardResult allow() {
        GuardResult result = new GuardResult();
        result.setAllowed(true);
        return result;
    }

    public static GuardResult deny(String message) {
        GuardResult result = new GuardResult();
        result.setAllowed(false);
        result.setMessage(message);
        return result;
    }

    public static GuardResult redirect(String path) {
        GuardResult result = new GuardResult();
        result.setAllowed(false);
        result.setRedirectPath(path);
        return result;
    }

    public static GuardResult confirm(String message, Consumer<Boolean> onConfirm) {
        GuardResult result = new GuardResult();
        result.setAllowed(false);
        result.setMessage(message);
        result.setOnConfirm(onConfirm);
        return result;
    }

    public boolean needsConfirm() {
        return onConfirm != null;
    }
}