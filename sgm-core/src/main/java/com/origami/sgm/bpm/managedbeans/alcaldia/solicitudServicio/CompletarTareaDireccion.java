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
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoTramites;
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
 * @author origami
 */
@Named
@ViewScoped
public class CompletarTareaDireccion extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(CompletarTareaDireccion.class.getName());

    @javax.inject.Inject
    private SolicitudServicosServices service;

    protected HistoricoTramites ht;
    protected SvSolicitudServicios solicitud = new SvSolicitudServicios();
    protected DepartamentoSolicitudServicio dss;
    protected SvSolicitudDepartamento detalleDepartamento;

    ///VARIABLES PARA EL REENVIO
    protected SvSolicitudDepartamento detalleDepartamento2;
    protected GeDepartamento departamento;
    protected List<AclUser> usuariosDeparta = new ArrayList<>();
    protected AclUser user;
    protected List<ModelUsuarios> reasignarDeparta = new ArrayList<>();
    protected List<DepartamentoSolicitudServicio> ldss;

    protected String tipoArchivos = SisVars.formatoArchivos;
    private String observ;

    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            try {
                detalleDepartamento2 = new SvSolicitudDepartamento();
                setTaskId(session.getTaskID());
                ht = service.getPropiedadHorizontalServices().getPermiso().getHistoricoTramiteById(new Long(this.getVariable(session.getTaskID(), "tramite").toString()));
                solicitud = service.getSolicitudServicioByTramite(ht.getIdTramite());
                dss = (DepartamentoSolicitudServicio) this.getVariable(session.getTaskID(), "departamento");
                ldss = (List<DepartamentoSolicitudServicio>) this.getVariable(session.getTaskID(), "departamentos");
                detalleDepartamento = service.getSvSolicitudDepartamentoById(dss.getIdDetalleSolicitud());
                departamento = acl.find(GeDepartamento.class, 7L);

                this.setBpmTramite(ht);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, Messages.error, e);
            }

        } else {
            this.continuar();
        }
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

    public void completarSolicitudDireccion() {
        HashMap<String, Object> paramt = new HashMap<>();
        String mensaje;
        if (observ == null) {
            JsfUti.messageError(null, "", Messages.observaciones);
            return;
        }
        try {
            detalleDepartamento.setFechaFinalizado(new Date());
            for (SvSolicitudDepartamento svSolDep : solicitud.getSvSolicitudDepartamentoCollection()) {
                if (svSolDep.getId().equals(detalleDepartamento.getId())) {
                    svSolDep = detalleDepartamento;
                    break;
                }
            }
            ht.setEstado("Finalizado");
            solicitud = service.actualizarSolicitudServcioyObservaciones(solicitud, ht, session.getName_user(), observ, this.getTaskDataByTaskID().getName());
            if (solicitud != null) {
                paramt.put("listaArchivos", this.getFiles());
                paramt.put("tecincoAsignado", Boolean.TRUE);
                this.completeTask(session.getTaskID(), paramt);
            } else {
                JsfUti.messageError(null, "", Messages.error);
            }
            this.continuar();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, Messages.errorTransaccion, e);
        }
    }

    public void cambioTecnico() {
        HashMap<String, Object> paramt = new HashMap<>();
        String avisoDepartamento = "";

        List<AclUser> usersj = acl.findAll(Querys.getAclUserDirectorByRol, new String[]{"idRol"}, new Object[]{85L});
        if (usersj != null && !usersj.isEmpty()) {
            AclUser u = usersj.get(usersj.size() - 1);
            dss.setAccion(5L);
            dss.setDirector(u.getUsuario());
            dss.setCorreoDirector(u.getEnte().getEnteCorreoCollection().get(0).getEmail());
        }
        detalleDepartamento.setEstado(Boolean.FALSE);
        detalleDepartamento.setFechaFinalizado(new Date());
        observ = "S.S. REENVIADA: " + detalleDepartamento2.getInforme();
        paramt.put("tecincoAsignado", Boolean.FALSE);
        paramt.put("prioridad", 50);
        paramt.put("departamento", dss);
        paramt.put("departamentos", ldss);
        paramt.put("listaArchivos", this.getFiles());
        solicitud = service.actualizarSolicitudServcioyObservaciones(solicitud, ht, session.getName_user(), observ, this.getTaskDataByTaskID().getDescription());
        if (solicitud != null) {
            this.completeTask(session.getTaskID(), paramt);
        }
        this.continuar();

    }

    public void agregarUsuarioDep() {
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

    public SvSolicitudDepartamento getDetalleDepartamento() {
        return detalleDepartamento;
    }

    public void setDetalleDepartamento(SvSolicitudDepartamento detalleDepartamento) {
        this.detalleDepartamento = detalleDepartamento;
    }

    public String getTipoArchivos() {
        return tipoArchivos;
    }

    public void setTipoArchivos(String tipoArchivos) {
        this.tipoArchivos = tipoArchivos;
    }

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public SvSolicitudDepartamento getDetalleDepartamento2() {
        return detalleDepartamento2;
    }

    public void setDetalleDepartamento2(SvSolicitudDepartamento detalleDepartamento2) {
        this.detalleDepartamento2 = detalleDepartamento2;
    }

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public List<AclUser> getUsuariosDeparta() {
        return usuariosDeparta;
    }

    public void setUsuariosDeparta(List<AclUser> usuariosDeparta) {
        this.usuariosDeparta = usuariosDeparta;
    }

    public AclUser getUser() {
        return user;
    }

    public void setUser(AclUser user) {
        this.user = user;
    }

    public List<ModelUsuarios> getReasignarDeparta() {
        return reasignarDeparta;
    }

    public void setReasignarDeparta(List<ModelUsuarios> reasignarDeparta) {
        this.reasignarDeparta = reasignarDeparta;
    }

}
