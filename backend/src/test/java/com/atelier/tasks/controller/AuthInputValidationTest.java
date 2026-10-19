package com.atelier.tasks.controller;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AuthInputValidationTest {
    @Test
    void registrationRequiresValidEmailAndStrongPassword() {
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var validator = validatorFactory.getValidator();
            var request = new AuthController.Register("", "not-an-email", "short");
            assertTrue(validator.validate(request).size() >= 3);
        }
    }

    @Test
    void bcryptStoresAHashThatVerifiesWithoutExposingThePassword() {
        var encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("correct horse battery");
        assertNotEquals("correct horse battery", hash);
        assertTrue(encoder.matches("correct horse battery", hash));
        assertFalse(encoder.matches("different password", hash));
    }
}
