package com.example.app.validator;

import com.example.app.component.FormItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FormValidatorTest {

    @Mock
    private FormItem formItem1;

    @Mock
    private FormItem formItem2;

    private FormValidator formValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        formValidator = new FormValidator();
    }

    @Test
    void validateAll_AllFieldsValid_ShouldReturnTrue() {
        FieldValidator validField = new FieldValidator(() -> "value", v -> null);
        formValidator.addField(validField, formItem1);

        boolean result = formValidator.validateAll();

        assertTrue(result);
        verify(formItem1).showError(null);
    }

    @Test
    void validateAll_InvalidField_ShouldReturnFalseAndShowError() {
        FieldValidator invalidField = new FieldValidator(() -> "", v -> "Required");
        formValidator.addField(invalidField, formItem1);

        boolean result = formValidator.validateAll();

        assertFalse(result);
        verify(formItem1).showError("Required");
    }

    @Test
    void validateAll_MultipleFieldsWithOneInvalid_ShouldReturnFalse() {
        FieldValidator validField = new FieldValidator(() -> "value", v -> null);
        FieldValidator invalidField = new FieldValidator(() -> "", v -> "Required");
        formValidator.addField(validField, formItem1);
        formValidator.addField(invalidField, formItem2);

        boolean result = formValidator.validateAll();

        assertFalse(result);
        verify(formItem1).showError(null);
        verify(formItem2).showError("Required");
    }
}
