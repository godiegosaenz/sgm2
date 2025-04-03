/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.rentas;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.FnExoneracionClase;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.lazymodels.PropietariosLazy;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import util.Utils;

/**
 *
 * @author origami
 */
@Named(value = "aplicacionSolicitudesResoluciones")
@ViewScoped
public class AplicacionSolicitudesResoluciones implements Serializable{
    public static final Long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AplicacionSolicitudesResoluciones.class.getName());
    
    @Inject
    private UserSession session;
    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private RecaudacionesService recaudacion;
    
    protected Long tipoAplicacion=1L;
    protected FnSolicitudExoneracion solicitudExoneracion= new FnSolicitudExoneracion();
    protected List<FnExoneracionClase> exoneracionesClasePorAplicacion;
    protected FnExoneracionClase exoneracionClasePorAplicacion;
    protected CatPredioModel predioModel = new CatPredioModel();
    protected String numeroResolucion;
    protected String numeroOficio;
    protected List<CatPredio> prediosUrbanos= new ArrayList<>();
    protected List<CatPredioRustico> prediosRurales= new ArrayList<>();
    protected List<CatCiudadela> ciudadelas;
    protected PropietariosLazy propietarios;
    protected List<CatParroquia> parroquiasRurales;
    
    @PostConstruct
    public void initView() {
        try {
            if (solicitudExoneracion!=null) {
                solicitudExoneracion.setAnioInicio(Utils.getAnio(new Date()));
                solicitudExoneracion.setAnioFin(Utils.getAnio(new Date()));
                solicitudExoneracion.setFechaAprobacion(new Date());
            }
            ciudadelas = manager.findAllObjectOrder(CatCiudadela.class, new String[]{"nombre"}, Boolean.TRUE);
            propietarios = new PropietariosLazy();
            this.cargarClasesExoneracion();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public void cargarClasesExoneracion(){
        try{
          exoneracionClasePorAplicacion=null;
          if(tipoAplicacion==1L){
              Map<String,Object> par=new HashMap<>();
              par.put("estado", true);
             // exoneracionesClasePorAplicacion=manager.findAll(QuerysFinanciero.getClaseExoneracionResoluciones);
              exoneracionesClasePorAplicacion=manager.findObjectByParameterOrderList(FnExoneracionClase.class,par, new String[] {"descripcion"}, Boolean.TRUE);
          }
          if(tipoAplicacion==2L){
              exoneracionesClasePorAplicacion=manager.findAll(QuerysFinanciero.getClaseExoneracionCatastro);
          }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public void consultarPredioUrbano(){
        List<CatPredio> prediosConsulta=recaudacion.getListPrediosByPredioModel(predioModel);
        if(prediosConsulta!=null && !prediosConsulta.isEmpty()){
            for (CatPredio p : prediosConsulta) {
                if(!this.prediosUrbanos.contains(p))
                    this.prediosUrbanos.add(p);
            }
        }
    }
    
    public void eliminarPredioUrbano(CatPredio urbano){
        this.prediosUrbanos.remove(urbano);
    }
    
    public void consultarPredioRural(){
        
    }
    
    public void eliminarPredioRural(CatPredioRustico rural){
        this.prediosRurales.remove(rural);
    }
    
    public void procesarSolicitudesResolucion(){
        if(validarSolicitudResolucion(this.solicitudExoneracion)){
            
        }else{
            
        }
    }
    
    public boolean validarSolicitudResolucion(FnSolicitudExoneracion solicitud){
        boolean solicitudValida=true;
        
        return solicitudValida;
    }

    public Long getTipoAplicacion() {
        return tipoAplicacion;
    }

    public void setTipoAplicacion(Long tipoAplicacion) {
        this.tipoAplicacion = tipoAplicacion;
    }

    public FnSolicitudExoneracion getSolicitudExoneracion() {
        return solicitudExoneracion;
    }

    public void setSolicitudExoneracion(FnSolicitudExoneracion solicitudExoneracion) {
        this.solicitudExoneracion = solicitudExoneracion;
    }

    public List<FnExoneracionClase> getExoneracionesClasePorAplicacion() {
        return exoneracionesClasePorAplicacion;
    }

    public void setExoneracionesClasePorAplicacion(List<FnExoneracionClase> exoneracionesClasePorAplicacion) {
        this.exoneracionesClasePorAplicacion = exoneracionesClasePorAplicacion;
    }

    public FnExoneracionClase getExoneracionClasePorAplicacion() {
        return exoneracionClasePorAplicacion;
    }

    public void setExoneracionClasePorAplicacion(FnExoneracionClase exoneracionClasePorAplicacion) {
        this.exoneracionClasePorAplicacion = exoneracionClasePorAplicacion;
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public String getNumeroResolucion() {
        return numeroResolucion;
    }

    public void setNumeroResolucion(String numeroResolucion) {
        this.numeroResolucion = numeroResolucion;
    }

    public String getNumeroOficio() {
        return numeroOficio;
    }

    public void setNumeroOficio(String numeroOficio) {
        this.numeroOficio = numeroOficio;
    }

    public List<CatPredio> getPrediosUrbanos() {
        return prediosUrbanos;
    }

    public void setPrediosUrbanos(List<CatPredio> prediosUrbanos) {
        this.prediosUrbanos = prediosUrbanos;
    }

    public List<CatPredioRustico> getPrediosRurales() {
        return prediosRurales;
    }

    public void setPrediosRurales(List<CatPredioRustico> prediosRurales) {
        this.prediosRurales = prediosRurales;
    }

    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }

    public PropietariosLazy getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(PropietariosLazy propietarios) {
        this.propietarios = propietarios;
    }

    public List<CatParroquia> getParroquiasRurales() {
        return parroquiasRurales;
    }

    public void setParroquiasRurales(List<CatParroquia> parroquiasRurales) {
        this.parroquiasRurales = parroquiasRurales;
    }
    
}
