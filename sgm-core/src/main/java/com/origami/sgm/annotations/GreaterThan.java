/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.annotations;

import com.origami.sgm.annotations.validations.GreaterThanValidation;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.*;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author OrigamiDi1
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = GreaterThanValidation.class)
@Documented
public @interface GreaterThan {

    String message() default "Valor debe ser mayor a 0";

    double valor() default 0;

    GreaterMode mode() default GreaterMode.GREATERTHAN;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
