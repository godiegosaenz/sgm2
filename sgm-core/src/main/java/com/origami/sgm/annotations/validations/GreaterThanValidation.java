/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.annotations.validations;

import com.origami.sgm.annotations.GreaterMode;
import com.origami.sgm.annotations.GreaterThan;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author OrigamiDi1
 */
public class GreaterThanValidation implements ConstraintValidator<GreaterThan, Object> {

    private double valor;
    private GreaterMode mode;
    private String message;

    @Override
    public void initialize(GreaterThan constraintAnnotation) {
        this.valor = constraintAnnotation.valor();
        this.mode = constraintAnnotation.mode();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean isValid;
        boolean msg = message.equals("Valor debe ser mayor a 0");

        if (msg) {
            message = "Valor debe ser mayor a " + valor;
        }
        if (value == null) {
            isValid = true;
        } else {

            if (null == mode) {
                isValid = Double.valueOf(value.toString()) > valor;
            } else {
                switch (mode) {
                    case GREATERTHAN_EQUALTO:
                        isValid = Double.valueOf(value.toString()) >= valor;
                        if (msg) {
                            message = "Valor debe ser mayor o igual a " + valor;
                        }
                        break;
                    case LESSTHAN:
                        isValid = Double.valueOf(value.toString()) < valor;
                        if (msg) {
                            message = "Valor debe ser menor a " + valor;
                        }
                        break;
                    case LESSTHAN_EQUALTO:
                        isValid = Double.valueOf(value.toString()) <= valor;
                        if (msg) {
                            message = "Valor debe ser menor o igual a " + valor;
                        }
                        break;
                    default:
                        isValid = Double.valueOf(value.toString()) > valor;
                        break;
                }
            }
            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
        }

        return isValid;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public GreaterMode getMode() {
        return mode;
    }

    public void setMode(GreaterMode mode) {
        this.mode = mode;
    }

}
