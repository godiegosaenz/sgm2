/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.alcaldia.solicitudServicio;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.TareaWF;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class ReportesSolicitudServicio extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(ReportesSolicitudServicio.class.getName());

    @javax.inject.Inject
    private SolicitudServicosServices service;
    @Inject
    private ServletSession ss;

    protected Date fechaInicio;
    protected Date fechaFin;
    protected Date fechaInicio10;
    protected Date fechaFin10;
    protected Date fechaInicioDep;
    protected Date fechaFinDep;
    protected Date fechaInicioResumen;
    protected Date fechaFinResumen;
    protected Long tipo = 2L;
    protected String estado = "pendiente";
    protected Long id = 1L;
    protected String path;
    protected List<TareaWF> tareasAsignadasAlcaldia = new ArrayList<>();
    protected AclUser asistente;

    @PostConstruct
    public void initView() {
        path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//");
        fechaFinResumen = new Date();
        fechaFin = new Date();
        fechaFinDep = new Date();
        fechaFin10 = new Date();
    }

    public void tareasAlcaldia() {
        asistente = service.getPropiedadHorizontalServices().getPermiso().getAclUserById(197L);
        tareasAsignadasAlcaldia = this.getListaTareasPersonales(asistente.getUsuario(),null);
    }

    public void reporteResumen() {
        if (fechaInicioResumen != null && fechaFinResumen != null) {
            if (fechaInicioResumen.compareTo(fechaFinResumen) != 1) {

                ss.instanciarParametros();

                ss.setTieneDatasource(Boolean.TRUE);
                ss.setNombreSubCarpeta("solicitudServicio");
                ss.agregarParametro("SUBREPORT_DIR", path + "/reportes/solicitudServicio/");
                ss.agregarParametro("inicio", fechaInicioResumen);
                ss.agregarParametro("fin", fechaFinResumen);

                switch (tipo.intValue()) {
                    case 1:
                        ss.setNombreReporte("resumenSolicitudServiciosTotal");
                        ss.agregarParametro("estado", estado);
                        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");
                        break;
                    case 2:
                        ss.setNombreReporte("resumenSolServ");
                        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
                        break;
                }
            } else {
                JsfUti.messageError(null, "La Fecha Fin no puede ser menor a la Fecha Inicio.", "");
            }
        } else {
            JsfUti.messageError(null, "La Fecha Fin no puede ser menor a la Fecha Inicio.", "");
        }

    }

    public void reportes(int opcion) {
        Boolean fechaNull = null;
        Boolean fechaIguales = null;

        ss.instanciarParametros();
        ss.setTieneDatasource(Boolean.TRUE);
        ss.setNombreSubCarpeta("solicitudServicio");
        ss.agregarParametro("LOGO", path + SisVars.logoReportes);

        switch (opcion) {
            case 1: // Sin Audiencia 10
                ss.agregarParametro("INICIO", fechaInicio);
                ss.agregarParametro("FIN", fechaFin);
                ss.setNombreReporte("reporteSolicitudServicio");
                fechaNull = fechaInicio != null && fechaFin != null;
                fechaIguales = fechaInicio.compareTo(fechaFin) != 1;
                break;
            case 2: // Audiencia 10
                ss.agregarParametro("INICIO", fechaInicio10);
                ss.agregarParametro("FIN", fechaFin10);
                ss.setNombreReporte("reporteSolicitudServicioAudiencia10");
                fechaNull = fechaInicio10 != null && fechaFin10 != null;
                fechaIguales = fechaInicio10.compareTo(fechaFin10) != 1;
                break;
            case 3: // Departamento
                ss.agregarParametro("INICIO", fechaInicioDep);
                ss.agregarParametro("FIN", fechaFinDep);
                ss.setNombreReporte("reporteSolicitudServicioDepartamento");
                fechaNull = fechaInicioDep != null && fechaFinDep != null;
                fechaIguales = fechaInicioDep.compareTo(fechaFinDep) != 1;
                break;
        }
        if (fechaNull) {
            if (fechaIguales) {
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            } else {
                JsfUti.messageError(null, "La Fecha Fin no puede ser menor a la Fecha Inicio.", "");
                ss.borrarDatos();
            }
        } else {
            JsfUti.messageError(null, "La Fecha Fin no puede ser menor a la Fecha Inicio.", "");
            ss.borrarDatos();
        }
    }

    public void resumenDepartamento(int print) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ss.instanciarParametros();
        ss.setTieneDatasource(Boolean.TRUE);
        ss.setNombreSubCarpeta("solicitudServicio");
        ss.agregarParametro("estado", estado);
        ss.agregarParametro("logo", path + SisVars.logoReportes);
        ss.agregarParametro("inicio", sdf.format(fechaInicioResumen));
        ss.agregarParametro("fin", sdf.format(fechaFinResumen));
        switch (tipo.intValue()) {
            case 1:
                ss.setNombreReporte("generalDepartamento");
                break;
            case 2:
                ss.setNombreReporte("generalVariosDepartamentos");
                break;
        }

        switch (print) {
            case 1:
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
                break;
            case 2:
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");
                break;
        }
    }

    public SolicitudServicosServices getService() {
        return service;
    }

    public void setService(SolicitudServicosServices service) {
        this.service = service;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Date getFechaInicio10() {
        return fechaInicio10;
    }

    public void setFechaInicio10(Date fechaInicio10) {
        this.fechaInicio10 = fechaInicio10;
    }

    public Date getFechaFin10() {
        return fechaFin10;
    }

    public void setFechaFin10(Date fechaFin10) {
        this.fechaFin10 = fechaFin10;
    }

    public Date getFechaInicioDep() {
        return fechaInicioDep;
    }

    public void setFechaInicioDep(Date fechaInicioDep) {
        this.fechaInicioDep = fechaInicioDep;
    }

    public Date getFechaFinDep() {
        return fechaFinDep;
    }

    public void setFechaFinDep(Date fechaFinDep) {
        this.fechaFinDep = fechaFinDep;
    }

    public Date getFechaInicioResumen() {
        return fechaInicioResumen;
    }

    public void setFechaInicioResumen(Date fechaInicioResumen) {
        this.fechaInicioResumen = fechaInicioResumen;
    }

    public Date getFechaFinResumen() {
        return fechaFinResumen;
    }

    public void setFechaFinResumen(Date fechaFinResumen) {
        this.fechaFinResumen = fechaFinResumen;
    }

    public Long getTipo() {
        return tipo;
    }

    public void setTipo(Long tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportesSolicitudServicio() {
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public List<TareaWF> getTareasAsignadasAlcaldia() {
        return tareasAsignadasAlcaldia;
    }

    public void setTareasAsignadasAlcaldia(List<TareaWF> tareasAsignadasAlcaldia) {
        this.tareasAsignadasAlcaldia = tareasAsignadasAlcaldia;
    }

    public AclUser getAsistente() {
        return asistente;
    }

    public void setAsistente(AclUser asistente) {
        this.asistente = asistente;
    }

}
