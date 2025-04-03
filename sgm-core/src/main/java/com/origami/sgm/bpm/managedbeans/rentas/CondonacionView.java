/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.financiero.AplicarExoneracion;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.FnCondonacion;
import com.origami.sgm.entities.FnCondonacionPorcentajes;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class CondonacionView implements Serializable{
    private static final Logger LOG = Logger.getLogger(CondonacionView.class.getName());
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    private FnCondonacion condonacion;
    private List<FnCondonacion> condonacionesList;
    private FnCondonacionPorcentajes porcentaje;
    private List<FnCondonacionPorcentajes> porcentajesList;
    
    @PostConstruct
    public void init() {
        try {
            nuevaCondonacion();
            condonacionesList = (List)services.findAll(QuerysFinanciero.getCondonacionesList, new String[]{"estado"}, new Object[]{true});
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void agregarNuevaCondonacion(){
        if(condonacion.getFechaInicio() == null || condonacion.getFechaFin() == null){
            JsfUti.messageInfo(null, "Info", "Debe ingresar la fecha de inicio y de finalización antes de continuar");
            return;
        }
        if(condonacion.getFechaInicio().compareTo(condonacion.getFechaFin()) > 0){
            JsfUti.messageInfo(null, "Info", "La fecha de inicio no debe ser mayor a la fecha de finalización");
            return;
        }
        if(condonacion.getFechaFin().compareTo(new Date()) < 0){
            JsfUti.messageInfo(null, "Info", "La fecha de finalización debe ser mayor a la fecha actual");
            return;
        }
        for(FnCondonacion temp : condonacionesList){
            if(condonacion.getFechaInicio().compareTo(temp.getFechaInicio())>=0 && condonacion.getFechaInicio().compareTo(temp.getFechaFin())<=0 ){
                JsfUti.messageInfo(null, "Info", "La fecha de inicio ingresada no se encuentra disponible");
                return;
            }
            if(condonacion.getFechaFin().compareTo(temp.getFechaInicio())>=0 && condonacion.getFechaFin().compareTo(temp.getFechaFin())<=0 ){
                JsfUti.messageInfo(null, "Info", "La fecha de finalización ingresada no se encuentra disponible");
                return;
            }
        }
        porcentaje = new FnCondonacionPorcentajes();
        porcentaje.setFechaDesde(condonacion.getFechaInicio());
        porcentajesList = new ArrayList();
        condonacionesList = (List)services.findAll(QuerysFinanciero.getCondonacionesList, new String[]{"estado"}, new Object[]{true});
        condonacion.setDiasPlazo(Integer.parseInt(TimeUnit.DAYS.convert((condonacion.getFechaFin().getTime() - condonacion.getFechaInicio().getTime()), TimeUnit.MILLISECONDS)+""));
        JsfUti.executeJS("PF('dlgPorcentaje').show()");
    }
    
    public void nuevaCondonacion(){
        condonacion = new FnCondonacion();
        condonacion.setEstado(Boolean.TRUE);
        condonacion.setFechaIngreso(new Date());
        condonacion.setUsuarioIngreso(uSession.getName_user());
    }
    
    public void agregarPorcentaje(){
        if(porcentaje.getFechaDesde() == null || porcentaje.getFechaHasta() == null || porcentaje.getPorcentaje() == null){
            JsfUti.messageInfo(null, "Info", "Debe ingresar todos los datos antes de continuar");
            return;
        }
        if(porcentaje.getFechaDesde().compareTo(porcentaje.getFechaHasta()) > 0){
            JsfUti.messageInfo(null, "Info", "La fecha de inicio no debe ser mayor a la fecha de finalización");
            return;
        }
        if(porcentaje.getFechaDesde().compareTo(condonacion.getFechaInicio()) < 0){
            JsfUti.messageInfo(null, "Info", "Fecha de inicio fuera de rango");
            return;
        }
        if(porcentaje.getFechaHasta().compareTo(condonacion.getFechaFin()) > 0){
            JsfUti.messageInfo(null, "Info", "Fecha de finalización fuera de rango");
            return;
        }
        for(FnCondonacionPorcentajes temp : porcentajesList){
            if(porcentaje.getFechaDesde().compareTo(temp.getFechaDesde())>=0 && porcentaje.getFechaDesde().compareTo(temp.getFechaHasta())<=0 ){
                JsfUti.messageInfo(null, "Info", "La fecha de inicio ingresada no se encuentra disponible");
                return;
            }
            if(porcentaje.getFechaHasta().compareTo(temp.getFechaDesde())>=0 && porcentaje.getFechaHasta().compareTo(temp.getFechaHasta())<=0 ){
                JsfUti.messageInfo(null, "Info", "La fecha de finalización ingresada no se encuentra disponible");
                return;
            }
        }
        Date temp = porcentaje.getFechaHasta();
        if(condonacion.getId() == null)
            condonacion = (FnCondonacion) services.persist(condonacion);
        porcentaje.setEstado(Boolean.TRUE);
        porcentaje.setCondonacion(condonacion);
        porcentaje = (FnCondonacionPorcentajes) services.persist(porcentaje);
        porcentajesList.add(porcentaje);
        porcentaje = new FnCondonacionPorcentajes();
        Calendar c = Calendar.getInstance();
        c.setTime(temp);
        c.add(Calendar.DATE, 1);
        porcentaje.setFechaDesde(c.getTime());
        c.add(Calendar.DATE, 30);
        porcentaje.setFechaHasta(c.getTime());
    }
    
    public void editarCondonacion(){
        
        for(FnCondonacionPorcentajes temp : porcentajesList){
            if(condonacion.getFechaInicio().compareTo(temp.getFechaDesde())>0 || condonacion.getFechaFin().compareTo(temp.getFechaHasta())<0){
                JsfUti.messageInfo(null, "Info", "No se puede ingresar el nuevo rango de fechas. Intente con otro o elimine porcentajes");
                condonacion.setFechaInicio(new Date());
                condonacion.setFechaFin(new Date());
                return;
            }
        }
        try{
            services.update(condonacion);
            JsfUti.messageInfo(null, "Info", "Fechas cambiados con éxito");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editarCondonacion(FnCondonacion c){
        condonacion = c;
        porcentajesList = (List)condonacion.getPorcentajes();
        porcentaje = new FnCondonacionPorcentajes();
    }
    
    public void eliminarCondonacion(FnCondonacion c){
        condonacion = c;
        condonacion.setEstado(false);
        services.update(condonacion);
        condonacionesList.remove(condonacion);
    }
    
    public void eliminarPorcentaje(FnCondonacionPorcentajes c){
        porcentaje = c;
        porcentaje.setEstado(false);
        services.update(porcentaje);
        porcentajesList.remove(porcentaje);
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public FnCondonacion getCondonacion() {
        return condonacion;
    }

    public void setCondonacion(FnCondonacion condonacion) {
        this.condonacion = condonacion;
    }

    public FnCondonacionPorcentajes getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(FnCondonacionPorcentajes porcentaje) {
        this.porcentaje = porcentaje;
    }

    public List<FnCondonacionPorcentajes> getPorcentajesList() {
        return porcentajesList;
    }

    public void setPorcentajesList(List<FnCondonacionPorcentajes> porcentajesList) {
        this.porcentajesList = porcentajesList;
    }

    public List<FnCondonacion> getCondonacionesList() {
        return condonacionesList;
    }

    public void setCondonacionesList(List<FnCondonacion> condonacionesList) {
        this.condonacionesList = condonacionesList;
    }
    
}
