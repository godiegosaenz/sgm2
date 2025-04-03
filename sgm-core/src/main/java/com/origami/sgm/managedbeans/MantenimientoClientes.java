/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.lazymodels.RegEnteIntervinienteLazy;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class MantenimientoClientes extends BpmManageBeanBaseRoot implements Serializable {

    protected RegEnteIntervinienteLazy intervinientesLazy;
    protected RegEnteInterviniente interv = new RegEnteInterviniente();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        intervinientesLazy = new RegEnteIntervinienteLazy();
    }

    public void showDlgEditar(RegEnteInterviniente ente) {
        try {
            interv = ente;
            if (ente.getTipoInterv().equalsIgnoreCase("N")) {
                interv.setTipo("Natural");
            } else {
                interv.setTipo("Juridica");
            }
            JsfUti.update("formEditarInterv");
            JsfUti.executeJS("PF('dlgEditarInterviniente').show();");
        } catch (Exception e) {
            Logger.getLogger(MantenimientoClientes.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardar() {
        try {
            if (interv.getNombre() != null && interv.getCedRuc() != null) {
                Boolean flag = acl.update(interv);
                if (flag) {
                    JsfUti.redirectFaces("/admin/registro/mantenimientoClientes.xhtml");
                } else {
                    JsfUti.messageWarning(null, Messages.error, "");
                }
                /*RegEnteInterviniente interviniente = (RegEnteInterviniente) acl.find(Querys.getRegInterviniente, new String[]{"cedula", "nomb", "tipointr"}, new Object[]{cedula, nombre, tipobase});
                 if (interviniente == null) {
                 Integer secuencia = (Integer) acl.find(Querys.getMaxRegEnteInterviniente, new String[]{"cedula", "tipointr"}, new Object[]{cedula, tipobase});
                 RegEnteInterviniente nuevo = new RegEnteInterviniente();
                 nuevo.setCedRuc(cedula);
                 nuevo.setNombre(nombre);
                 nuevo.setTipoInterv(tipobase);
                 nuevo.setSecuencia(secuencia + 1);
                 nuevo.setFecha(new Date());
                 nuevo.setUsuario(session.getName_user());
                 nuevo = (RegEnteInterviniente)acl.persist(nuevo);
                 if(nuevo.getId() != null){
                 JsfUti.redirectFaces("/admin/registro/mantenimientoClientes.xhtml");
                 } else {
                 JsfUti.messageWarning(null, Messages.error, "");
                 }
                 } else {
                 JsfUti.messageWarning(null, "Ya existe un interviniente con el mismo nombre, misma cedula y mismo tipo.", "");
                 }*/
            } else {
                JsfUti.messageWarning(null, "Debe ingresar el campo Cedula y Nombre.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(MantenimientoClientes.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public RegEnteInterviniente getInterv() {
        return interv;
    }

    public void setInterv(RegEnteInterviniente interv) {
        this.interv = interv;
    }

    public RegEnteIntervinienteLazy getIntervinientesLazy() {
        return intervinientesLazy;
    }

    public void setIntervinientesLazy(RegEnteIntervinienteLazy intervinientesLazy) {
        this.intervinientesLazy = intervinientesLazy;
    }

}
