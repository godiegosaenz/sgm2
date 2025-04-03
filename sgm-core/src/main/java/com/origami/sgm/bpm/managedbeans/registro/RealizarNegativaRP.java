/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author origami
 */
@Named
@ViewScoped
public class RealizarNegativaRP extends BpmManageBeanBaseRoot implements Serializable{

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        
    }
    
}
