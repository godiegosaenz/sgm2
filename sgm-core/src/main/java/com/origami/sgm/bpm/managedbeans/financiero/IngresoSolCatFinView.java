/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.financiero;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class IngresoSolCatFinView extends BpmManageBeanBaseRoot implements Serializable{
    private static final Logger LOG = Logger.getLogger(IngresoSolCatFinView.class.getName());

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;
    
    private FnSolicitudExoneracion sol;
    private HistoricoTramites ht;
    private List<CatPredio> predios;
    private List<CatPredioRustico> rusticos;
    private Boolean es_catastro;
    private Boolean aceptar;
    private Observaciones obs;
    
    @PostConstruct
    public void init(){
        if (uSession.esLogueado()) {
            this.setTaskId(uSession.getTaskID());
            if(this.getVariable(uSession.getTaskID(), "tramite") != null){
                obs = new Observaciones();
                predios = new ArrayList();
                rusticos = new ArrayList();
                ht = (HistoricoTramites)services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{(Long)this.getVariable(this.getTaskId(), "tramite")});
                
                sol = (FnSolicitudExoneracion)services.find(QuerysFinanciero.getSolicitudExoneracionByTramite, new String[]{"idTramite", "estado"}, new Object[]{ht, 2L});
                for(HistoricoTramiteDet temp : ht.getHistoricoTramiteDetCollection()){
                    if(temp.getPredio() != null)
                        predios.add(temp.getPredio());
                    if(temp.getPredioRustico() != null)
                        rusticos.add(temp.getPredioRustico());
                }
                es_catastro = (Boolean)this.getVariable(this.getTaskId(), "es_catastro");
            }else{
                return;
            }
        }
    }
    
    public void completarTarea(){
        try{
            HashMap<String, Object> paramt = new HashMap<>();
            
            paramt.put("aprobado", aceptar);
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea("Ingreso de solicitud de exoneración");
            services.persist(obs);
            this.completeTask(uSession.getTaskID(), paramt);
            this.continuar();
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

    public FnSolicitudExoneracion getSol() {
        return sol;
    }

    public void setSol(FnSolicitudExoneracion sol) {
        this.sol = sol;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public List<CatPredio> getPredios() {
        return predios;
    }

    public void setPredios(List<CatPredio> predios) {
        this.predios = predios;
    }

    public List<CatPredioRustico> getRusticos() {
        return rusticos;
    }

    public void setRusticos(List<CatPredioRustico> rusticos) {
        this.rusticos = rusticos;
    }

    public Boolean getAceptar() {
        return aceptar;
    }

    public void setAceptar(Boolean aceptar) {
        this.aceptar = aceptar;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }
    
}
