/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.permisosFuncionamiento;

import com.origami.sgm.PermisosFuncionamientoLazy;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.activiti.engine.history.HistoricTaskInstance;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class PermisosFuncionamiento extends BpmManageBeanBaseRoot implements Serializable {

    // variables de lista de permiso de funcionamiento
    private PermisosFuncionamientoLazy permisos;
    private RenPermisosFuncionamientoLocalComercial permiso;

    /**
     * inicializador de la vista
     */
    @PostConstruct
    public void initView() {
        permisos = new PermisosFuncionamientoLazy();
    }
    
    public void verDetalle(RenPermisosFuncionamientoLocalComercial permiso) {
        this.permiso = permiso;
        JsfUti.executeJS("PF('dlgDet').show();");
        JsfUti.update("frmDet");
    }
    
    public List<HistoricTaskInstance> getTaskInstance(){
        if(permiso != null)
            return this.getTaskByProcessInstanceIdMain(permiso.getHt().getIdProcesoTemp());
        else
            return null;
    }

//inicio de getter and setter

    public PermisosFuncionamientoLazy getPermisos() {
        return permisos;
    }

    public void setPermisos(PermisosFuncionamientoLazy permisos) {
        this.permisos = permisos;
    }
    

    public RenPermisosFuncionamientoLocalComercial getPermiso() {
        return permiso;
    }

    public void setPermiso(RenPermisosFuncionamientoLocalComercial permiso) {
        this.permiso = permiso;
    }
    
    public PermisosFuncionamiento() {
    }
    
}
