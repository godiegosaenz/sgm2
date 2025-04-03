/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.config.SisVars;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.RegMantenimientoMenu;
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
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class MenuMantenimiento implements Serializable{
    public static final long serialVersionUID = 1L;
    
    @javax.inject.Inject
    private Entitymanager acl;
    
    protected List<RegMantenimientoMenu> list = new ArrayList<>();
    protected List<RegMantenimientoMenu> listDocs = new ArrayList<>();
    
    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            // TIPO 1 PARA MENU DE MANTENIMIENTO
            list = acl.findAll(Querys.getRegMantenimientoMenu, new String[]{"estado", "tipo"}, new Object[]{true, 1});
            // TIPO 2 PARA DOCUMENTOS DE DESCARGAS
            listDocs = acl.findAll(Querys.getRegMantenimientoMenu, new String[]{"estado", "tipo"}, new Object[]{true, 2});
        } catch (Exception e) {
            Logger.getLogger(MenuMantenimiento.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void redirectUrl(RegMantenimientoMenu menu){
        JsfUti.redirectFaces(menu.getUrl());
    }
    
    public void descargarDocumento(RegMantenimientoMenu menu) {
        try {
            if (menu.getUrl() != null) {
                JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/DescargarDocsRepositorio?id=" + menu.getUrl());
            } else {
                JsfUti.messageError(null, "Error, no se encuentra el documento.", "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(MenuMantenimiento.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public List<RegMantenimientoMenu> getList() {
        return list;
    }

    public void setList(List<RegMantenimientoMenu> list) {
        this.list = list;
    }

    public List<RegMantenimientoMenu> getListDocs() {
        return listDocs;
    }

    public void setListDocs(List<RegMantenimientoMenu> listDocs) {
        this.listDocs = listDocs;
    }
    
}
