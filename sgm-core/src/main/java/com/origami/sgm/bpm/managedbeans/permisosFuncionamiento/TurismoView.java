/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.permisosFuncionamiento;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.sgm.entities.RenTurismo;
import com.origami.sgm.entities.RenTurismoDetalleHoteles;
import com.origami.sgm.entities.RenTurismoServicios;
import com.origami.sgm.entities.models.HabitacionTurismo;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.managedbeans.component.Busquedas;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@ManagedBean
@ViewScoped
public class TurismoView extends Busquedas implements Serializable {

    public static Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(TurismoView.class.getName());

    @javax.inject.Inject
    private RentasServices services;
    @Inject
    private UserSession session;
    @javax.inject.Inject
    private Entitymanager servicesACL;
    @Inject
    private ServletSession servletSession;
    @Inject
    private ReportesView reportes;
    
    private PdfReporte reporte;
    
    private Map<String, Object> entradas;
    private HistoricoTramites ht;
    private RenPermisosFuncionamientoLocalComercial permiso;
    private RenTurismo turismo;
    private List<RenTurismoServicios> serviciosGenerales;
    private List<RenTurismoServicios> transportesTerrestre;
    private List<RenTurismoServicios> transportesAereo;
    private List<RenTurismoServicios> transportesMaritimo;
    private List<RenTurismoServicios> serviciosAlimenticios;
    private List<RenTurismoServicios> habitaciones;
    private CatEnteLazy entesList;
     
    //
    
    private RenTurismoServicios servicioGeneral;
    private RenTurismoServicios transporteTerrestre;
    private RenTurismoServicios transporteAereo;
    private RenTurismoServicios transporteMaritimo;
    private RenTurismoServicios servicioAlimenticio;
    private RenTurismoServicios habitacion;
    private List<HabitacionTurismo> detallesList;
    private Boolean mostrarGuardarDatos, esPropietario;
    
    @PostConstruct
    public void initView() {
        try {
            if (session.esLogueado() && session.getTaskID() != null) {
                entradas = new HashMap<>();
            
                this.setTaskId(session.getTaskID());
                String var = (String) this.getVariable(session.getTaskID(), "tramite").toString();
                
                ht = services.permisoServices().getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
                if (ht != null) {
                    permiso = ht.getPermisoDeFuncionamientoLC();
                    turismo = new RenTurismo();
                    entradas.put("obs", new Observaciones());
                    entradas.put("localComercialList", new ArrayList<RenLocalComercial>());
                    entesList = new CatEnteLazy();
                    
                    ((List)entradas.get("localComercialList")).add(permiso.getLocalComercial());
                    turismo.setPermisoFuncionamiento(permiso);
                    turismo.setUsuarioIngreso(session.getName_user());
                    
                    serviciosGenerales = (List)servicesACL.findAll(QuerysFinanciero.getRenTurismoServiciosByTipo, new String[]{"tipo"}, new Object[]{1L});
                    transportesTerrestre = (List)servicesACL.findAll(QuerysFinanciero.getRenTurismoServiciosByTipo, new String[]{"tipo"}, new Object[]{2L});
                    transportesAereo = (List)servicesACL.findAll(QuerysFinanciero.getRenTurismoServiciosByTipo, new String[]{"tipo"}, new Object[]{3L});
                    transportesMaritimo = (List)servicesACL.findAll(QuerysFinanciero.getRenTurismoServiciosByTipo, new String[]{"tipo"}, new Object[]{4L});
                    serviciosAlimenticios = (List)servicesACL.findAll(QuerysFinanciero.getRenTurismoServiciosByTipo, new String[]{"tipo"}, new Object[]{5L});
                    habitaciones = (List)servicesACL.findAll(QuerysFinanciero.getRenTurismoServiciosByTipo, new String[]{"tipo"}, new Object[]{6L});
                    
                    detallesList = new ArrayList();
                    reporte = new PdfReporte();
                    mostrarGuardarDatos = true;
                    
                    turismo.setPropietario(permiso.getLocalComercial().getPropietario());
                    turismo.setPropietario(permiso.getLocalComercial().getRazonSocial());
                    
                    for(int i=0; i<habitaciones.size(); i++){
                        detallesList.add(new HabitacionTurismo(habitaciones.get(i).getId(), habitaciones.get(i).getDescripcion(), new RenTurismoDetalleHoteles()));
                    }
                }
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public void selectEnte(CatEnte ente){
        if(esPropietario)
            turismo.setPropietario(ente);
        else
            turismo.setRepesentanteLegal(ente);
    }

    public void guardarDatos() {
        try {
            String obs = ((Observaciones) entradas.get("obs")).getObservacion();
            if (obs == null) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar las Observaciones de la tarea.");
                return;
            }
            if (obs.length() == 0) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar las Observaciones de la tarea.");
                return;
            }
            HashMap<String, Object> paramsActiviti = new HashMap();
            ((Observaciones) entradas.get("obs")).setEstado(Boolean.TRUE);
            ((Observaciones) entradas.get("obs")).setFecCre(new Date());
            ((Observaciones) entradas.get("obs")).setIdTramite(ht);
            ((Observaciones) entradas.get("obs")).setUserCre(session.getName_user());
            ((Observaciones) entradas.get("obs")).setTarea(this.getTaskDataByTaskID().getName());
            if(turismo.getPropietario() == null)
                turismo.setPropietario(permiso.getLocalComercial().getPropietario());
            turismo.setFechaIngreso(new Date());
            turismo.setEstado(Boolean.TRUE);
            
            if((turismo = services.guardarTurismo(turismo, (Observaciones) entradas.get("obs"), detallesList)) != null){
                JsfUti.messageInfo(null, "Info", "Datos guardados correctamente");
                mostrarGuardarDatos = false;
            }else{
                JsfUti.messageInfo(null, "Info", "Hubo un problema al guardar los datos. Inténtelo de nuevo");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void completarTarea(){
        try{
            if(turismo.getId() == null){
                JsfUti.messageInfo(null, "Info", "Debe guardar los datos antes de imprimir");
                return;
            }
            servletSession.instanciarParametros();
                
            servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("id_turismo", turismo.getId());
            servletSession.agregarParametro("telefonos",  turismo.getPropietario().getTelefonos());
            servletSession.agregarParametro("emails", turismo.getPropietario().getEmails());
            servletSession.agregarParametro("actividades", permiso.getLocalComercial().getActividades());
            servletSession.setNombreReporte("inspeccion_turismo");
            servletSession.setNombreSubCarpeta("localesComerciales");
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//permisoFuncionamiento//"));
            servletSession.setTieneDatasource(true);

            servletSession.setReportePDF(reporte.generarPdf("/reportes/permisoFuncionamiento/inspeccion_turismo.jasper", servletSession.getParametros()));
            reportes.descargarPDFarregloBytes(servletSession.getReportePDF());
            
            HashMap paramsActiviti = new HashMap();
            
            paramsActiviti.put("carpeta", ht.getCarpetaRep());
            paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
            paramsActiviti.put("prioridad", 50);
            paramsActiviti.put("aprobado", this.getFiles().isEmpty());
            paramsActiviti.put("archivo", servletSession.getReportePDF());
            paramsActiviti.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + ht.getSolicitante().getCiRuc() + ".pdf");
            paramsActiviti.put("tipoArchivoByteArray", "application/pdf");

            this.completeTask(this.getTaskId(), paramsActiviti);    
//            this.continuar();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Map<String, Object> getEntradas() {
        return entradas;
    }

    public void setEntradas(Map<String, Object> entradas) {
        this.entradas = entradas;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public RenPermisosFuncionamientoLocalComercial getPermiso() {
        return permiso;
    }

    public void setPermiso(RenPermisosFuncionamientoLocalComercial permiso) {
        this.permiso = permiso;
    }

    public List<RenTurismoServicios> getServiciosGenerales() {
        return serviciosGenerales;
    }

    public void setServiciosGenerales(List<RenTurismoServicios> serviciosGenerales) {
        this.serviciosGenerales = serviciosGenerales;
    }

    public List<RenTurismoServicios> getTransportesTerrestre() {
        return transportesTerrestre;
    }

    public void setTransportesTerrestre(List<RenTurismoServicios> transportesTerrestre) {
        this.transportesTerrestre = transportesTerrestre;
    }

    public List<RenTurismoServicios> getTransportesAereo() {
        return transportesAereo;
    }

    public void setTransportesAereo(List<RenTurismoServicios> transportesAereo) {
        this.transportesAereo = transportesAereo;
    }

    public List<RenTurismoServicios> getTransportesMaritimo() {
        return transportesMaritimo;
    }

    public void setTransportesMaritimo(List<RenTurismoServicios> transportesMaritimo) {
        this.transportesMaritimo = transportesMaritimo;
    }

    public RenTurismoServicios getServicioGeneral() {
        return servicioGeneral;
    }

    public void setServicioGeneral(RenTurismoServicios servicioGeneral) {
        this.servicioGeneral = servicioGeneral;
    }

    public RenTurismoServicios getTransporteTerrestre() {
        return transporteTerrestre;
    }

    public void setTransporteTerrestre(RenTurismoServicios transporteTerrestre) {
        this.transporteTerrestre = transporteTerrestre;
    }

    public RenTurismoServicios getTransporteAereo() {
        return transporteAereo;
    }

    public void setTransporteAereo(RenTurismoServicios transporteAereo) {
        this.transporteAereo = transporteAereo;
    }

    public RenTurismoServicios getTransporteMaritimo() {
        return transporteMaritimo;
    }

    public void setTransporteMaritimo(RenTurismoServicios transporteMaritimo) {
        this.transporteMaritimo = transporteMaritimo;
    }

    public RenTurismoServicios getServicioAlimenticio() {
        return servicioAlimenticio;
    }

    public void setServicioAlimenticio(RenTurismoServicios servicioAlimenticio) {
        this.servicioAlimenticio = servicioAlimenticio;
    }

    public RenTurismoServicios getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(RenTurismoServicios habitacion) {
        this.habitacion = habitacion;
    }

    public List<RenTurismoServicios> getServiciosAlimenticios() {
        return serviciosAlimenticios;
    }

    public void setServiciosAlimenticios(List<RenTurismoServicios> serviciosAlimenticios) {
        this.serviciosAlimenticios = serviciosAlimenticios;
    }

    public List<RenTurismoServicios> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<RenTurismoServicios> habitaciones) {
        this.habitaciones = habitaciones;
    }

    public CatEnteLazy getEntesList() {
        return entesList;
    }

    public void setEntesList(CatEnteLazy entesList) {
        this.entesList = entesList;
    }

    public Boolean getEsPropietario() {
        return esPropietario;
    }

    public void setEsPropietario(Boolean esPropietario) {
        this.esPropietario = esPropietario;
    }
    
    public RenTurismo getTurismo() {
        return turismo;
    }

    public void setTurismo(RenTurismo turismo) {
        this.turismo = turismo;
    }

    public List<HabitacionTurismo> getDetallesList() {
        return detallesList;
    }

    public void setDetallesList(List<HabitacionTurismo> detallesList) {
        this.detallesList = detallesList;
    }

    public Boolean getMostrarGuardarDatos() {
        return mostrarGuardarDatos;
    }

    public void setMostrarGuardarDatos(Boolean mostrarGuardarDatos) {
        this.mostrarGuardarDatos = mostrarGuardarDatos;
    }

}
