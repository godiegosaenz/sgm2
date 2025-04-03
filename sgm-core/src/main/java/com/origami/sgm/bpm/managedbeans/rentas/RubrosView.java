/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.RenTipoValor;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
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
public class RubrosView implements Serializable {

    private static final Logger LOG = Logger.getLogger(RubrosView.class.getName());
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected RentasServices servicesRentas;
    
    @Inject
    private ServletSession ss;
    
    private RenTipoLiquidacion tipoLiquidacion;
    private List<RenRubrosLiquidacion> rubrosList;
    private BaseLazyDataModel<RenRubrosLiquidacion> rubrosLazy;
    private RenRubrosLiquidacion rubro;
    private RenTipoValor tipoValor;
    
    @PostConstruct
    public void initView() {  
        if(uSession.esLogueado()){
            Long idTipoLiq = (Long)ss.retornarValor("idTipoLiquidacion");
            System.out.println("Tipo Liquidacion: " + idTipoLiq);
            tipoLiquidacion = (RenTipoLiquidacion)services.find(RenTipoLiquidacion.class, idTipoLiq);
            rubrosList = services.findAll(QuerysFinanciero.getRubrosByTipoLiquidacionCodRubroASC, new String[]{"tipoLiq"}, new Object[]{idTipoLiq});
            rubrosLazy = new BaseLazyDataModel<>(RenRubrosLiquidacion.class);
            JsfUti.update("frmMain");
        }
    }
    
    public void nuevoRubro(){
        rubro = new RenRubrosLiquidacion();
        rubro.setRubroDelMunicipio(Boolean.TRUE);
        rubro.setEstado(Boolean.TRUE);
        tipoValor = new RenTipoValor();
    }
    
    public void editarRubro(RenRubrosLiquidacion rl ){
        try {
            this.rubro = rl;
            tipoValor = new RenTipoValor();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public void guardarRubro(){
        try{
            if(rubro.getCtaContable() == null || rubro.getCtaOrden() == null || rubro.getCtaPresupuesto() == null || 
                    rubro.getDescripcion()== null  ){
                JsfUti.messageInfo(null, "Info", "Faltan datos del rubro");
                return;
            }
            System.out.println("tipoValor " + tipoValor);
            rubro.setTipoLiquidacion(null);
            rubro.setTipoValor(tipoValor);
            rubro = servicesRentas.guardarRubroNuevo(rubro, rubro.getTipoLiquidacion());            
//            if(rubrosList == null || rubrosList.isEmpty())
//                rubrosList = new ArrayList();
//            rubrosList.add(rubro);
            JsfUti.messageInfo(null, "Info", "Rubro guardado correctamente");
        }catch(Exception e){
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public void guardarEdicionRubro(){
        try {
            rubro.setTipoValor(tipoValor);
            services.persist(rubro);
            JsfUti.messageInfo(null, "Info", "Rubro guardado correctamente");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public void eliminarRubro(Integer index){
        RenRubrosLiquidacion temp = rubrosList.get(index);
        rubrosList.remove(temp);
        if(servicesRentas.eliminarRubro(temp, tipoLiquidacion)){
            JsfUti.messageInfo(null, "Info", "Rubro eliminado correctamente");
        }else{
            JsfUti.messageInfo(null, "Info", "Ocurrió un problema al eliminar el rubro");
        }
        
    }
    
    public void guardarRubros(){
        try{
            servicesRentas.guardarRubrosPorTipoLiquidacion(rubrosList);
            JsfUti.messageInfo(null, "Info", "Rubros guardados correctamente");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void agregarRubro(RenRubrosLiquidacion rubro){
        try{
            System.out.println("TIPO VALOR" + rubro.getTipoValor());
            this.rubro = servicesRentas.guardarRubroNuevo(rubro, tipoLiquidacion);
            //this.rubro.setTipoLiquidacion(tipoLiquidacion);
            //services.update(this.rubro);
            if(rubrosList == null)
                    rubrosList = new ArrayList();
            if(!rubrosList.contains(rubro)){
                rubrosList.add(this.rubro);
            }
            
            JsfUti.messageInfo(null, "Info", "Se agregó el rubro correctamente");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public RenTipoLiquidacion getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(RenTipoLiquidacion tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public List<RenRubrosLiquidacion> getRubrosList() {
        return rubrosList;
    }

    public void setRubrosList(List<RenRubrosLiquidacion> rubrosList) {
        this.rubrosList = rubrosList;
    }

    public BaseLazyDataModel<RenRubrosLiquidacion> getRubrosLazy() {
        return rubrosLazy;
    }

    public void setRubrosLazy(BaseLazyDataModel<RenRubrosLiquidacion> rubrosLazy) {
        this.rubrosLazy = rubrosLazy;
    }

    public RenRubrosLiquidacion getRubro() {
        return rubro;
    }

    public void setRubro(RenRubrosLiquidacion rubro) {
        this.rubro = rubro;
    }

    public RenTipoValor getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(RenTipoValor tipoValor) {
        this.tipoValor = tipoValor;
    }
    
}
