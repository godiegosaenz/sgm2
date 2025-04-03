/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.entities.RegCapital;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class CapitalesIngresados implements Serializable{

    @javax.inject.Inject
    protected RegistroPropiedadServices services;

    protected List<RegCapital> capitalList = new ArrayList<>();
    protected RegCapital capital;
    protected Boolean edit = false;

    public CapitalesIngresados() {
    }

    @PostConstruct
    public void initView() {
        capitalList = services.getRegCapitalList();
    }

    public void nuevoCapital() {
        capital = new RegCapital();
        edit = false;
        JsfUti.update("fromCapitMod");
        JsfUti.executeJS("PF('nuevoCap').show();");
    }

    public void editarCapital(RegCapital cap) {
        capital = new RegCapital();
        capital = cap;
        edit = true;
        JsfUti.update("fromCapitMod");
        JsfUti.executeJS("PF('nuevoCap').show();");
    }

    public void guardarCapital() {
        if (capital.getNombre() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar el Nombre.", null);
            return;
        }
        if (edit) {
            Boolean ok = services.updateRegCapital(capital);
        } else {
            capital = services.guardarRegCapital(capital);
        }
        JsfUti.executeJS("PF('nuevoCap').hide();");
        JsfUti.redirectFaces("/faces/admin/registro/capitalesIngresados.xhtml");
    }

    public List<RegCapital> getCapitalList() {
        return capitalList;
    }

    public void setCapitalList(List<RegCapital> capitalList) {
        this.capitalList = capitalList;
    }

    public RegCapital getCapital() {
        return capital;
    }

    public void setCapital(RegCapital capital) {
        this.capital = capital;
    }

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

}
