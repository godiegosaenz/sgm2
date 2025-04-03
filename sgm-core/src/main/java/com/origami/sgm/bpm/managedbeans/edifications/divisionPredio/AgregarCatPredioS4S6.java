/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.divisionPredio;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CatTiposDominio;
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
public class AgregarCatPredioS4S6 extends BpmManageBeanBaseRoot implements Serializable {
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;
    
    private HistoricoTramites ht;
    private CatPredio datosPredio, cpTemp;
    private List<CatPredio> prediosHijos;
    
    // Listas con opciones para seleccionar
    private List<CtlgItem> cerramientosList;
    private List<CtlgItem> tipoLocalList;
    private List<CtlgItem> accesibilidadList;
    private List<CtlgItem> accesibilidadPredioS4;
    private List<CatCanton> cantonList;
    private List<CatTiposDominio> tipoDominioList;
    private List<CatPredioPropietario> propietariosList;
    
    private CatEscritura escrituraPredio;
    private Boolean nextPage;
    //private List<CatPredioPropietario> cppList;
    //CatPredioPropietario cppprueba;
    
    @PostConstruct
    public void init() {
        int num=0;
        String propietarios = "";
        
        try {
            if (uSession != null && uSession.getTaskID() != null) {
                this.setTaskId(uSession.getTaskID());
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                nextPage = true;
                if (ht != null) {
                    datosPredio = servicesDP.obtenerCatPredioPorQuery(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                }
                prediosHijos = servicesDP.obtenerCatPrediosHijos(Querys.getPredioHijosByFatherID, new String[]{"numPredio"}, new Object[]{datosPredio.getId()});
                cerramientosList = servicesDP.obtenerCtlgItemListByNombreDeCatalogo("predio.cerramiento");
                tipoLocalList = servicesDP.obtenerCtlgItemListByNombreDeCatalogo("predio.loc_manzana");
                accesibilidadList = servicesDP.obtenerCtlgItemListByNombreDeCatalogo("predio.accesibilidad");
                tipoDominioList = servicesDP.obtenerTipoDominioList();
                cantonList = servicesDP.obtenerCantonesList();
                for(CatPredio cp : prediosHijos){
                    propietariosList = services.findAll(Querys.getCatPropietariosByPredio, new String[]{"id"}, new Object[]{cp.getId()});
                    
                    if(cp.getCatPredioS4() == null || cp.getCatPredioS4().getId() == null){
                        nextPage = false;
                    }
                    if(cp.getCatPredioS6() == null || cp.getCatPredioS6().getId() == null){
                        nextPage = false;
                    }
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
                if(nextPage){
                    JsfUti.redirectFaces("/vistaprocesos/edificaciones/divisionPredio/agregarEdificaciones.xhtml");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
    * Guarda los CatPredioS12 de todas las divisiones del predio que se hicieron.
    */
    public void guardarCatPredioS4S6(){
        Boolean next = true;
        for(CatPredio cp : prediosHijos){
            if(cp.getCatPredioS4() == null){
                next = false;
                break;
            }
            if(cp.getCatPredioS6() == null){
                next = false;
                break;
            }
        }
        if(next){
            if(servicesDP.guardarCatPredioS4S6(prediosHijos, ht))
                JsfUti.redirectFaces("/vistaprocesos/edificaciones/divisionPredio/agregarEdificaciones.xhtml");
            else{
                JsfUti.messageError(null, "Error", "Uno o más campos de uno de los predios no han sido ingresados. Asegúrese de llenar todos los campos antes de guardar.");
                return;
            }                
        }else{
            JsfUti.messageError(null, "Error", "Uno o más predios no tienen características. Asegúrese de agregarle características a todos los predios antes de guardar.");
            return;
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
        List list;
        if(cpTemp.getCatPredioS4() == null)
            cpTemp.setCatPredioS4(new CatPredioS4());
        if(cpTemp.getCatPredioS6() == null)
            cpTemp.setCatPredioS6(new CatPredioS6());
        if(cpTemp.getCatPredioS4().getAccesibilidadList() == null)
            cpTemp.getCatPredioS4().setAccesibilidadList(new ArrayList<CtlgItem>());
        if(cpTemp.getCatEscrituraCollection() == null || cpTemp.getCatEscrituraCollection().isEmpty()){
            escrituraPredio = new CatEscritura();
            cpTemp.setCatEscrituraCollection(new ArrayList());
        }else{
            list = (List)cpTemp.getCatEscrituraCollection();
            escrituraPredio = (CatEscritura) list.get(0);
        }
    }
    
    /**
     * Agrega una escritura al predio.
     */
    public void setearEscrituraAlPredio(){
        List list;
        if(cpTemp.getCatEscrituraCollection().isEmpty()){
            list = (List) cpTemp.getCatEscrituraCollection();
            list.add(escrituraPredio);
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

    public List<CatPredio> getPrediosHijos() {
        return prediosHijos;
    }

    public void setPrediosHijos(List<CatPredio> prediosHijos) {
        this.prediosHijos = prediosHijos;
    }

    public CatPredio getCpTemp() {
        return cpTemp;
    }

    public void setCpTemp(CatPredio cpTemp) {
        this.cpTemp = cpTemp;
    }

    public List<CtlgItem> getCerramientosList() {
        return cerramientosList;
    }

    public void setCerramientosList(List<CtlgItem> cerramientosList) {
        this.cerramientosList = cerramientosList;
    }

    public List<CtlgItem> getTipoLocalList() {
        return tipoLocalList;
    }

    public void setTipoLocalList(List<CtlgItem> tipoLocalList) {
        this.tipoLocalList = tipoLocalList;
    }

    public List<CtlgItem> getAccesibilidadPredioS4() {
        return accesibilidadPredioS4;
    }

    public void setAccesibilidadPredioS4(List<CtlgItem> accesibilidadPredioS4) {
        this.accesibilidadPredioS4 = accesibilidadPredioS4;
    }

    public CatEscritura getEscrituraPredio() {
        return escrituraPredio;
    }

    public void setEscrituraPredio(CatEscritura escrituraPredio) {
        this.escrituraPredio = escrituraPredio;
    }

    public List<CtlgItem> getAccesibilidadList() {
        return accesibilidadList;
    }

    public void setAccesibilidadList(List<CtlgItem> accesibilidadList) {
        this.accesibilidadList = accesibilidadList;
    }

    public List<CatCanton> getCantonList() {
        return cantonList;
    }

    public void setCantonList(List<CatCanton> cantonList) {
        this.cantonList = cantonList;
    }

    public List<CatTiposDominio> getTipoDominioList() {
        return tipoDominioList;
    }

    public void setTipoDominioList(List<CatTiposDominio> tipoDominioList) {
        this.tipoDominioList = tipoDominioList;
    }
    
}
