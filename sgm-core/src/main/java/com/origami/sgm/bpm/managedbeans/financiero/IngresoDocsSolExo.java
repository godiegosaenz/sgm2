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
import util.Archivo;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class IngresoDocsSolExo extends BpmManageBeanBaseRoot implements Serializable{
    private static final Logger LOG = Logger.getLogger(IngresoSolCatFinView.class.getName());

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;
    
    private FnSolicitudExoneracion sol;
    private HistoricoTramites ht;
    private List<CatPredio> predios;
    private List<CatPredioRustico> rusticos;
    private Observaciones obs;
    private String numsPredio = "", numsPredioRustico = "";
    
    @PostConstruct
    public void init(){
        if (uSession.esLogueado()) {
            System.out.println("uSession.getTaskID()" + uSession.getTaskID());
            this.setTaskId(uSession.getTaskID());
            if(this.getVariable(uSession.getTaskID(), "tramite") != null){
                obs = new Observaciones();
                predios = new ArrayList();
                rusticos = new ArrayList();
                ht = (HistoricoTramites)services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{(Long)this.getVariable(this.getTaskId(), "tramite")});
                
                sol = (FnSolicitudExoneracion)services.find(QuerysFinanciero.getSolicitudExoneracionByTramite, new String[]{"idTramite", "estado"}, new Object[]{ht, 2L});
                for(HistoricoTramiteDet temp : ht.getHistoricoTramiteDetCollection()){
                    if(temp.getPredio() != null){
                        predios.add(temp.getPredio());
                        numsPredio = numsPredio + temp.getPredio().getNumPredio()+"\n";
                    }
                    if(temp.getPredioRustico() != null){
                        rusticos.add(temp.getPredioRustico());
                        numsPredioRustico = numsPredioRustico + temp.getPredioRustico().getIdPredial()+"\n";
                    }
                }
            }
        }
    }
    
    public void validarDoc(){
        if(this.getFiles() == null || this.getFiles().isEmpty()){
            JsfUti.messageInfo(null, "Info", "Debe ingresar un documento");
        }else{
            JsfUti.executeJS("PF('obs').show()");
        }
    }
    
    public void completarTarea(){
        HashMap<String, Object> paramsActiviti = new HashMap();
        obs.setEstado(Boolean.TRUE);
        obs.setFecCre(new Date());
        obs.setIdTramite(ht);
        obs.setUserCre(uSession.getName_user());
        obs.setTarea(this.getTaskDataByTaskID().getName());
            
        services.persist(obs);
        paramsActiviti.put("carpeta", ht.getTipoTramite().getCarpeta());            
        paramsActiviti.put("listaArchivos", this.getFiles());
        //paramsActiviti.put("listaArchivosFinal", new ArrayList<Archivo>());
        paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());        
        paramsActiviti.put("prioridad", 50);
        this.completeTask(this.getTaskId(), paramsActiviti);
        this.continuar();
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

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public String getNumsPredio() {
        return numsPredio;
    }

    public void setNumsPredio(String numsPredio) {
        this.numsPredio = numsPredio;
    }

    public String getNumsPredioRustico() {
        return numsPredioRustico;
    }

    public void setNumsPredioRustico(String numsPredioRustico) {
        this.numsPredioRustico = numsPredioRustico;
    }
    
}
