/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.divisionPredio;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS12;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PePermiso;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
public class AgregarCatPredioS12 extends BpmManageBeanBaseRoot implements Serializable {
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;
    
    private HistoricoTramites ht, htTemp;
    private CatPredio datosPredio, cpTemp;
    private List<CatPredio> prediosHijos;
    private Observaciones obs;
    private List<CtlgItem> usosList;
    private HashMap<String, Object> paramsActiviti;
    private List<CatPredioPropietario> propietariosList;
    private PePermiso pePermiso;
    private PeInspeccionFinal inspeccionFinal;
    private Boolean tienePermiso;
    
    @PostConstruct
    public void init() {
        try {
            int num=0;
            String propietarios = "";
            
            if (uSession != null && uSession.getTaskID() != null) {
                tienePermiso = false;
                paramsActiviti = new HashMap<>();
                this.setTaskId(uSession.getTaskID());
                obs = new Observaciones();
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                if (ht != null) {
                    datosPredio = servicesDP.obtenerCatPredioPorQuery(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                }
                prediosHijos = servicesDP.obtenerCatPrediosHijos(Querys.getPredioHijosByFatherID, new String[]{"numPredio"}, new Object[]{datosPredio.getId()});
                usosList = servicesDP.obtenerCtlgItemListByNombreDeCatalogo("predio.uso");
                
                for(CatPredio cp : prediosHijos){
                    propietariosList = services.findAll(Querys.getCatPropietariosByPredio, new String[]{"id"}, new Object[]{cp.getId()});
                    
                    // Obtengo los nombres de los propietarios, ya que no los puedo tener por getter.
                    if(propietariosList != null){
                        for(CatPredioPropietario cpp : propietariosList){
                            num++;

                            if(num < propietariosList.size())
                                propietarios = propietarios + cpp.getEnte().getNombres() + " " + cpp.getEnte().getApellidos()+" - ";
                            else
                                propietarios = propietarios + cpp.getEnte().getNombres() + " " + cpp.getEnte().getApellidos();
                        }
                        cp.setPropietarios(propietarios);
                        propietarios = "";
                        num = 0;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    /**
     * Setea el predio y le instancia campos si fueran necesarios para agregarlos en
     * el facelete, sino saldría error de null.
     * 
     * @param cp 
     */
    public void setearPredio(CatPredio cp){
        cpTemp = cp;
        if(cpTemp.getCatPredioS12()== null)
            cpTemp.setCatPredioS12(new CatPredioS12());
        if(cpTemp.getCatPredioS12().getUsosList() == null)
            cpTemp.getCatPredioS12().setUsosList(new ArrayList<CtlgItem>());
    }
    
    /**
     * Le agrega información al CatPredioS12 del predio.
     */
    public void agregarInformacion(){
        if(!tienePermiso)
            cpTemp.getCatPredioS12().setNextStep(true);
        else{ 
            if(tienePermiso && pePermiso!=null && inspeccionFinal!=null){
                cpTemp.getCatPredioS12().setNextStep(true);
                cpTemp.getCatPredioS12().setFechaPermiso(pePermiso.getFechaEmision());
                cpTemp.getCatPredioS12().setResponsablePermiso(pePermiso.getResponsable());
                cpTemp.getCatPredioS12().setNumInspeccionFinal(inspeccionFinal.getId()+"");
                cpTemp.getCatPredioS12().setFechaInspeccionFinal(inspeccionFinal.getFechaInspeccion());
                cpTemp.getCatPredioS12().setAreaConsInspeccion(inspeccionFinal.getAreaConst());
            }
        }
        pePermiso = null;
    }
    
    /**
     * Busca un permiso de construcción y a partir de él adquiere la inspección
     * final.
     * 
     */
    public void buscarPermisoDeConstruccion(){
        pePermiso = new PePermiso();
        inspeccionFinal = new PeInspeccionFinal();
        List<PeInspeccionFinal> listTemp;
        try{
            String id = this.cpTemp.getCatPredioS12().getNumPermisoConstruccion();
            if(id == null)
                return;
            pePermiso = (PePermiso) services.find(Querys.getPePermisoById, new String[]{"id"}, new Object[]{Long.parseLong(id)});
            if(pePermiso!=null){
                JsfUti.messageInfo(null, "Info", "Se encontró un permiso.");
                listTemp = services.findAllEntCopy(Querys.getPeInspeccionByPermisoID, new String[]{"idPermiso"}, new Object[]{pePermiso.getId()});
                if(listTemp != null && !listTemp.isEmpty()){
                    inspeccionFinal = listTemp.get(0);
                    JsfUti.messageInfo(null, "Info", "Se encontró la inspección asociada.");
                }
                else
                    JsfUti.messageError(null, "Error", "No se encontró la inspección asociada.");
            }else
                JsfUti.messageError(null, "Error", "No se encontró ningún permiso.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Guarda los CatPredioS12 de todas las divisiones del predio que se hicieron.
     */
    public void guardarCatPredioS12(){
        Boolean next = true;
        for(CatPredio cp : prediosHijos){
            if(cp.getCatPredioS12()== null){
                next = false;
                JsfUti.messageError(null, "Error", "Toda la información debe estar habilitada antes de proceder a completar la tarea.");
                break;
            }else if(!cp.getCatPredioS12().getNextStep()){
                next = false;
                JsfUti.messageError(null, "Error", "Toda la información debe estar habilitada antes de proceder a completar la tarea.");
                break;
            }
        }
        if(next){
            if(servicesDP.guardarCatPredioS12(prediosHijos)){
                try {            
                    obs.setEstado(Boolean.TRUE);
                    obs.setFecCre(new Date());
                    obs.setIdTramite(ht);
                    obs.setUserCre(uSession.getName_user());
                    obs.setTarea(this.getTaskDataByTaskID().getName());
                    ht.setEstado("Finalizado");
                    services.update(ht);
                    servicesDP.guardarObservacion(obs);
                    paramsActiviti.put("aprobado", true);
                    paramsActiviti.put("tdocs", false);
                    this.completeTask(this.getTaskId(), paramsActiviti);
                    this.continuar();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                JsfUti.messageError(null, "Error", "Uno o más campos de uno de los predios no han sido ingresados. Asegúrese de llenar todos los campos antes de guardar.");
                return;
            }                
        }
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public CatPredio getDatosPredio() {
        return datosPredio;
    }

    public void setDatosPredio(CatPredio datosPredio) {
        this.datosPredio = datosPredio;
    }

    public CatPredio getCpTemp() {
        return cpTemp;
    }

    public void setCpTemp(CatPredio cpTemp) {
        this.cpTemp = cpTemp;
    }

    public List<CatPredio> getPrediosHijos() {
        return prediosHijos;
    }

    public void setPrediosHijos(List<CatPredio> prediosHijos) {
        this.prediosHijos = prediosHijos;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public List<CtlgItem> getUsosList() {
        return usosList;
    }

    public void setUsosList(List<CtlgItem> usosList) {
        this.usosList = usosList;
    }

    public PePermiso getPePermiso() {
        return pePermiso;
    }

    public void setPePermiso(PePermiso pePermiso) {
        this.pePermiso = pePermiso;
    }

    public PeInspeccionFinal getInspeccionFinal() {
        return inspeccionFinal;
    }

    public void setInspeccionFinal(PeInspeccionFinal inspeccionFinal) {
        this.inspeccionFinal = inspeccionFinal;
    }

    public Boolean getTienePermiso() {
        return tienePermiso;
    }

    public void setTienePermiso(Boolean tienePermiso) {
        this.tienePermiso = tienePermiso;
    }
    
}
