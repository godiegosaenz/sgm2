/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class DialogEnte implements Serializable {

    @javax.inject.Inject
    private DatoSeguroServices datoSeguroSeguro;
    
    private Boolean esPersona = true;
    private CatEnteLazy entes;
    private CatEnte ente;
    private String cedula;
    private DatoSeguro data;
    private String msg;
    
    @PostConstruct
    public void initView(){
        data = null;
        entes = new CatEnteLazy(esPersona, "A");
    }
    
    public Boolean buscarEnteDatoSeguro(Integer intentos){
        try{
            data = datoSeguroSeguro.getDatos(cedula, false, 0);
            if(data==null){
                msg = "No se ha encontrado al ente";
                return false;
            }
            ente = datoSeguroSeguro.getEnteFromDatoSeguro(data);
            //JsfUti.messageInfo(null, "Info", "Se ha encontrado al ente "+ente.getNombres());
            msg = "Se ha encontrado al cliente "+ente.getNombres() +" "+ente.getApellidos();
            JsfUti.update("frmEnte");
            JsfUti.executeJS("PF('dlgMsg').show()");
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public void cambioTipoPersona(){
        entes = new CatEnteLazy(esPersona, "A");
    }
    
    public void agregar(){
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "generic/entefaces.xhtml");
    }
    
    public void returnEnte(){
        if(ente != null){
            selectEnte(ente);
        }
    }

    public void selectEnte(CatEnte ente) {
        RequestContext.getCurrentInstance().closeDialog(ente);
    }
    
    public CatEnteLazy getEntes() {
        return entes;
    }

    public void setEntes(CatEnteLazy entes) {
        this.entes = entes;
    }

    public Boolean getEsPersona() {
        return esPersona;
    }

    public void setEsPersona(Boolean esPersona) {
        this.esPersona = esPersona;
    }
    
    public DialogEnte() {
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
}
