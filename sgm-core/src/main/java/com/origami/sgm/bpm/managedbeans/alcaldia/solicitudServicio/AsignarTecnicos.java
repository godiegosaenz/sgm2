/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.alcaldia.solicitudServicio;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.DepartamentoSolicitudServicio;
import com.origami.sgm.bpm.models.ModelUsuarios;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.SvSolicitudDepartamento;
import com.origami.sgm.entities.SvSolicitudServicios;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
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
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author supergold
 */
@Named
@ViewScoped
public class AsignarTecnicos extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(AsignarTecnicos.class.getName());

    @javax.inject.Inject
    private SolicitudServicosServices service;

    protected HistoricoTramites ht;
    protected MsgFormatoNotificacion msg;
    protected SvSolicitudServicios solicitud = new SvSolicitudServicios();

    protected List<AclUser> usuariosDeparta = new ArrayList<>();
    protected List<ModelUsuarios> reasignarDeparta;
    protected List<GeDepartamento> departamentos;
    protected GeDepartamento departamento;

    protected String tipoArchivos = SisVars.formatoArchivos;
    protected String observ;
    protected String titleObs;
    protected String placeholderObs;
    protected String msgAdicinal;
    protected Boolean audiencia;
    protected Boolean adjuntarInformeDoc = false;
    protected int opcion;
    // Variables para asignar...
    protected AclUser user;
    protected List<AclUser> listUsuarios;

    //ASIGNAR SECRETARIA 
    protected List<AclUser> listUsuariosSecretariaPlanificacion;
    protected AclUser userSecretaria;
    protected ModelUsuarios userAsignar;

    protected List<DepartamentoSolicitudServicio> ldss;
    protected DepartamentoSolicitudServicio dss;
    protected SvSolicitudDepartamento detalleDepartamento;
    protected SvSolicitudDepartamento depSubDireccion;

    protected List<CatPredio> catPredioList;

    /**
     * Se obtiene
     * los objetos
     * necesarios
     * para realizar
     * la tarea.
     * HistoricoTramites,
     * SvSolicitudServicios,
     * DepartamentoSolicitudServicio,
     * SvSolicitudDepartamento.
     */
    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            try {
                ht = new HistoricoTramites();
                setTaskId(session.getTaskID());

                ht = service.getPropiedadHorizontalServices().getPermiso().getHistoricoTramiteById(new Long(this.getVariable(session.getTaskID(), "tramite").toString()));
                this.setBpmTramite(ht);
                msg = service.getNormasConstruccion().getMsgFormatoNotificacionByTipo(2L);
                solicitud = service.getSolicitudServicioByTramite(ht.getIdTramite());
                departamentos = service.getDirecciones();
                if (solicitud != null) {
                    audiencia = solicitud.getTipoServicio().getId().compareTo(783L) == 0;

                    dss = (DepartamentoSolicitudServicio) this.getVariable(session.getTaskID(), "departamento");
                    ldss = (List<DepartamentoSolicitudServicio>) this.getVariable(session.getTaskID(), "departamentos");
                    detalleDepartamento = service.getSvSolicitudDepartamentoById(dss.getIdDetalleSolicitud());
                }
                catPredioList = this.initListPredios(ht);
                userAsignar = new ModelUsuarios();
                List<GeDepartamento> deps = service.getDepartamentosById(session.getDepts());

                listUsuarios = new ArrayList<>();
                for (GeDepartamento dep : deps) {
                    for (AclRol aclRolCollection : dep.getAclRolCollection()) {
                        if (aclRolCollection.getId() != 167L) {
                            for (AclUser aclUserCollection : aclRolCollection.getAclUserCollection()) {
                                if (aclUserCollection.getSisEnabled() && !listUsuarios.contains(aclUserCollection) && !session.getName_user().equals(aclUserCollection.getUsuario())) {
                                    listUsuarios.add(service.getUsuario(aclUserCollection.getId()));
                                }
                            }
                        }

                    }
                }
                listUsuariosSecretariaPlanificacion = new ArrayList<>();
                for (GeDepartamento dep : deps) {
                    for (AclRol aclRolCollection : dep.getAclRolCollection()) {
                        if (aclRolCollection.getId() == 167L) {
                            for (AclUser aclUserCollection : aclRolCollection.getAclUserCollection()) {
                                if (aclUserCollection.getSisEnabled() && !listUsuariosSecretariaPlanificacion.contains(aclUserCollection) && !session.getName_user().equals(aclUserCollection.getUsuario())) {
                                    listUsuariosSecretariaPlanificacion.add(service.getUsuario(aclUserCollection.getId()));
                                }
                            }
                        }

                    }
                }

            } catch (Exception e) {
                LOG.log(Level.SEVERE, Messages.error, e);
            }

        } else {
            this.continuar();
        }
    }

    /**
     * Valida la
     * correcta
     * seleccion de
     * un Usuario
     * cuando un
     * Director
     * delega la
     * solicitud.
     */
    public void agregarUsuario() {
        if (user == null) {
            JsfUti.messageError(null, "", "Debe seleccionar un Usuario.");
            return;
        }

        if (user.getEnte() == null) {
            JsfUti.messageError(null, "", "Usuario sin datos Personales.");
            return;
        }

        if (user.getEnte().getEnteCorreoCollection() == null || user.getEnte().getEnteCorreoCollection().isEmpty()) {
            JsfUti.messageError(null, "", "Usuario no tiene correo.");
        }
    }

    public void agregarUsuarioSecretaria() {
        if (userSecretaria == null) {
            JsfUti.messageError(null, "", "Debe seleccionar un Usuario.");
            return;
        }

        if (userSecretaria.getEnte() == null) {
            JsfUti.messageError(null, "", "Usuario sin datos Personales.");
            return;
        }

        if (userSecretaria.getEnte().getEnteCorreoCollection() == null || userSecretaria.getEnte().getEnteCorreoCollection().isEmpty()) {
            JsfUti.messageError(null, "", "Usuario no tiene correo.");
        }
    }

    public void limpiarUsuario() {
        user = null;
    }

    public void limpiarUsuarioSecretaria() {
        userSecretaria = null;
    }

    /**
     * Muestra un
     * panel con
     * datos
     * representativos
     * a la accion a
     * Realizar. 1 :
     * Resolver
     * Solicitud, 2
     * : Delegar
     * Solicitud, 3:
     * Rechazar
     * Solicitud, 4:
     * Reenviar
     * Solicitud.
     *
     * @param accion
     */
    public void mostarObs(int accion) {
        switch (accion) {
            case 1:
                titleObs = "RESOLVER SOLICITUD";
                placeholderObs = "Informe de la Solicitud. (Campo Obligatorio)";

                break;
            case 2:
                titleObs = "DELEGAR SOLICITUD";
                placeholderObs = "Nota para el delegado. (Campo Obligatorio)";
                adjuntarInformeDoc = false;

                break;
            case 3:
                titleObs = "RECHAZAR SOLICITUD";
                placeholderObs = "Ingrese las Observaciones. (Campo Obligatorio)";
                msgAdicinal = "El motivo de rechazo solo puede darse porque el departamento no es el indicado para resolver la solicitud.";
                adjuntarInformeDoc = false;
                break;
            case 4:
                titleObs = "REENVIAR SOLICITUD A DIRECCION";
                placeholderObs = "Ingrese las Observaciones. (Campo Obligatorio)";
                msgAdicinal = "Ingrese el motivo por el cual la solicitud sera Reenviada a Otro departamento.";
                adjuntarInformeDoc = false;
                reasignarDeparta = new ArrayList<>();
                break;
        }
        opcion = accion;
        JsfUti.executeJS("PF('obs').show()");
        JsfUti.update("frmObsCor");
    }

    /**
     * Se actualizan
     * los objetos
     * dependiendo
     * el tipo de
     * accion que el
     * usuario
     * selecciono
     * para
     * completar la
     * tarea. 1 :
     * Resolver
     * Solicitud, 2
     * : Delegar
     * Solicitud, 3:
     * Rechazar
     * Solicitud, 4:
     * Reenviar
     * Solicitud.
     */
    public void completarTarea() {
        HashMap<String, Object> paramt = new HashMap<>();
        String notificar = null;
        String subject = null;
        String to = null;

        dss.setAccion(new Long(opcion));
        detalleDepartamento.setAccion(new Long(opcion));
        switch (opcion) {
            case 1: // Resolver
                if (detalleDepartamento.getArchivo()) {
                    if (this.getFiles().isEmpty()) {
                        JsfUti.messageError(null, "Documentos.", "Debe Ingresar el documento del Informe");
                        return;
                    } else {
                        if (detalleDepartamento.getInforme() == null) {
                            detalleDepartamento.setInforme("El detalle de la solicitud se encuentra en el docuemnto adjunto");
                        }
                    }
                } else {
                    if (detalleDepartamento.getInforme() == null) {
                        JsfUti.messageError(null, "Informe.", "Ingresar detalle de la resolucion.");
                        return;
                    }
                }
                dss.setValidar(Boolean.FALSE);
                subject = "Solicitud de Servicio " + ht.getId();
                notificar = msg.getHeader() + "Se ha delegado una tarea.<br/><br/>" + msg.getFooter();
                observ = "S.S. ATENDIDA: " + detalleDepartamento.getInforme();
                detalleDepartamento.setFechaFinalizado(new Date());
                break;
            case 2: // Delegar
                if (user != null && detalleDepartamento.getComunicado() != null) {
                    detalleDepartamento.setResponsable(user);
                    dss.setResponsable(user.getUsuario());
                    dss.setCorreoResponsable(user.getEnte().getEnteCorreoCollection().get(0).getEmail());
                    for (AclRol aclRolCollection : user.getAclRolCollection()) {
                        if (aclRolCollection.getEsSubDirector()) {
                            dss.setAccion(4L);
                            dss.setDirector(user.getUsuario());
                            dss.setCorreoDirector(user.getEnte().getEnteCorreoCollection().get(0).getEmail());
                            depSubDireccion = new SvSolicitudDepartamento();
                            depSubDireccion.setDirector(user);
                            depSubDireccion.setDepartamento(aclRolCollection.getDepartamento());
                            depSubDireccion.setPadre(detalleDepartamento.getId());
                            break;
                        }
                        if (aclRolCollection.getIsDirector()) {
                            List<AclUser> usersj = acl.findAll(Querys.getAclUserDirectorByRol, new String[]{"idRol"}, new Object[]{aclRolCollection.getId()});
                            if (usersj != null && !usersj.isEmpty()) {
                                AclUser u = usersj.get(usersj.size() - 1);
                                dss.setAccion(4L);
                                dss.setDirector(u.getUsuario());
                                dss.setCorreoDirector(u.getEnte().getEnteCorreoCollection().get(0).getEmail());
                                depSubDireccion = new SvSolicitudDepartamento();
                                depSubDireccion.setDirector(u);
                                depSubDireccion.setDepartamento(aclRolCollection.getDepartamento());
                                depSubDireccion.setPadre(detalleDepartamento.getId());
                                break;
                            }

                        }
                    }
                } else {
                    JsfUti.messageError(null, "Delegar.", "Seleccione usuario e ingrese un comunicado para el Delegado.");
                    return;
                }
                subject = "Solicitud de Servicio " + ht.getId();
                notificar = msg.getHeader() + "Se ha delegado una tarea.<br/><br/>" + msg.getFooter();
                observ = "S.S. DELEGADA: " + detalleDepartamento.getComunicado();
                break;
            case 3: // Rechazar
                AclUser secretaria = service.getPropiedadHorizontalServices().getPermiso().getAclUserByUser(this.getVariable(session.getTaskID(), "secretariaAlcaldia").toString());
                if (!secretaria.getEnte().getEnteCorreoCollection().isEmpty()) {
                    to = secretaria.getEnte().getEnteCorreoCollection().get(0).getEmail();
                } else {
                    to = SisVars.correoClienteGenerico;
                }
                subject = "Asignacion Incorrecta";
                notificar = msg.getHeader() + "Este departamento no es el indicado para resolver esta solicitud.<br/><br/>" + msg.getFooter();
                solicitud.setAsignados("A");
                detalleDepartamento.setEstado(Boolean.FALSE);
                observ = "S.S. RECHAZADA: " + detalleDepartamento.getInforme();
                break;
            case 4:
                if (reasignarDeparta == null || reasignarDeparta.isEmpty() || detalleDepartamento.getInforme() == null) {
                    JsfUti.messageError(null, "Reenviar.", "Seleccione usuario y detalle del reenvio.");
                    return;
                }
                String avisoDepartamento = "";
                for (ModelUsuarios reasignarDeparta1 : reasignarDeparta) {
                    dss.setAccion(4L);
                    dss.setDirector(reasignarDeparta1.getUsuario().getUsuario());
                    dss.setCorreoDirector(reasignarDeparta1.getUsuario().getEnte().getEnteCorreoCollection().get(0).getEmail());
                    detalleDepartamento.setResponsable(reasignarDeparta1.getUsuario());
                    depSubDireccion = new SvSolicitudDepartamento();
                    depSubDireccion.setDirector(reasignarDeparta1.getUsuario());
                    depSubDireccion.setDepartamento(reasignarDeparta1.getDepartamento());
                    avisoDepartamento = avisoDepartamento.concat(reasignarDeparta1.getDepartamento().getNombre().concat(": ").concat(reasignarDeparta1.getUsuario().getEnte().getApellidos() + " " + reasignarDeparta1.getUsuario().getEnte().getNombres())).concat(". ");
                    break;
                }
                String mensaje = msg.getHeader() + "Notificacion Solicitud Servicios Online.<br/><br/>"
                        + "Tramite : " + ht.getId()
                        + "Se le asigno una tarea en el sistema, para realizarla dirigirse a la Bandeja de Tareas.<br/><br/> "
                        + avisoDepartamento + "<br/><br/>" + msg.getFooter();
                notificar = mensaje;
                subject = (String) this.getVariable(session.getTaskID(), "subject");
                detalleDepartamento.setEstado(Boolean.FALSE);
                detalleDepartamento.setFechaFinalizado(new Date());
                observ = "S.S. REENVIADA: " + detalleDepartamento.getInforme();
                break;
        }
        // ACTUALIZA EL 'departaemnto' DEL LISTADO 'departamentos' (USADA EN EL FLUJO)
        for (DepartamentoSolicitudServicio lds : ldss) {
            if (lds.getIdDetalleSolicitud().equals(dss.getIdDetalleSolicitud())) {
                lds.setAccion(dss.getAccion());
                lds.setResponsable(dss.getResponsable());
                lds.setCorreoResponsable(dss.getCorreoResponsable());
                break;
            }
        }
        //Actualiza el registro del detalle de la solicitud
        for (SvSolicitudDepartamento ge : solicitud.getSvSolicitudDepartamentoCollection()) {
            if (ge.getId().equals(detalleDepartamento.getId())) {
                ge.setComunicado(detalleDepartamento.getComunicado());
                ge.setInforme(detalleDepartamento.getInforme());
                ge.setAccion(detalleDepartamento.getAccion());
                ge.setResponsable(detalleDepartamento.getResponsable());
                ge.setArchivo(detalleDepartamento.getArchivo());
                ge.setValidar(detalleDepartamento.getValidar());
                ge.setEstado(detalleDepartamento.getEstado());
                ge.setFechaAccion(new Date());
                ge.setFechaFinalizado(detalleDepartamento.getFechaFinalizado());
                break;
            }
        }
        if (depSubDireccion != null) {
            depSubDireccion = service.guardarSvSolicitudDepartamentoById(depSubDireccion);
            if (depSubDireccion != null && depSubDireccion.getId() != null) {
                dss.setIdDetalleSolicitud(depSubDireccion.getId());
            }
            solicitud.getSvSolicitudDepartamentoCollection().add(depSubDireccion);
        }
        paramt.put("to", to);
        paramt.put("subject", subject);
        paramt.put("message", notificar);
        paramt.put("prioridad", 50);
        paramt.put("departamento", dss);
        paramt.put("departamentos", ldss);
        paramt.put("secretaria", userSecretaria.getUsuario());
        paramt.put("listaArchivos", this.getFiles());
        solicitud = service.actualizarSolicitudServcioyObservaciones(solicitud, ht, session.getName_user(), observ, this.getTaskDataByTaskID().getDescription());
        if (solicitud != null) {
            this.completeTask(session.getTaskID(), paramt);
        }
        this.continuar();
    }

    public void agregarUsuarioDep() {
        if (departamento == null) {
            JsfUti.messageError(null, "", "Debe seleccionar un Departamento.");
            return;
        }
        if (user == null) {
            JsfUti.messageError(null, "", "Debe seleccionar un Usuario.");
            return;
        }
        if (user.getEnte() == null) {
            JsfUti.messageError(null, "", "Usuario no ha registrado datos personales.");
            return;
        }

        if (user.getEnte().getEnteCorreoCollection() == null || user.getEnte().getEnteCorreoCollection().isEmpty()) {
            JsfUti.messageError(null, "", "Usuario no tiene correo.");
            return;
        }

        for (ModelUsuarios directorAsignado : reasignarDeparta) {
            if (directorAsignado.getUsuario().equals(user)) {
                JsfUti.messageError(null, "", "Usuario ya fue asignado.");
                return;
            }
        }
        if (!reasignarDeparta.isEmpty()) {
            JsfUti.messageError(null, "", "Solo puede reasignar a una direccion, si no es la adecuada Elimine la actual.");
            return;
        }
        if (service.getSvSolicitudDepartamentoByIdDireccion(solicitud, departamento, Boolean.TRUE) != null) {
            JsfUti.messageError(null, "", "Dirección ya se encuentra Asignada dentro del Tramite, Verificar en la pestaña Direcciones Involucradas.");
            return;
        }
        ModelUsuarios us = new ModelUsuarios();
        us.setDepartamento(departamento);
        us.setUsuario(user);
        reasignarDeparta.add(us);
    }

    public void eliminarUsuario(ModelUsuarios usuario) {
        reasignarDeparta.remove(usuario);
    }

    public void cargarUsuariosDepartamento() {
        try {
            usuariosDeparta = new ArrayList<>();
            if (departamento != null) {
                for (AclRol a : departamento.getAclRolCollection()) {
                    if (a.getIsDirector() && a.getEstado()) {
                        usuariosDeparta.addAll(a.getAclUserCollection());
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, Messages.errorTransaccion, e);
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

    public AsignarTecnicos() {
    }

    public int getOpcion() {
        return opcion;
    }

    public void setOpcion(int opcion) {
        this.opcion = opcion;
    }

    public String getTipoArchivos() {
        return tipoArchivos;
    }

    public void setTipoArchivos(String tipoArchivos) {
        this.tipoArchivos = tipoArchivos;
    }

    public String getTitleObs() {
        return titleObs;
    }

    public void setTitleObs(String titleObs) {
        this.titleObs = titleObs;
    }

    public String getPlaceholderObs() {
        return placeholderObs;
    }

    public void setPlaceholderObs(String placeholderObs) {
        this.placeholderObs = placeholderObs;
    }

    public String getMsgAdicinal() {
        return msgAdicinal;
    }

    public void setMsgAdicinal(String msgAdicinal) {
        this.msgAdicinal = msgAdicinal;
    }

    public Boolean getAdjuntarInformeDoc() {
        return adjuntarInformeDoc;
    }

    public void setAdjuntarInformeDoc(Boolean adjuntarInformeDoc) {
        this.adjuntarInformeDoc = adjuntarInformeDoc;
    }

    public ModelUsuarios getUserAsignar() {
        return userAsignar;
    }

    public void setUserAsignar(ModelUsuarios userAsignar) {
        this.userAsignar = userAsignar;
    }

    public List<AclUser> getListUsuarios() {
        return listUsuarios;
    }

    public void setListUsuarios(List<AclUser> listUsuarios) {
        this.listUsuarios = listUsuarios;
    }

    public AclUser getUser() {
        return user;
    }

    public void setUser(AclUser user) {
        this.user = user;
    }

    public List<AclUser> getUsuariosDeparta() {
        return usuariosDeparta;
    }

    public void setUsuariosDeparta(List<AclUser> usuariosDeparta) {
        this.usuariosDeparta = usuariosDeparta;
    }

    public List<ModelUsuarios> getReasignarDeparta() {
        return reasignarDeparta;
    }

    public void setReasignarDeparta(List<ModelUsuarios> reasignarDeparta) {
        this.reasignarDeparta = reasignarDeparta;
    }

    public DepartamentoSolicitudServicio getDss() {
        return dss;
    }

    public void setDss(DepartamentoSolicitudServicio dss) {
        this.dss = dss;
    }

    public SvSolicitudDepartamento getDetalleDepartamento() {
        return detalleDepartamento;
    }

    public void setDetalleDepartamento(SvSolicitudDepartamento detalleDepartamento) {
        this.detalleDepartamento = detalleDepartamento;
    }

    public List<GeDepartamento> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<GeDepartamento> departamentos) {
        this.departamentos = departamentos;
    }

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public List<CatPredio> getCatPredioList() {
        return catPredioList;
    }

    public void setCatPredioList(List<CatPredio> catPredioList) {
        this.catPredioList = catPredioList;
    }

    public List<AclUser> getListUsuariosSecretariaPlanificacion() {
        return listUsuariosSecretariaPlanificacion;
    }

    public void setListUsuariosSecretariaPlanificacion(List<AclUser> listUsuariosSecretariaPlanificacion) {
        this.listUsuariosSecretariaPlanificacion = listUsuariosSecretariaPlanificacion;
    }

    public AclUser getUserSecretaria() {
        return userSecretaria;
    }

    public void setUserSecretaria(AclUser userSecretaria) {
        this.userSecretaria = userSecretaria;
    }

}
