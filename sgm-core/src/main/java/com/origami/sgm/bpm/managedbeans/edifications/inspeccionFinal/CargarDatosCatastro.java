/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.managedbeans.edifications.EntCarpTec;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeDetalleInspeccion;
import com.origami.sgm.entities.PeInspeccionCabEdificacion;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.InspeccionFinalServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import util.Archivo;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class CargarDatosCatastro extends BpmManageBeanBaseRoot implements Serializable {
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected InspeccionFinalServices servicesIF;
    
    private HistoricoTramites tramite;
    private Observaciones obs;
    private HashMap<String, Object> params;
    private HistoricoTramites ht;
    private CatPredio datosPredio;
    private List<CatPredioPropietario> listPropietarios;
    private List<CatPredioEdificacion> edificacionesList;
    private List<CatEnte> enteList;
    private String cedulaRuc;
    private CatCanton canton;
    private CatParroquia parroquia;
    private Boolean aprobar;
    private PeInspeccionFinal inspeccion;
    private List<PeInspeccionCabEdificacion> edificaciones;
    private PeInspeccionCabEdificacion edificacion;
    private List<PeDetalleInspeccion> peDetalleInsList;
    private String codigoCatastral;
    private List<HistoricoReporteTramite> hrts;
    
    @PostConstruct
    public void initView() {
        List<CatPredioPropietario> propTemp;
        
        try {
            if (uSession != null && uSession.getTaskID() != null) {
                this.setTaskId(uSession.getTaskID());
                obs = new Observaciones();
                params = new HashMap<>();
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                if(ht == null)
                    return;
                hrts = services.findAll(Querys.getHistoricoReporteTramiteByEstadoAndTramiteID, new String[]{"idTramite"}, new Object[]{ht});
                datosPredio = (CatPredio) services.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{Long.parseLong(ht.getNumPredio()+"")});
                codigoCatastral = datosPredio.getSector() + "." + datosPredio.getMz() + "." + datosPredio.getCdla() + "." + datosPredio.getMzdiv() + "." + datosPredio.getSolar() + "." + datosPredio.getDiv1() + "." + datosPredio.getDiv2() + "." + datosPredio.getDiv3() + "." + datosPredio.getDiv4() + "." + datosPredio.getDiv5() + "." + datosPredio.getDiv6() + "." + datosPredio.getDiv7() + "." + datosPredio.getDiv8() + "." + datosPredio.getDiv9() + "." + datosPredio.getPhv() + "." + datosPredio.getPhh();
                inspeccion = ht.getPeInspeccionFinal();
                edificaciones = (List<PeInspeccionCabEdificacion>) inspeccion.getPeInspeccionCabEdificacionCollection();
                if(datosPredio!=null){
                    parroquia = datosPredio.getCiudadela().getCodParroquia();
                    canton = parroquia.getIdCanton();
                    edificacionesList = services.findAllEntCopy(Querys.getCatEdificacionesByPredio, new String[]{"predioId"}, new Object[]{datosPredio.getId()});
                    listPropietarios = new ArrayList<CatPredioPropietario>();
                    codigoCatastral = datosPredio.getSector() + "." + datosPredio.getMz() + "." + datosPredio.getCdla() + "." + datosPredio.getMzdiv() + "." + datosPredio.getSolar() + "." + datosPredio.getDiv1() + "." + datosPredio.getDiv2() + "." + datosPredio.getDiv3() + "." + datosPredio.getDiv4() + "." + datosPredio.getDiv5() + "." + datosPredio.getDiv6() + "." + datosPredio.getDiv7() + "." + datosPredio.getDiv8() + "." + datosPredio.getDiv9() + "." + datosPredio.getPhv() + "." + datosPredio.getPhh();

                    propTemp = (List<CatPredioPropietario>) datosPredio.getCatPredioPropietarioCollection();

                    for (CatPredioPropietario temp : propTemp) {
                        if (temp.getEstado().equals("A")) {
                            listPropietarios.add(temp);
                        }
                    }
                }
            }
        }catch (Exception e) {
            Logger.getLogger(EntCarpTec.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Selecciona una edificación y de acuerdo a ello se le enlistan las características
     * del mismo para que puedan ser vistas por el técnico de catastro.
     * 
     * @param event 
     */
    public void onRowSelect(SelectEvent event){
        peDetalleInsList = null;
        if(edificacion!=null && edificacion.getPeDetalleInspeccionCollection()!=null)
            peDetalleInsList = (List<PeDetalleInspeccion>) edificacion.getPeDetalleInspeccionCollection();
    }
    
    /**
     * Remueve un PeDetalleInspeccion de la lista.
     * 
     * @param detalleIns 
     */
    public void eliminarPermiso(PeDetalleInspeccion detalleIns){
        if(detalleIns.getId()!=null){
            detalleIns.setEstado(false);
            services.update(detalleIns);
        }
        peDetalleInsList.remove(detalleIns);
    }
    
    /**
     * Agrega un propietario.
     * 
     * @param ente 
     */
    public void agregarPropietario(CatEnte ente) {
        CatPredioPropietario propTemp = new CatPredioPropietario();
        for(CatPredioPropietario cpp : listPropietarios){
            if(cpp.getEnte().equals(ente) && cpp.getEstado().equals("A")){
                JsfUti.messageError(null, "Error", "El usuario ya ha sido agregado anteriormente. No lo puede volver a agregar.");
                return;
            }
        }
        
        if (!listPropietarios.isEmpty()) {
            propTemp.setPredio(listPropietarios.get(0).getPredio());
            propTemp.setTipo(listPropietarios.get(0).getTipo());
            propTemp.setEsResidente(listPropietarios.get(0).getEsResidente());
            propTemp.setEstado(listPropietarios.get(0).getEstado());
            propTemp.setModificado(listPropietarios.get(0).getModificado());
            if (propTemp != null) {
                propTemp.setEnte(ente);
                listPropietarios.add(propTemp);
                enteList.remove(0);
                cedulaRuc = "";
            }
        }
    }
    
    /**
     * Remueve un propietario de la lista de propietarios.
     * 
     * @param prop 
     */
    public void eliminarPropietario(CatPredioPropietario prop) {
        if(prop.getId()!=null){
            prop.setEstado("I");
            services.update(prop);
        }
        listPropietarios.remove(prop);
    }
    
    /**
     * Si todos los valores fueron ingresados correctamente, muestra el dialog para
     * el ingreso de la observación de la tarea.
     * 
     * @param aprobado 
     */
    public void mostrarObservaciones(boolean aprobado) {
        aprobar = aprobado;
        JsfUti.executeJS("PF('obs').show();");
        JsfUti.update("frmObs");
    }
    
    /**
     * Método que busca un ente por su número de cédula/ruc y lo almacena en
     * memoria.
     * 
     */
    public void buscarEnte() {
        //CatEnte tempEnte = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        CatEnte tempEnte = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        if (tempEnte != null) {
            enteList.add(tempEnte);
            cedulaRuc = "";
        }
    }
    
    /**
     * El técnico luego de observar toda la información tiene la opción de aprobar 
     * o rechar la tarea. Si lo aprueba se actualiza el predio con las nuevas 
     * edificaciones y termina el proceso, caso contrario regresa al director de 
     * edificaciones para que lo revise de nuevo.
     */
    public void completarTarea(){
        ArrayList<Archivo> archivos = this.getFiles();
        try{
            if (!obs.getObservacion().equals("")) {
                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setUserCre(uSession.getName_user());
                obs.setTarea("Aprobación de Catastro");

                params.put("aprobado", aprobar);
                if(archivos != null && !archivos.isEmpty()){
                    this.params.put("listaArchivos", archivos);
                    this.params.put("listaArchivosFinal", new ArrayList<Archivo>());
                    params.put("tdocs", true);
                }else
                    params.put("tdocs", false);

                if(aprobar)
                    servicesIF.actualizarDatosPredio(datosPredio, inspeccion);
                ht.setEstado("Finalizado");
                services.update(ht);
                services.persist(obs);
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            }else{
                JsfUti.messageError(null, "Error", "No ha ingresado las observaciones de la tarea");
            }
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

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
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

    public List<CatPredioEdificacion> getEdificacionesList() {
        return edificacionesList;
    }

    public void setEdificacionesList(List<CatPredioEdificacion> edificacionesList) {
        this.edificacionesList = edificacionesList;
    }

    public List<CatPredioPropietario> getListPropietarios() {
        return listPropietarios;
    }

    public void setListPropietarios(List<CatPredioPropietario> listPropietarios) {
        this.listPropietarios = listPropietarios;
    }

    public List<CatEnte> getEnteList() {
        return enteList;
    }

    public void setEnteList(List<CatEnte> enteList) {
        this.enteList = enteList;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public List<PeInspeccionCabEdificacion> getEdificaciones() {
        return edificaciones;
    }

    public void setEdificaciones(List<PeInspeccionCabEdificacion> edificaciones) {
        this.edificaciones = edificaciones;
    }

    public PeInspeccionCabEdificacion getEdificacion() {
        return edificacion;
    }

    public void setEdificacion(PeInspeccionCabEdificacion edificacion) {
        this.edificacion = edificacion;
    }

    public List<PeDetalleInspeccion> getPeDetalleInsList() {
        return peDetalleInsList;
    }

    public void setPeDetalleInsList(List<PeDetalleInspeccion> peDetalleInsList) {
        this.peDetalleInsList = peDetalleInsList;
    }

    public String getCodigoCatastral() {
        return codigoCatastral;
    }

    public void setCodigoCatastral(String codigoCatastral) {
        this.codigoCatastral = codigoCatastral;
    }

    public List<HistoricoReporteTramite> getHrts() {
        return hrts;
    }

    public void setHrts(List<HistoricoReporteTramite> hrts) {
        this.hrts = hrts;
    }

    public Boolean getAprobar() {
        return aprobar;
    }

    public void setAprobar(Boolean aprobar) {
        this.aprobar = aprobar;
    }
    
}
