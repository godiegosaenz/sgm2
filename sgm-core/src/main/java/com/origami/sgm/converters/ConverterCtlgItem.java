/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.converters;

import com.origami.config.SisVars;
import com.origami.sgm.entities.CtlgItem;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import util.EntityBeanCopy;

/**
 *
 * @author dfcalderio
 */
@FacesConverter(value = "ctlgItemConverter", forClass = CtlgItem.class)
public class ConverterCtlgItem implements Converter {

    private Entitymanager manager;

    public ConverterCtlgItem() {
        try {

            manager = (Entitymanager) new InitialContext().lookup(SisVars.entityManager);
        } catch (NamingException ex) {
            Logger.getLogger(ConverterCtlgItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent c, String value) {
        if (value == null || value.isEmpty() == true || value.equals("")) {
            return null;
        }
        try {
            if (value.equals("NaN") || value.equals("null")) {
                return null;
            }
            Long id = Long.parseLong(value);
            CtlgItem item = (CtlgItem) EntityBeanCopy.clone(manager.find(CtlgItem.class, id));
            return item;
        } catch (NullPointerException | ELException | NumberFormatException e) {
            Logger.getLogger(ConverterCtlgItem.class.getName()).log(Level.SEVERE, value, e);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent c, Object value) {
        if (value != null) {
            return String.valueOf(((CtlgItem) value).getOrden());
        } else {
            return null;
        }
    }

    public Entitymanager getManager() {
        return manager;
    }

    public void setManager(Entitymanager manager) {
        this.manager = manager;
    }

}
