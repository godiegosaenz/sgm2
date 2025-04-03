/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.RegRegistrador;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class CambioRegistrador implements Serializable {

    public static final long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager serv;;

    protected Boolean nuevo = false;
    protected RegRegistrador registrador;
    protected List<RegRegistrador> list = new ArrayList<>();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            list = serv.findAll(Querys.getListRegRegistrador);
        } catch (Exception e) {
            Logger.getLogger(CambioRegistrador.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgNew() {
        nuevo = true;
        registrador = new RegRegistrador();
        JsfUti.update("frmReg");
        JsfUti.executeJS("PF('dlgRegistrador').show();");
    }

    public void showDlgEdit(RegRegistrador r) {
        nuevo = false;
        registrador = r;
        JsfUti.update("frmReg");
        JsfUti.executeJS("PF('dlgRegistrador').show();");
    }

    public void saveRegistrador() {
        try {
            if (registrador.getNombre() == null || registrador.getNombreCompleto() == null || registrador.getTituloCompleto() == null) {
                JsfUti.messageError(null, "Faltan Campos", "Todos los campos son obligatorios.");
            } else {
                if (nuevo) {
                    registrador.setActual(true);
                    Object o = serv.persist(registrador);
                    if (o != null) {
                        for (RegRegistrador r : list) {
                            r.setActual(false);
                            serv.update(r);
                        }
                    }
                } else {
                    for (RegRegistrador r : list) {
                        r.setActual(false);
                        serv.update(r);
                    }
                    registrador.setActual(true);
                    serv.update(registrador);
                }
                JsfUti.executeJS("PF('dlgRegistrador').hide();");
                JsfUti.update("frmConfirm");
                JsfUti.executeJS("PF('dlgConfirm').show();");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, "Ocurrio un Problema", "");
            Logger.getLogger(CambioRegistrador.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void cargarDatos() {
        try {
            list = serv.findAll(Querys.getListRegRegistrador);
            JsfUti.update("mainForm");
            JsfUti.executeJS("PF('dlgConfirm').hide();");
        } catch (Exception e) {
            JsfUti.messageError(null, "Ocurrio un Problema", "");
            Logger.getLogger(CambioRegistrador.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    public RegRegistrador getRegistrador() {
        return registrador;
    }

    public void setRegistrador(RegRegistrador registrador) {
        this.registrador = registrador;
    }

    public List<RegRegistrador> getList() {
        return list;
    }

    public void setList(List<RegRegistrador> list) {
        this.list = list;
    }

}
