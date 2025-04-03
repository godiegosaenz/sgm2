/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.app;

import com.origami.sgm.services.interfaces.TitulosReporteCacheLocal;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 * @Date 25/05/2016
 */
@Named
@ViewScoped
public class CPanelMain implements Serializable {

    /**
     * Creates a new instance of CPanelMain
     */
    public CPanelMain() {
    }

    @Inject
    private TitulosReporteCacheLocal menuTitulosReporte;
    
    @Inject
    private AppMenu menu;

    @PostConstruct
    public void initView() {

    }

    /**
     * Inicializar el menu
     */
    public void iniciarMenu() {
        this.menu.clearMenuWorkflow();
        this.menu.getMenuWorkflow();
        JsfUti.redirectFaces("/cpanel/cpanelMain.xhtml");
    }
    
    public void iniciarArbol() {
        try{
            this.menuTitulosReporte.clearCache();
            this.menuTitulosReporte.getTree();
            JsfUti.redirectFaces("/cpanel/cpanelMain.xhtml");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void mostrarDialog(String urlFacelet, String alto, String ancho) {
        Map<String, Object> options = new HashMap<>();
        if (ancho == null || ancho.trim().length() == 0) {
            options.put("width", "75%");
        } else {
            options.put("width", ancho);
        }
        if (alto == null || alto.trim().length() == 0) {
            options.put("height", "100%");
        } else {
            options.put("height", alto);
        }
        options.put("closable", true);
        options.put("contentHeight", "100%");
        options.put("contentWidth", "100%");
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog(urlFacelet, options, null);
    }
}
