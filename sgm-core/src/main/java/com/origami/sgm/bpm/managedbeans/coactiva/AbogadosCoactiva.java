/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.coactiva;

import com.origami.session.UserSession;
import com.origami.sgm.entities.CoaAbogado;
import com.origami.sgm.lazymodels.CoaAbogadosLazy;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.JsfUti;

/**
 *
 * @author origami
 */
@Named(value = "abogadosCoactiva")
@ViewScoped
public class AbogadosCoactiva implements Serializable{
    public static final Long serialVersionUID = 1L;
    
    @javax.inject.Inject
    private RecaudacionesService recaudacion;
    
    @Inject
    protected UserSession session;
    
    protected CoaAbogadosLazy abogados;
    protected CoaAbogado abogado;
    
            
    @PostConstruct
    public void initView() {
        try{
            abogados= new CoaAbogadosLazy();
        }catch (Exception e) {
            Logger.getLogger(AbogadosCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void seleccionarAbogado(CoaAbogado ab){
        try{
            if(ab==null){
                this.abogado= new CoaAbogado();
            }else{
                this.abogado=ab;
            }
        } catch (Exception e) {
            Logger.getLogger(AbogadosCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void guardarAbogado(){
        try{
            if(validarAbogado(this.abogado)){
                this.abogado.setEstado(Boolean.TRUE);
                this.abogado.setFechaIngreso(new Date());
                this.abogado.setUsuarioIngreso(session.getName_user());
                this.abogado=recaudacion.grabarAbogado(this.abogado);
                if(this.abogado!=null){
                    JsfUti.messageInfo(null, "Información", "Registro Grabado Exitosamente");
                }else{
                    JsfUti.messageError(null, "Error", "No se pudo grabar el Registro");
                }
            }else{
                JsfUti.messageInfo(null, "Información", "Los campos son Obligatorios");
            }
        } catch (Exception e) {
            Logger.getLogger(AbogadosCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void inactivarActivarAbogado(){
        try{
            if(validarAbogado(this.abogado)){
                this.abogado.setEstado(!this.abogado.getEstado());
                this.abogado=recaudacion.grabarAbogado(this.abogado);
                if(this.abogado!=null){
                    JsfUti.messageInfo(null, "Información", "Registro Grabado Exitosamente");
                }else{
                    JsfUti.messageError(null, "Error", "No se pudo grabar el Registro");
                }
            }else{
                JsfUti.messageInfo(null, "Información", "Los campos son Obligatorios");
            }
        } catch (Exception e) {
            Logger.getLogger(AbogadosCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public boolean validarAbogado(CoaAbogado ab){
        return !(ab.getDetalle()==null || ab.getDetalle().length()==0);
    }

    public CoaAbogadosLazy getAbogados() {
        return abogados;
    }

    public void setAbogados(CoaAbogadosLazy abogados) {
        this.abogados = abogados;
    }

    public CoaAbogado getAbogado() {
        return abogado;
    }

    public void setAbogado(CoaAbogado abogado) {
        this.abogado = abogado;
    }
    
}
