/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.TareaWF;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
@SuppressWarnings("serial")
public class ReportesEdificaciones extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(ReportesEdificaciones.class.getName());
    private static final long serialVersionUID = 1L;

    @javax.inject.Inject
    private SolicitudServicosServices service;
    @javax.inject.Inject
    private Entitymanager manager;

    protected Integer id;
    protected Integer semanal;
    protected String title;
    protected Integer anioDesde;
    protected String memorandum;
    protected String memorandum2 = " LPC-DPU-";
    protected String dirigioA;
    protected String cargo;
    protected String permisoDesde;
    protected String permisoHasta;
    protected Date fechaInicio;
    protected Date fechaFin;
    protected List<GeDepartamento> departamentos;
    protected Long departamento;
    protected GeDepartamento depEdificaciones;
    protected List<AclUser> tecnicos;
    protected List<TareaWF> tareasTecnicos;
    protected List<Long> tramites;
    protected Boolean todosTramites = Boolean.TRUE;
    protected String estadoDetalle;
    protected Date desdeDetalle= new Date();
    protected Date hastaDetalle= new Date();

    @Inject
    private ServletSession ss;
    @Inject
    private UserSession sess;

    protected Long tipoReporte = 1L;
    protected Long tipoTramiteReporte;
    protected AclUser tecnicoEdif;

    @PostConstruct
    public void initView() {
        fechaFin = new Date();
        semanal = 1;
        departamento = 1L;
        depEdificaciones = (GeDepartamento) manager.find(GeDepartamento.class, 1L);
    }

    public void actualizarSemanal() {

        try {
            switch (id) {
                case 1:
                    Calendar cl = Calendar.getInstance();
                    anioDesde = (cl.get(Calendar.YEAR));
                    memorandum2 = memorandum2 + "" + anioDesde;
                    title = "Reportes Semanal";
                    JsfUti.update("frmEdificaciones:pnlSemanal");
                    break;
                case 2:
                    departamentos = service.getDirecciones();
                    title = "Seleccione las fechas para el reporte de Asignación";
                    departamento = 1L;
                    JsfUti.update("frmEdificaciones:pnlAsig");
                    break;
                case 3:
                    title = "Seleccione las fechas para el reporte de Valija";
                    JsfUti.update("frmEdificaciones:pnlVal");
                    break;
                case 4:
                    title = "Tareas Por Tecnicos";
                    break;
                case 5:
                    title = "Detalle Tramites Edificaciones";
                    break;
            }
            tecnicos = new ArrayList<>();
            /*depEdificaciones.getAclRolCollection().stream().forEach((rol)->{
             rol.getAclUserCollection().stream().filter((user)->(!tecnicos.contains(user) && user.getSisEnabled())).forEach((user)->{
             tecnicos.add(user);
             });
             });*/
            for (AclRol rol : depEdificaciones.getAclRolCollection()) {
                for (AclUser ac : rol.getAclUserCollection()) {
                    if (!tecnicos.contains(ac)) {
                        tecnicos.add(ac);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR", e);
        }
        JsfUti.update("frmEdificaciones");
    }

    public void generarSemanal(int tipo) {
        ss.instanciarParametros();
        ss.setTieneDatasource(Boolean.TRUE);
        Calendar fechIn = Calendar.getInstance();
        int anioIn = 0;
        String mesInicio = null;
        String diaInicio = null;
        int anioFi = 0;
        String mesFin = null;
        String day = null;
        if (tipo != 3) {
            //fecha de Inicio
            fechIn.setTime(fechaInicio);
            anioIn = fechIn.get(Calendar.YEAR);
            mesInicio = getMonth(fechIn);
            diaInicio = getDayMonth(fechIn);
            // Fecha final
            fechIn.setTime(fechaFin);
            anioFi = fechIn.get(Calendar.YEAR);
            mesFin = getMonth(fechIn);
            day = getDayMonth(fechIn);
        }

        try {
            switch (tipo) {
                case 1: // Asignación
                    ss.setNombreSubCarpeta("reportesEdificaciones");
                    ss.setNombreReporte("tramites_ingresados");
//                    System.out.println("Dep " + departamento);
                    ss.agregarParametro("FECHA_INICIO", (anioIn + "-" + mesInicio + "-" + diaInicio));
                    System.out.println((anioFi + "-" + mesFin + "-" + diaInicio));
                    ss.agregarParametro("FECHA_FIN", (anioFi + "-" + mesFin + "-" + day));
                    ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                    ss.agregarParametro("DEPARTAMENTO", 1L);
                    ss.agregarParametro("ESTADO", "Pendiente");

                    break;
                case 2: // Valija
                    ss.setNombreSubCarpeta("reportesEdificaciones");
                    ss.setNombreReporte("valija");
                    ss.agregarParametro("FECHA_INICIO", (anioIn + "-" + mesInicio + "-" + diaInicio));
                    ss.agregarParametro("FECHA_FIN", (anioFi + "-" + mesFin + "-" + day));
                    ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                    ss.agregarParametro("ESTADO", "Pendiente");

                    break;
                case 3: // Semanal
                    switch (semanal) {
                        case 1: // Permiso de Construcción
                            ss.setNombreReporte("listado_permiso_construccion");
                            ss.setNombreSubCarpeta("permisoConstruccion");
                            ss.setTieneDatasource(Boolean.TRUE);
                            ss.agregarParametro("MEMO", memorandum + " " + memorandum2);
                            ss.agregarParametro("DE", new Long(permisoDesde));
                            ss.agregarParametro("HASTA", new Long(permisoHasta));
                            ss.agregarParametro("DESDE_ANIO", new Long(anioDesde));
                            ss.agregarParametro("DIRIGIDO_A", dirigioA);
                            ss.agregarParametro("CARGO", cargo);
                            break;
                        case 2: // Inspección Final
                            ss.setNombreReporte("reporteInspeccion");
                            ss.setNombreSubCarpeta("inspeccionFinal");
                            ss.agregarParametro("MEMORANDUM", memorandum + " " + memorandum2);
                            ss.agregarParametro("DE", new Long(permisoDesde));
                            ss.agregarParametro("HASTA", new Long(permisoHasta));
                            ss.agregarParametro("DESDE_ANIO", new Long(anioDesde));
                            ss.agregarParametro("DIRIGIDO_A", dirigioA);
                            ss.agregarParametro("CARGO", cargo);
                            break;
                    }
                    break;
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, "ERROR REPORTES EDIFICACIONES", e);
        }
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void reporteTareasPorTecnicosEdificaciones() {
        try {
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.FALSE);
            ss.setNombreSubCarpeta("reportesEdificaciones");
            if (this.tipoReporte == 1L) {
                ss.setNombreReporte("tareasTecnicosGrupal");
            } else if (this.tipoReporte == 2L) {
                ss.setNombreReporte("tareasTecnicosDetalle");
            }
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.sisLogo1));
            tareasTecnicos = new ArrayList<>();
            /*tecnicos.stream().forEach((tecnico) -> {
             tareasTecnicos.addAll(this.getListaTareasPersonales(tecnico.getUsuario()));
             });*/
            if (this.tecnicoEdif == null) {
                for (AclUser tecnico : tecnicos) {
                    tareasTecnicos.addAll(this.getListaTareasPersonales(tecnico.getUsuario(), null));
                }
            } else {
                tareasTecnicos.addAll(this.getListaTareasPersonales(this.tecnicoEdif.getUsuario(), null));
            }
            ss.setDataSource(tareasTecnicos);
            List<TareaWF> tareasTecnicosFilter;
            if (this.tipoTramiteReporte != null) {
                tareasTecnicosFilter = new ArrayList<>();
                for (TareaWF tareasTecnico : tareasTecnicos) {
                    if (tareasTecnico.getTramite().getCorreccion().equals(this.tipoTramiteReporte)) {
                        tareasTecnicosFilter.add(tareasTecnico);
                    }
                }
                ss.setDataSource(tareasTecnicosFilter);
            }
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, "ERROR REPORTES EDIFICACIONES", e);
        }
    }

    public void reporteDetalleEdificaciones() {

        try {

            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("reportesEdificaciones");
            ss.setNombreReporte("detalleTramites");
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.sisLogo1));
            ss.agregarParametro("TRAMITES", (Collection) tramites);
            ss.agregarParametro("DESDE", desdeDetalle);
            ss.agregarParametro("HASTA", hastaDetalle);
            ss.agregarParametro("ESTADO", estadoDetalle);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR REPORTES EDIFICACIONES - DETALLE ", e);
        }
    }

    public Integer getSemanal() {
        return semanal;
    }

    public void setSemanal(Integer semanal) {
        this.semanal = semanal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getAnioDesde() {
        return anioDesde;
    }

    public void setAnioDesde(Integer anioDesde) {
        this.anioDesde = anioDesde;
    }

    public String getMemorandum() {
        return memorandum;
    }

    public void setMemorandum(String memorandum) {
        this.memorandum = memorandum;
    }

    public String getMemorandum2() {
        return memorandum2;
    }

    public void setMemorandum2(String memorandum2) {
        this.memorandum2 = memorandum2;
    }

    public String getDirigioA() {
        return dirigioA;
    }

    public void setDirigioA(String dirigioA) {
        this.dirigioA = dirigioA;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getPermisoDesde() {
        return permisoDesde;
    }

    public void setPermisoDesde(String permisoDesde) {
        this.permisoDesde = permisoDesde;
    }

    public String getPermisoHasta() {
        return permisoHasta;
    }

    public void setPermisoHasta(String permisoHasta) {
        this.permisoHasta = permisoHasta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public ReportesEdificaciones() {
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public List<GeDepartamento> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<GeDepartamento> departamentos) {
        this.departamentos = departamentos;
    }

    public Long getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Long departamento) {
        this.departamento = departamento;
    }

    public Long getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(Long tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public AclUser getTecnicoEdif() {
        return tecnicoEdif;
    }

    public void setTecnicoEdif(AclUser tecnicoEdif) {
        this.tecnicoEdif = tecnicoEdif;
    }

    public List<AclUser> getTecnicos() {
        return tecnicos;
    }

    public void setTecnicos(List<AclUser> tecnicos) {
        this.tecnicos = tecnicos;
    }

    public Long getTipoTramiteReporte() {
        return tipoTramiteReporte;
    }

    public void setTipoTramiteReporte(Long tipoTramiteReporte) {
        this.tipoTramiteReporte = tipoTramiteReporte;
    }

    public GeDepartamento getDepEdificaciones() {
        return depEdificaciones;
    }

    public void setDepEdificaciones(GeDepartamento depEdificaciones) {
        this.depEdificaciones = depEdificaciones;
    }

    public Boolean getTodosTramites() {
        return todosTramites;
    }

    public void setTodosTramites(Boolean todosTramites) {
        this.todosTramites = todosTramites;
    }

    public String getEstadoDetalle() {
        return estadoDetalle;
    }

    public void setEstadoDetalle(String estadoDetalle) {
        this.estadoDetalle = estadoDetalle;
    }

    public Date getDesdeDetalle() {
        return desdeDetalle;
    }

    public void setDesdeDetalle(Date desdeDetalle) {
        this.desdeDetalle = desdeDetalle;
    }

    public Date getHastaDetalle() {
        return hastaDetalle;
    }

    public void setHastaDetalle(Date hastaDetalle) {
        this.hastaDetalle = hastaDetalle;
    }

    public List<Long> getTramites() {
        return tramites;
    }

    public void setTramites(List<Long> tramites) {
        this.tramites = tramites;
    }

    private String getDayMonth(Calendar fechIn) {
        Integer dayFi = fechIn.get(Calendar.DAY_OF_MONTH);
        if (dayFi <= 9) {
            return "0" + dayFi;
        } else {
            return dayFi.toString();
        }
    }

    private String getMonth(Calendar fechIn) {
        Integer mesIn = fechIn.get(Calendar.MONTH) + 1;
        if (mesIn <= 9) {
            return "0" + mesIn;
        } else {
            return mesIn.toString();
        }
    }

}
