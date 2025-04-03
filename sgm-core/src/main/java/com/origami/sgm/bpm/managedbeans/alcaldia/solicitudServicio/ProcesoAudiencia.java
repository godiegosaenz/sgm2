/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.alcaldia.solicitudServicio;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.ModelUsuarios;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.SvSolicitudServicios;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import java.io.Serializable;
import java.util.ArrayList;
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
public class ProcesoAudiencia extends BpmManageBeanBaseRoot implements Serializable{

    private static final Logger LOG = Logger.getLogger(ProcesoAudiencia.class.getName());

    @javax.inject.Inject
    private SolicitudServicosServices service;
    
    protected HistoricoTramites ht;
    protected SvSolicitudServicios solicitud = new SvSolicitudServicios();
    protected GeDepartamento departamento;
    protected List<GeDepartamento> departamentos;
    protected List<AclUser> usuariosDepartamentos;
    protected AclUser usuario;
    protected List<ModelUsuarios> usuariosAsignados= new ArrayList<>();
    protected MsgFormatoNotificacion msg;
    protected String observ;
    protected String tipoArchivos = SisVars.formatoArchivos;
    
    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            try {
                setTaskId(session.getTaskID());
                ht = service.getPropiedadHorizontalServices().getPermiso().getHistoricoTramiteById(new Long(this.getVariable(session.getTaskID(), "tramite").toString()));
                solicitud = service.getSolicitudServicioByTramite(ht.getIdTramite());
                departamentos=service.getDepartamentos();
                msg = service.getNormasConstruccion().getMsgFormatoNotificacionByTipo(2L);
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
            usuariosDepartamentos= new ArrayList<>();
            if(departamento!=null){
                for (AclRol a : departamento.getAclRolCollection()) {
                    usuariosDepartamentos.addAll(a.getAclUserCollection());
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, Messages.errorTransaccion, e);
        }
    }
    
    public void agregarUsuario() {
        if (departamento == null) {
            JsfUti.messageError(null, "", "Debe seleccionar un Departamento.");
            return;
        }
        if (usuario == null) {
            JsfUti.messageError(null, "", "Debe seleccionar un Usuario.");
            return;
        }
        if(usuario.getEnte()==null){
            JsfUti.messageError(null, "", "Usuario no ha registrado datos personales.");
            return;
        }else if (usuario.getEnte().getEnteCorreoCollection()==null||usuario.getEnte().getEnteCorreoCollection().isEmpty()) {
            JsfUti.messageError(null, "", "Usuario no tiene correo.");
            return;
        }else{
            for (ModelUsuarios directorAsignado : usuariosAsignados) {
                if (directorAsignado.getUsuario().equals(usuario)) {
                    JsfUti.messageError(null, "", "Usuario ya fue asignado.");
                    return;
                }
            }
        }
        ModelUsuarios u = new ModelUsuarios();
        u.setDepartamento(departamento);
        u.setUsuario(usuario);
        usuariosAsignados.add(u);

    }
    
    public void validarDefinirAudiencia(){
        if (solicitud.getNotificacion()==null) {
            JsfUti.messageError(null, "", "Debe ingresar Notificacion.");
            return;
        }
        if (usuariosAsignados.isEmpty()) {
            JsfUti.messageError(null, "", "Debe agregar usuarios.");
            return;
        }
        JsfUti.update("frmObsCor");
        JsfUti.executeJS("PF('obs').show()");
    }
    
    public void completarDefinirAudiencia(){
        HashMap<String, Object> paramt = new HashMap<>();
        String mensaje;
        String correosUsuarios="";
        if (observ == null) {
            JsfUti.messageError(null, "", Messages.observaciones);
            return;
        }
        try{
            solicitud=service.actualizarSolicitudServcioyObservaciones(solicitud, ht, session.getName_user(), observ, this.getTaskDataByTaskID().getName());
            if(solicitud!=null){
                mensaje = msg.getHeader() + "<br/>"
                        + "Saludos,<br/><br/>"
                        + solicitud.getNotificacion()
                        + "<br/><br/>Gracias <br/><br/>" + msg.getFooter();
                
                for (ModelUsuarios usuariosAsignado : usuariosAsignados) {
                    correosUsuarios=correosUsuarios+getCorreos(usuariosAsignado.getUsuario().getEnte().getEnteCorreoCollection())+",";
                }
                correosUsuarios=correosUsuarios.substring(0, correosUsuarios.length()-1);
                paramt.put("from", SisVars.correo);
                paramt.put("to", correosUsuarios);
                paramt.put("subject", "Servicio Solicitado - Audiencia " + ht.getId());
                paramt.put("message", mensaje);
                this.completeTask(session.getTaskID(), paramt);
            }else{
                JsfUti.messageError(null, "", Messages.error);
            }
            this.continuar();
        } catch(Exception e){
            LOG.log(Level.SEVERE, Messages.errorTransaccion, e);
        }
    }
    
    public void validarInformeAudiencia(){
        if (solicitud.getInforme()==null) {
            JsfUti.messageError(null, "", "Debe ingresar Informe.");
            return;
        }
        if (this.getFiles().isEmpty()) {
            JsfUti.messageError(null, "", "Debe agregar Documento.");
            return;
        }
        JsfUti.update("frmObsCor");
        JsfUti.executeJS("PF('obs').show()");
    }
    
    public void completarInformeAudiencia(){
        HashMap<String, Object> paramt = new HashMap<>();
        if (observ == null) {
            JsfUti.messageError(null, "", Messages.observaciones);
            return;
        }
        try{
            ht.setEstado("Finalizado");
            ht.setObservacion(solicitud.getInforme());
            solicitud=service.actualizarSolicitudServcioyObservaciones(solicitud, ht, session.getName_user(), observ, this.getTaskDataByTaskID().getName());
            if(solicitud!=null){
                paramt.put("listaArchivos", this.getFiles());
                this.completeTask(session.getTaskID(), paramt);
            }else{
                JsfUti.messageError(null, "", Messages.error);
            }
            this.continuar();
        } catch(Exception e){
            LOG.log(Level.SEVERE, Messages.errorTransaccion, e);
        }
    }
    
    public void eliminarUsuario(ModelUsuarios usuario) {
        usuariosAsignados.remove(usuario);
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

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public List<GeDepartamento> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<GeDepartamento> departamentos) {
        this.departamentos = departamentos;
    }

    public List<AclUser> getUsuariosDepartamentos() {
        return usuariosDepartamentos;
    }

    public void setUsuariosDepartamentos(List<AclUser> usuariosDepartamentos) {
        this.usuariosDepartamentos = usuariosDepartamentos;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public List<ModelUsuarios> getUsuariosAsignados() {
        return usuariosAsignados;
    }

    public void setUsuariosAsignados(List<ModelUsuarios> usuariosAsignados) {
        this.usuariosAsignados = usuariosAsignados;
    }

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public String getTipoArchivos() {
        return tipoArchivos;
    }

    public void setTipoArchivos(String tipoArchivos) {
        this.tipoArchivos = tipoArchivos;
    }
    
}
