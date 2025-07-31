package com.devit.chatapp.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueOfEnumValidator.class)
@Documented
public @interface ValueOfEnum {
    Class<? extends Enum<?>> enumClass();
    String message() default "Debe ser un valor permitido del enum";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}