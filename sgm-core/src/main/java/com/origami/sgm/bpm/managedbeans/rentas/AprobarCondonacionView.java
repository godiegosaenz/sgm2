/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.FnCondonacionPorcentajes;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.FnSolicitudCondonacion;
import com.origami.sgm.lazymodels.FnSolicitudCondonacionLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class AprobarCondonacionView implements Serializable {

    private static final Long serialVersionUID = 1L;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    private FnSolicitudCondonacionLazy solicitudes;
    private List<FnCondonacionPorcentajes> condonaciones;
    private FnCondonacionPorcentajes condMain;
    
    @PostConstruct
    public void initView(){
        solicitudes = new FnSolicitudCondonacionLazy();
        condonaciones = services.findAll(QuerysFinanciero.getCondonaciones, new String[]{}, new Object[]{});
    }
    
    public void aprobar(FnSolicitudCondonacion condonacion){
        try{
            condonacion.setEstado(new FnEstadoExoneracion(1L));
            services.persist(condonacion);
            for(FnCondonacionPorcentajes temp : condonaciones){
                if(temp.getFechaDesde().after(new Date()) &&  temp.getFechaHasta().before(new Date())){
                    condMain = temp;
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void inactivar(FnSolicitudCondonacion condonacion){
        try{
            condonacion.setEstado(new FnEstadoExoneracion(3L));
            services.persist(condonacion);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void cancelar(FnSolicitudCondonacion condonacion){
        try{
            condonacion.setEstado(new FnEstadoExoneracion(4L));
            services.persist(condonacion);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public FnSolicitudCondonacionLazy getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(FnSolicitudCondonacionLazy solicitudes) {
        this.solicitudes = solicitudes;
    }
    
}
