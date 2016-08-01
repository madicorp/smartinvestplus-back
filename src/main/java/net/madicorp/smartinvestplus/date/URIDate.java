package net.madicorp.smartinvestplus.date;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: sennen
 * Date: 29/07/2016
 * Time: 18:22
 */
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Pattern(regexp = "[12]\\d{3}([01]\\d){2}")
@Constraint(validatedBy = { })
public @interface URIDate {

    /**
     * @return the error message template
     */
    String message() default "{javax.validation.constraints.Pattern.message}";

    /**
     * @return the groups the constraint belongs to
     */
    Class<?>[] groups() default { };

    /**
     * @return the payload associated to the constraint
     */
    Class<? extends Payload>[] payload() default { };
}

