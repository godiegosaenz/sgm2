/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.recaudaciones;

import com.origami.sgm.entities.RenIntereses;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.JsfUti;

/**
 *
 * @author origami
 */
@Named(value = "intereses")
@ViewScoped
public class Intereses implements Serializable{

    public static final Long serialVersionUID = 1L;
    final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
    @javax.inject.Inject
    private RecaudacionesService recaudacion;
    @javax.inject.Inject
    private Entitymanager manager;
    protected BaseLazyDataModel<RenIntereses> intereses;
    protected RenIntereses interes;
    private Map<String, Object> paramt;
    
    @PostConstruct
    public void initView() {
        try{
            intereses= new BaseLazyDataModel<>(RenIntereses.class, "id", "DESC");
        } catch (Exception e) {
            Logger.getLogger(Intereses.class.getName()).log(Level.SEVERE, null, e);
        }
        
    }
    
    public boolean validarInteres(RenIntereses i){
        if(i.getAnio() == null || i.getPorcentaje()==null){
            JsfUti.messageInfo(null, "Información", "Los campos son Obligatorios");
            return false;
        }
        if(i.getId()==null){
            paramt = new HashMap<>();
            paramt.put("anio", i.getAnio());
            if(manager.findObjectByParameter(RenIntereses.class, paramt)!=null){
                JsfUti.messageInfo(null, "Información", "Registro ya existe");
                return false;
            }
        }
        return true;
    }
    
    public void seleccionarInteres(RenIntereses i){
        try{
            if(i==null){
                this.interes=new RenIntereses();
                Calendar fechaActual= Calendar.getInstance();
                fechaActual.set(Calendar.HOUR, 0);
                fechaActual.set(Calendar.MINUTE, 0);
                fechaActual.set(Calendar.SECOND, 0);
                fechaActual.set(Calendar.DAY_OF_MONTH, 1);
                this.interes.setDesde(fechaActual.getTime());
                fechaActual.add(Calendar.MONTH, 1);
                fechaActual.add(Calendar.DAY_OF_MONTH, -1);
                this.interes.setHasta(fechaActual.getTime());            
                this.interes.setDias(new Integer(((this.interes.getHasta().getTime()-this.interes.getDesde().getTime())/MILLSECS_PER_DAY)+1+""));
            }else{
                this.interes=i;
            }
        } catch (Exception e) {
            Logger.getLogger(Intereses.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void guardarInteres(){
        try{
            if(validarInteres(this.interes)){
                this.interes=recaudacion.grabraInteres(this.interes);
                if(this.interes!=null){
                    JsfUti.messageInfo(null, "Información", "Registro Grabado Exitosamente");
                }else{
                    JsfUti.messageError(null, "Error", "No se pudo grabar el Registro");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Intereses.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public BaseLazyDataModel<RenIntereses> getIntereses() {
        return intereses;
    }

    public void setIntereses(BaseLazyDataModel<RenIntereses> intereses) {
        this.intereses = intereses;
    }

    public RenIntereses getInteres() {
        return interes;
    }

    public void setInteres(RenIntereses interes) {
        this.interes = interes;
    }
    
}
