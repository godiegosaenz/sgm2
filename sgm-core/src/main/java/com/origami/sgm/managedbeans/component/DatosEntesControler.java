/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.util.EjbsCaller;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.el.MethodExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Angel Navarro
 * @date 29/08/2016
 */
@FacesComponent(value = "datosEntesCont")
public class DatosEntesControler extends UINamingContainer {

    @PostConstruct
    public void init() {

    }
    public void seleccionEnte(SelectEvent event){
        FacesContext context = FacesContext.getCurrentInstance();
        MethodExpression eventListener = (MethodExpression) getAttributes().get("seleccion");
        eventListener.invoke(context.getELContext(), new Object[] {event});
        
    }

    public String getNombreRepresentante(BigDecimal representante) {
        if(representante == null)
            return null;
        CatEnte ente = EjbsCaller.getTransactionManager().find(CatEnte.class, representante.longValue());
        if(ente != null)
            return ente.getNombreCompleto();
        return null;
    }

}
