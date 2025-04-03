/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.divisionPredio;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCategoriasConstruccion;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import java.io.Serializable;
import java.util.ArrayList;
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
public class AgregarEdificaciones extends BpmManageBeanBaseRoot implements Serializable {
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    private HistoricoTramites ht;
    private CatPredio datosPredio, cpTemp;
    private List<CatPredio> prediosHijos;
    private Boolean enEdicion;
    private CatPredioEdificacion edificacionNew, edificacionEdit;
    private List<CatCategoriasConstruccion> catConsList;
    private List<CatPredioEdificacion> edificacionesList;
    private List<CtlgItem> estadoEdifList;
    private CatEdfCategProp cecp;
    private List<CatEdfCategProp> cecpList;
    private List<CatPredioPropietario> propietariosList;
    private List<CatPredioEdificacion> edificacionListTemp;
    private Integer cont;
    
    private Boolean nextPage;
    
    @PostConstruct
    public void init() {
        try {
            int num=0;
            String propietarios = "";
            
            if (uSession != null && uSession.getTaskID() != null) {
                this.setTaskId(uSession.getTaskID());
                nextPage = true;
                enEdicion = false;
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                estadoEdifList = servicesDP.obtenerCtlgItemListByNombreDeCatalogo("edif.estado_cons");
                cecpList = servicesDP.obtenerCatCategoriasPropConstruccion();
                if (ht != null) {
                    datosPredio = servicesDP.obtenerCatPredioPorQuery(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                }
                prediosHijos = servicesDP.obtenerCatPrediosHijos(Querys.getPredioHijosByFatherID, new String[]{"numPredio"}, new Object[]{datosPredio.getId()});
                
                for(CatPredio cp : prediosHijos){
                    propietariosList = services.findAll(Querys.getCatPropietariosByPredio, new String[]{"id"}, new Object[]{cp.getId()});
                    edificacionListTemp = services.findAll(Querys.getCatEdificacionesByPredio, new String[]{"predioId"}, new Object[]{cp.getId()});
                    if(edificacionListTemp == null || edificacionListTemp.isEmpty())
                        nextPage = false;   
                    
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
                if(nextPage){
                    JsfUti.redirectFaces("/vistaprocesos/edificaciones/divisionPredio/agregarCatPredioS12.xhtml");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Setea un predio y le extrae sus datos para ser manipulados posteriormente.
     * 
     * @param cp 
     */
    public void setearPredio(CatPredio cp){
        cpTemp = cp;
        cont = 1;
        if(cpTemp.getCatPredioEdificacionCollection() == null)
            cpTemp.setCatPredioEdificacionCollection(new ArrayList<CatPredioEdificacion>());
        edificacionesList = (List<CatPredioEdificacion>) cpTemp.getCatPredioEdificacionCollection();
        enEdicion = true;
    }
    
    /**
     * Al agregar una nueva edificación se suma en 1 el contador para asignárselo
     * como número de edificación.
     */
    public void guardarEdifNew(){
        edificacionesList.add(edificacionNew);
        cont++;
    }
    
    /**
     * Instancia una nueva edificación para ser agregada a la lista de edificaciones
     * del predio.
     */
    public void agregarEdificacion(){
        edificacionNew = new CatPredioEdificacion();
        edificacionNew.setNoEdificacion(Short.valueOf(this.generadorCeroALaIzquierda(new Long(cont))+""));
    }
    
    /**
     * Variable que indica si la edificación se está editando.
     */
    public void terminarDeAgregarEdificaciones(){
        enEdicion = false;
    }
    
    /**
     * Método que procede a guardar las edificaciones de cada uno de los predios
     * generados.
     * 
     */
    public void guardarEdificaciones(){
        Boolean next = true;
        for(CatPredio cp : prediosHijos){
            if(cp.getCatPredioEdificacionCollection() == null || cp.getCatPredioEdificacionCollection().isEmpty()){
                next = false;
                break;
            }
        }
        if(next){
            if(servicesDP.guardarEdificaciones(prediosHijos))
                JsfUti.redirectFaces("/vistaprocesos/edificaciones/divisionPredio/agregarCatPredioS12.xhtml");
            else{
                JsfUti.messageError(null, "Error", "Alguna de las edificaciones no ha sido llenado correctamente. Asegúrese de que todas las edificaciones sean llenados por completo antes de guardar.");
                return;
            }
        }else{
            JsfUti.messageError(null, "Error", "Uno o más predios no tienen ninguna edificación. Asegúrese de agregarle al menos una edificación a todos los predios antes de guardar.");
            return;
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

    public Boolean getEnEdicion() {
        return enEdicion;
    }

    public void setEnEdicion(Boolean enEdicion) {
        this.enEdicion = enEdicion;
    }

    public CatPredioEdificacion getEdificacionNew() {
        return edificacionNew;
    }

    public void setEdificacionNew(CatPredioEdificacion edificacionNew) {
        this.edificacionNew = edificacionNew;
    }

    public CatPredioEdificacion getEdificacionEdit() {
        return edificacionEdit;
    }

    public void setEdificacionEdit(CatPredioEdificacion edificacionEdit) {
        this.edificacionEdit = edificacionEdit;
    }

    public List<CatCategoriasConstruccion> getCatConsList() {
        return catConsList;
    }

    public void setCatConsList(List<CatCategoriasConstruccion> catConsList) {
        this.catConsList = catConsList;
    }

    public List<CatPredioEdificacion> getEdificacionesList() {
        return edificacionesList;
    }

    public void setEdificacionesList(List<CatPredioEdificacion> edificacionesList) {
        this.edificacionesList = edificacionesList;
    }

    public List<CtlgItem> getEstadoEdifList() {
        return estadoEdifList;
    }

    public void setEstadoEdifList(List<CtlgItem> estadoEdifList) {
        this.estadoEdifList = estadoEdifList;
    }

    public CatEdfCategProp getCecp() {
        return cecp;
    }

    public void setCecp(CatEdfCategProp cecp) {
        this.cecp = cecp;
    }

    public List<CatEdfCategProp> getCecpList() {
        return cecpList;
    }

    public void setCecpList(List<CatEdfCategProp> cecpList) {
        this.cecpList = cecpList;
    }

}
