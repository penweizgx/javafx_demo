package com.example.app.validator;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Validates a single field.
 * - valueProvider: supplier providing current string value
 * - rule: function that returns null if ok, or error message if invalid
 */
public class FieldValidator {
    private final Supplier<String> valueProvider;
    private final Function<String, String> rule;

    public FieldValidator(Supplier<String> valueProvider, Function<String, String> rule) {
        this.valueProvider = valueProvider;
        this.rule = rule;
    }

    public String validate() {
        return rule.apply(valueProvider.get());
    }
}
