/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Angel Navarro
 */
@FacesValidator(value = "dateValidator")
public class validarFecha implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }
        try {
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            String dateVal = f.format(value);
            new Date(dateVal);
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage("Fecha ingresada incorrecta."));
//            Faces.messageWarning(null, "Error de Fecha", "Fecha ingresada incorrecta");
        }

    }

}
