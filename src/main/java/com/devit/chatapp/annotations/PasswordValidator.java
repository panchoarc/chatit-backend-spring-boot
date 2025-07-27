package com.devit.chatapp.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    // Expresión regular para validar la contraseña
    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // Aquí podrías inicializar parámetros adicionales si es necesario
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;  // Considera la contraseña nula como no válida
        }

        // Comprobar si la contraseña cumple con los requisitos
        return password.matches(PASSWORD_REGEX);
    }
}
