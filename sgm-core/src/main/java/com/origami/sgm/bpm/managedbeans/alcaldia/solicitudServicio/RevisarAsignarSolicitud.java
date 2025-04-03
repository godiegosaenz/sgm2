/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.alcaldia.solicitudServicio;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.DepartamentoSolicitudServicio;
import com.origami.sgm.bpm.models.ModelUsuarios;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.SvSolicitudDepartamento;
import com.origami.sgm.entities.SvSolicitudServicios;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class RevisarAsignarSolicitud extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(RevisarAsignarSolicitud.class.getName());

    @javax.inject.Inject
    private SolicitudServicosServices service;
    @javax.inject.Inject
    private CatastroServices catastroServices;

    @Inject
    private ServletSession ss;

    protected HistoricoTramites ht;
    protected MsgFormatoNotificacion msg;
    protected SvSolicitudServicios solicitud = new SvSolicitudServicios();
    protected GeDepartamento departamento;
    protected List<GeDepartamento> departamentos;
    protected AclUser director;
    protected List<AclUser> directores;
    protected List<ModelUsuarios> directoresAsignados;
    protected String observ;
    protected String tipoArchivos = SisVars.formatoArchivos;
    protected Boolean audiencia;
    protected Boolean tempInterna = false;
    protected Boolean aprobado = false;
    protected Boolean archivar = false;

    protected List<DepartamentoSolicitudServicio> departamentosModel = new ArrayList<>();
    protected DepartamentoSolicitudServicio dss;
    
    protected List<CatPredio> catPredioList;
    
    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            ht = new HistoricoTramites();
            setTaskId(session.getTaskID());
            ht = service.getPropiedadHorizontalServices().getPermiso().getHistoricoTramiteById(new Long(this.getVariable(session.getTaskID(), "tramite").toString()));
            msg = service.getNormasConstruccion().getMsgFormatoNotificacionByTipo(2L);
            solicitud = service.getSolicitudServicioByTramite(ht.getIdTramite());
            catPredioList = this.initListPredios(ht);
            departamentos = service.getDirecciones();
            if (solicitud != null) {
                this.setBpmTramite(ht);
                tempInterna = solicitud.getSolicitudInterna();
                if (solicitud.getAsignados() != null) {
                    if (solicitud.getAsignados().compareTo("A") == 0) {
                        //usuariosAsig = new ArrayList<>();
                        directoresAsignados = new ArrayList<>();
                    }
                }
                audiencia = solicitud.getTipoServicio().getId().compareTo(783L) == 0;
            }
        } else {
            this.continuar();
        }
    }

    public void aprobarRechazar() {
        try {
            HashMap<String, Object> paramt = new HashMap<>();
            if (observ == null) {
                JsfUti.messageError(null, "", Messages.observaciones);
                return;
            }
            if (!aprobado) {
                String mensaje = "Saludos,<br/><br/>"
                        + "Su solicitud fue rechazada.<br/>"
                        + "Motivo(s): <br/>"
                        + observ
                        + "<br/><br/>Gracias ";
                paramt.put("to", ht.getCorreos());
                paramt.put("subject", "Solicitud no procede");
                paramt.put("message", mensaje);
                ht.setEstado("Finalizado");
                ht.setObservacion(observ);
                solicitud.setInforme(observ);
                paramt.put("aprobar", aprobado);

            } else {
                tempInterna = true;
            }
            solicitud.setAsignados("R");
            if (service.actualizarSolicitudServcioyObservaciones(solicitud, ht, session.getName_user(), observ, "Revisar Solicitud") != null) {
                if (!aprobado) {
                    this.completeTask(session.getTaskID(), paramt);
                    this.continuar();
                }
                observ = null;
                JsfUti.update("frmRavAsgSol");
                JsfUti.update("frmRavAsgSol:tvDatos");
                JsfUti.update("frmRavAsgSol:pnlPublica");
                JsfUti.update("frmRavAsgSol:pnlSubirSumillado");
            } else {
                JsfUti.messageError(null, "", Messages.error);
            }
            JsfUti.executeJS("PF('obs').hide()");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, Messages.errorTransaccion, e);
        }
    }

    public void agregarUsuario() {
        if (departamento == null) {
            JsfUti.messageError(null, "", "Debe seleccionar un Departamento.");
            return;
        }
        if (director == null) {
            JsfUti.messageError(null, "", "Debe seleccionar un Usuario.");
            return;
        }
        if (director.getEnte() == null) {
            JsfUti.messageError(null, "", "Usuario no ha registrado datos personales.");
            return;
        } else if (director.getEnte().getEnteCorreoCollection() == null || director.getEnte().getEnteCorreoCollection().isEmpty()) {
            JsfUti.messageError(null, "", "Usuario no tiene correo.");
            return;
        } else {
            for (ModelUsuarios directorAsignado : directoresAsignados) {
                if (directorAsignado.getUsuario().equals(director)) {
                    JsfUti.messageError(null, "", "Usuario ya fue asignado.");
                    return;
                }
            }
        }
        ModelUsuarios u = new ModelUsuarios();
        u.setDepartamento(departamento);
        u.setUsuario(director);
        directoresAsignados.add(u);

    }

    public void eliminarUsuario(ModelUsuarios usuario) {
        directoresAsignados.remove(usuario);
    }

    public void mostrarObsRev() {
        if (this.getFiles().isEmpty()) {
            JsfUti.messageError(null, "", Messages.documentos);
            return;
        }

        JsfUti.update("frmObsCor");
        JsfUti.executeJS("PF('obs').show()");
    }

    public void generarSolicitud() {
        // solicitudServicio.jrxml
        ss.instanciarParametros();
        Calendar cl = Calendar.getInstance();
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//");

        ss.setNombreReporte("solicitudServicio");
        ss.setNombreSubCarpeta("solicitudServicio");
        ss.setTieneDatasource(true);
        ss.agregarParametro("SOLICITUD", solicitud.getId());
        ss.agregarParametro("NOMBRE", ht.getNombrePropietario());
        ss.agregarParametro("TELEFONO", Utils.isEmpty(ht.getTelefonos()));
        ss.agregarParametro("CORREO", ht.getCorreos());
        ss.agregarParametro("DIRECCION", ht.getSolicitante().getDireccion());

        ss.agregarParametro("LOGO", path + SisVars.logoReportes);
        if (solicitud.getTipoServicio().getId().compareTo(783L) != 0) {
            if (solicitud.getParroquia() != null) {
                ss.agregarParametro("PARROQUIA", solicitud.getParroquia().getDescripcion());
            }
            if (solicitud.getCdla() != null) {
                ss.agregarParametro("CDLA", solicitud.getCdla().getNombre());
            }
        }

        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void mostrarObsAsig() {
        if (directoresAsignados.isEmpty()) {
            JsfUti.messageError(null, "", "Debe seleccionar por lo menos un Departamento.");
            return;
        }
        JsfUti.update("frmObsCor");
        JsfUti.executeJS("PF('obs').show()");
    }

    /**
     * Permite completar la tarea de asignacion a los diferentes departamentos
     */
    public void completar() {
        if (observ == null) {
            JsfUti.messageError(null, "", Messages.observaciones);
            return;
        }
        HashMap<String, Object> paramt = new HashMap<>();

        try {
            // Completar Tarea
            MsgFormatoNotificacion ms;
            ms = service.getPropiedadHorizontalServices().getMsgFormatoNotificacionByTipo(2L);
            List<SvSolicitudDepartamento> departamentosSolicitud = new ArrayList<>();

            if (archivar) {
                ht.setObservacion(observ);
                ht.setEstado("Finalizado");
                solicitud.setInforme(observ);
            } else {
                String avisoDep = "Esta solicitud debe ser atenida por los siguientes departamentos: ";

                for (ModelUsuarios d : directoresAsignados) {
                    SvSolicitudDepartamento dep = new SvSolicitudDepartamento();
                    avisoDep = avisoDep.concat(d.getDepartamento().getNombre().concat(": ").concat(d.getUsuario().getEnte().getApellidos() + " " + d.getUsuario().getEnte().getNombres())).concat(". ");
                    dep.setDepartamento(d.getDepartamento());
                    dep.setDirector(d.getUsuario());
                    departamentosSolicitud.add(dep);
                }
                paramt.put("to", null);
                String mensaje = ms.getHeader() + "Notificacion Solicitud Servicios Online.<br/><br/>"
                        + "Tramite : " + ht.getId()
                        + " Se le asigno una tarea en el sistema, para realizarla dirigirse a la Bandeja de Tareas.<br/><br/> "
                        + avisoDep + "<br/><br/>" + ms.getFooter();
                paramt.put("from", SisVars.correo);
                paramt.put("subject", "Servicio Solicitado " + ht.getId());
                paramt.put("message", mensaje);
                paramt.put("aviso", avisoDep);
                paramt.put("prioridad", 50);
                imprimirInforme();
            }
            paramt.put("archivar", archivar);

            solicitud.setSvSolicitudDepartamentoCollection(departamentosSolicitud);
            solicitud.setAsignado(true);
            solicitud = service.actualizarSolicitudServcioyObservaciones(solicitud, ht, session.getName_user(), observ, this.getTaskDataByTaskID().getName());

            if (solicitud != null) {
                departamentosSolicitud = service.getSolicitudDepartamentoByIdSol(solicitud.getId());
                for (ModelUsuarios directorAsignado : directoresAsignados) {
                    for (SvSolicitudDepartamento ge : departamentosSolicitud) {
                        if (ge.getEstado() && ge.getDepartamento().equals(directorAsignado.getDepartamento()) && ge.getDirector().equals(directorAsignado.getUsuario())) {
                            dss = new DepartamentoSolicitudServicio();
                            dss.setDirector(ge.getDirector().getUsuario());
                            dss.setIdDepartamento(ge.getDepartamento().getId());
                            dss.setIdDirector(ge.getDirector().getId());
                            dss.setIdDetalleSolicitud(ge.getId());
                            dss.setCorreoDirector(ge.getDirector().getEnte().getEnteCorreoCollection().get(0).getEmail());
                            departamentosModel.add(dss);
                        }
                    }
                }
                paramt.put("departamento", dss);
                paramt.put("departamentos", departamentosModel);

            }
            this.completeTask(session.getTaskID(), paramt);
//            imprimirInforme();
            this.continuar();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, Messages.errorTransaccion, e);
        }
    }

    /**
     * Imprime el informe de asignacion a los diferentes departamentos
     */
    public void imprimirInforme() {
        //DATOS PARA EL REPORTE
        ss.instanciarParametros();
        ss.setTieneDatasource(true);
        ss.setNombreReporte("asignacionDepartamento_1");
        ss.setNombreSubCarpeta("solicitudServicio");
        // INGRESO DE VARIABLES DEL REPORTE
        ss.agregarParametro("FECHA", new Date());
        ss.agregarParametro("SOLICITUD", solicitud.getId());
        ss.agregarParametro("TRAMITE", ht.getId());
        ss.agregarParametro("OBSERVACION", observ);
        ss.agregarParametro("DEPARTAMENTOS", departamentosModel);
        ss.agregarParametro("LOGO", Faces.getRealPath("/").concat(SisVars.logoReportes));
        ss.agregarParametro("LOGO_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
        // MOSTRAR EL REPORTE
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void mostrarObs() {
        if (this.getFiles().isEmpty()) {
            JsfUti.messageError(null, "", "Debe adjunta la solicitud.");
            return;
        }
        JsfUti.update("frmObsCor");
        JsfUti.executeJS("PF('obs').show()");
    }

    public void completarRevisar() {
        if (observ == null) {
            JsfUti.messageError(null, "", Messages.observaciones);
            return;
        }
        if (this.getFiles().isEmpty()) {
            JsfUti.messageError(null, "", "Debe adjunta la solicitud.");
            return;
        }
        HashMap<String, Object> paramt = new HashMap<>();
        try {
            solicitud.setAsignados("A");
            if (service.actualizarSolicitudServcioyObservaciones(solicitud, ht, session.getName_user(), observ, "Cargar Solicitud Sumillada") != null) {
                String mensaje = msg.getHeader() + "<br/>"
                        + "Saludos,<br/><br/>"
                        + "Su solicitud ha sido receptada, posteriormente será asignada al departamento que corresponda. "
                        + "<br/><br/>Gracias <br/><br/>" + msg.getFooter();
                paramt.put("aprobar", true);
                paramt.put("from", SisVars.correo);
                paramt.put("to", ht.getCorreos());
                paramt.put("subject", "Servicio Solicitado " + ht.getId());
                paramt.put("message", mensaje);
                paramt.put("carpeta", ht.getCarpetaRep());
                paramt.put("listaArchivos", this.getFiles());
                paramt.put("listaArchivosFinal", new ArrayList<>());
                paramt.put("prioridad", 50);
                this.completeTask(session.getTaskID(), paramt);
            }
            this.continuar();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, Messages.errorTransaccion, e);
        }
    }

    public void cargarUsuariosDepartamento() {
        try {
            directores = new ArrayList<>();
            if (departamento != null) {
                directores.addAll(catastroServices.getUser(departamento));
//                for (AclRol a : departamento.getAclRolCollection()) {
//                    if (a.getIsDirector() && a.getEstado()) {
//                        directores.addAll(a.getAclUserCollection());
//                    }
//                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, Messages.errorTransaccion, e);
        }
    }

    public void observacionDefault() {
        if (observ == null || observ.equals("")) {
            observ = "TRAMITE: " + ht.getId();
        }
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public SvSolicitudServicios getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SvSolicitudServicios solicitud) {
        this.solicitud = solicitud;
    }

    public List<GeDepartamento> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<GeDepartamento> departamentos) {
        this.departamentos = departamentos;
    }

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public Boolean getAudiencia() {
        return audiencia;
    }

    public void setAudiencia(Boolean audiencia) {
        this.audiencia = audiencia;
    }

    public RevisarAsignarSolicitud() {
    }

    public Boolean getAprobado() {
        return aprobado;
    }

    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
    }

    public String getTipoArchivos() {
        return tipoArchivos;
    }

    public void setTipoArchivos(String tipoArchivos) {
        this.tipoArchivos = tipoArchivos;
    }

    public Boolean getTempInterna() {
        return tempInterna;
    }

    public void setTempInterna(Boolean tempInterna) {
        this.tempInterna = tempInterna;
    }

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Boolean getArchivar() {
        return archivar;
    }

    public void setArchivar(Boolean archivar) {
        this.archivar = archivar;
    }

    public AclUser getDirector() {
        return director;
    }

    public void setDirector(AclUser director) {
        this.director = director;
    }

    public List<AclUser> getDirectores() {
        return directores;
    }

    public void setDirectores(List<AclUser> directores) {
        this.directores = directores;
    }

    public List<ModelUsuarios> getDirectoresAsignados() {
        return directoresAsignados;
    }

    public void setDirectoresAsignados(List<ModelUsuarios> directoresAsignados) {
        this.directoresAsignados = directoresAsignados;
    }

    public List<CatPredio> getCatPredioList() {
        return catPredioList;
    }

    public void setCatPredioList(List<CatPredio> catPredioList) {
        this.catPredioList = catPredioList;
    }
    
    

}
