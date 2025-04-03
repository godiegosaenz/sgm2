/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans;

import com.origami.session.UserSession;
import com.origami.sgm.lazymodels.bpm.TareasWFLazy;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Administrador
 */
@Named
@ViewScoped
public class DashBoard extends BpmManageBeanBaseRoot implements Serializable {

    protected UserSession us1 = new UserSession();
    //protected List<TareaWF> tareasWF = new ArrayList<>();
    protected Boolean esAdmin = false;
    protected Boolean userRegistro = false;
    private String usuario;
    private Integer cantidad = 0;
    
    protected TareasWFLazy lazy;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            usuario = session.getName_user();
            if(usuario != null){
                esAdmin = this.validateAdmin(session.getRoles());
                userRegistro = this.validarUsuariosRegistro(session.getDepts());
                //tareasWF = this.getListaTareasPersonales(usuario,null);
                //System.out.println("// tareas dashboard: " + tareasWF.size());
                lazy = new TareasWFLazy(usuario);
                cantidad = this.getCantidadTareasUser(usuario);
            }
        } catch (Exception e) {
            Logger.getLogger(DashBoard.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void actualizarTramitePendiente() {
        try {
            //tareasWF = this.getListaTareasPersonales(usuario,null);
            //System.out.println("// tareas dashboard: " + tareasWF.size());
            lazy = new TareasWFLazy(usuario);
            cantidad = this.getCantidadTareasUser(usuario);
            JsfUti.update("formMain");
        } catch (Exception e) {
            Logger.getLogger(DashBoard.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean validateAdmin(List<Long> list) {
        for (Long id : list) {
            if (id.equals(9L)) {
                return true;
            }
        }
        return false;
    }

    public Boolean validarUsuariosRegistro(List<Long> list){
        for (Long id : list) {
            if (id.equals(4L) || id.equals(2L)) { // departamento Registro Propiedad 4 - Catastro 2
                return true;
            }
        }
        return false;
    }
    
    /*public List<TareaWF> getTareasWF() {
        return tareasWF;
    }

    public void setTareasWF(List<TareaWF> tareasWF) {
        this.tareasWF = tareasWF;
    }*/

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Boolean getEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(Boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    public Boolean getUserRegistro() {
        return userRegistro;
    }

    public void setUserRegistro(Boolean userRegistro) {
        this.userRegistro = userRegistro;
    }

    public TareasWFLazy getLazy() {
        return lazy;
    }

    public void setLazy(TareasWFLazy lazy) {
        this.lazy = lazy;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}
