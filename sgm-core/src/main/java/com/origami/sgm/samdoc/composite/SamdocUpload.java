/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.samdoc.composite;

import javax.annotation.PostConstruct;
import javax.el.MethodExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Fernando
 */
@FacesComponent("samdocUpload")
public class SamdocUpload extends UINamingContainer {
    
    @PostConstruct
    public void init(){
        
    }
    
    public void uploadListener(ActionEvent event){
        FacesContext context = FacesContext.getCurrentInstance();
        MethodExpression eventListener = (MethodExpression) getAttributes().get("subidaListener");
        eventListener.invoke(context.getELContext(), null /* new Object[] {  }*/);
        
    }
    
    
    
}
