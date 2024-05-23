package advanced.algorithms.programming.tailoredsocialnetwork.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ContentOrPictureValidator.class)
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ContentOrPictureRequired {
    String message() default "Either content or picture must be provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

