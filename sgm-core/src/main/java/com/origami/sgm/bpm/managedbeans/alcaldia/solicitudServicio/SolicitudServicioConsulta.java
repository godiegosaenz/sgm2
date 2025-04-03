/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.alcaldia.solicitudServicio;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.SvSolicitudDepartamento;
import com.origami.sgm.entities.SvSolicitudServicios;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.SvSolicitudServiciosLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
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
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class SolicitudServicioConsulta extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(SolicitudServicioConsulta.class.getName());

    @javax.inject.Inject
    private SolicitudServicosServices service;
    
    @javax.inject.Inject
    private Entitymanager acl;
    
    @Inject
    private ServletSession ss;
    private HistoricoTramites hist;
    private SvSolicitudServicios solicitud;
    private boolean audiencia = false;
    private List<HistoricTaskInstance> task;
    protected CatEnte solicitante;

    protected SvSolicitudServiciosLazy solicitudServiciosLazy;
    private CatEnteLazy solicitantes;

    @PostConstruct
    public void initView() {
        solicitudServiciosLazy = new SvSolicitudServiciosLazy();
        solicitantes = new CatEnteLazy();
    }

    public SvSolicitudServiciosLazy getSolicitudServiciosLazy() {
        return solicitudServiciosLazy;
    }

    public void setSolicitudServiciosLazy(SvSolicitudServiciosLazy solicitudServiciosLazy) {
        this.solicitudServiciosLazy = solicitudServiciosLazy;
    }

    public void imprimirTicket(SvSolicitudServicios s) {
        try {
            HistoricoTramites ht = service.getPropiedadHorizontalServices().getPermiso().getHistoricoTramiteById(s.getTramite().getId());
            ss.instanciarParametros();
            ss.agregarParametro("P_TITULO", "Número de Trámite");
            ss.agregarParametro("P_SUBTITULO", "S.S. " + s.getTramite().getTipoTramite().getDescripcion());
            ss.agregarParametro("P_NUMERO_TRAMITE", ht.getId().toString());
            ss.agregarParametro("NOM_SOLICITANTE", ht.getNombrePropietario());
            ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("DESCRIPCION", s.getTipoServicio().getNombre());
            ss.setNombreReporte("plantilla1");
            ss.setTieneDatasource(false);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
    }
    
    public void imprimirInforme(SvSolicitudServicios s){
        //DATOS PARA EL REPORTE
        ss.instanciarParametros();
        ss.setTieneDatasource(true);
        ss.setNombreReporte("asignacionDepartamento_1");
        ss.setNombreSubCarpeta("solicitudServicio");
        // INGRESO DE VARIABLES DEL REPORTE
        ss.agregarParametro("FECHA", new Date());
        ss.agregarParametro("SOLICITUD", s.getId());
        ss.agregarParametro("TRAMITE", s.getTramite().getId());
        ss.agregarParametro("OBSERVACION", s.getTramite().getId());
        ss.agregarParametro("DEPARTAMENTOS", s.getSvSolicitudDepartamentoCollection());
        ss.agregarParametro("LOGO", Faces.getRealPath("/").concat(SisVars.logoReportes));
        ss.agregarParametro("LOGO_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
        // MOSTRAR EL REPORTE
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void ver(SvSolicitudServicios s) {
        try {
            task=null;
            hist = new HistoricoTramites();
            solicitud = new SvSolicitudServicios();
            solicitud = s;
            audiencia = s.getTipoServicio().getId().compareTo(783L) == 0;
            HistoricoTramites ht = service.getPropiedadHorizontalServices().getPermiso().getHistoricoTramiteById(s.getTramite().getId());
            
            if (ht != null) {
                hist = ht;
            }
            if (ht != null && ht.getIdProceso()!=null) {
                task = engine.getTaskByProcessInstanceId(ht.getIdProceso());
            }

            JsfUti.update("frmInfo");
            JsfUti.executeJS("PF('info').show()");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
    }

    public List<HistoricIdentityLink> getAssignee(String IdTask) {
        List<HistoricIdentityLink> identy = null;
        if (hist != null) {
            identy = obtenerHistoricIdentityLinkByIdTask(IdTask);
        }

        return identy;
    }

    public void aceptar() {
        JsfUti.executeJS("PF('info').hide()");
        JsfUti.update("formSolServCon");
    }

    public List<Attachment> getDocumentos() {
        if (hist != null && hist.getIdProceso()!=null) {
            return this.getProcessInstanceAllAttachmentsFiles(hist.getIdProceso());
        }
        return null;
    }
    
    public List<SvSolicitudDepartamento> getDireccionesActivas(SvSolicitudServicios s){
        List<SvSolicitudDepartamento> direccionesActivas= new ArrayList<>();
        if (s.getSvSolicitudDepartamentoCollection()!=null && !s.getSvSolicitudDepartamentoCollection().isEmpty()) {
            for (SvSolicitudDepartamento de : s.getSvSolicitudDepartamentoCollection()) {
                if (de.getEstado()) {
                    direccionesActivas.add(de);
                }
            }
        }
        return direccionesActivas;
    }
    
    public void reingreso(SvSolicitudServicios s){
        try{
            GeTipoTramite tipoTramite = service.getPropiedadHorizontalServices().getPermiso().getGeTipoTramiteById(20L);
            AclUser secretaria = service.getPropiedadHorizontalServices().getPermiso().getAclUserById(18L);
            AclUser asistente = service.getPropiedadHorizontalServices().getPermiso().getAclUserById(197L);
            HashMap<String, Object> paramt= new HashMap<>();
            paramt.put("asistenteAlcaldia", asistente.getUsuario());
            paramt.put("secretariaAlcaldia", secretaria.getUsuario());
            paramt.put("from", SisVars.correo);
            if (secretaria.getEnte() != null && secretaria.getEnte().getEnteCorreoCollection()!=null && !secretaria.getEnte().getEnteCorreoCollection().isEmpty()) {
                paramt.put("to", secretaria.getEnte().getEnteCorreoCollection().get(0).getEmail());
            } else {
                paramt.put("to", "no_tiene_correo@hotmail.com");
            }
            paramt.put("carpeta", tipoTramite.getCarpeta());
            paramt.put("listaArchivos", this.getFiles());
            paramt.put("listaArchivosFinal", new ArrayList<>());
            paramt.put("prioridad", 50);
            paramt.put("descripcion", tipoTramite.getDescripcion());// NOMBRE DEL TRAMITE
            paramt.put("audiencia", audiencia);
            paramt.put("tramite", s.getTramite().getId());
            
            ProcessInstance pro = this.startProcessByDefinitionKey(tipoTramite.getActivitykey(), paramt);
            if (pro != null) {
                s.getTramite().setCarpetaRep(s.getTramite().getId() + "-" + pro.getId());
                s.getTramite().setIdProceso(pro.getId());
                s.getTramite().setIdProcesoTemp(pro.getId());
                service.getPropiedadHorizontalServices().getPermiso().actualizarHistoricoTramites(s.getTramite());
                JsfUti.messageInfo(null, "Ingreso Exitoso.", "");
            }
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
            JsfUti.messageError(null, "Ingreso Fallo.", "");
        }
    }
    
    public void openDlgEdit(SvSolicitudServicios s){
        try {
            this.solicitud = s;
            JsfUti.update("formEdit");
            JsfUti.executeJS("PF('dlgEdit').show();");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
    }
    
    public void editarSolicitud(){
        try {
            acl.persist(this.solicitud.getTramite().getSolicitante());
            this.solicitud.getTramite().setNombrePropietario(this.solicitud.getTramite().getSolicitante().getEsPersona()?((this.solicitud.getTramite().getSolicitante().getApellidos()!=null?this.solicitud.getTramite().getSolicitante().getApellidos():"")+" "+(this.solicitud.getTramite().getSolicitante().getNombres()!=null?this.solicitud.getTramite().getSolicitante().getNombres():"")):(this.solicitud.getTramite().getSolicitante().getRazonSocial()!=null?this.solicitud.getTramite().getSolicitante().getRazonSocial():""));
            this.solicitud.getTramite().setCorreos(this.getCorreosByCatEnte(this.solicitud.getTramite().getSolicitante()));
            acl.persist(this.solicitud.getTramite());
            this.solicitud.setEnteSolicitante(this.solicitud.getTramite().getSolicitante());
            this.solicitud=service.editarSolicitud(this.solicitud);
            if (this.solicitud!=null) {
                this.setVariableByProcessInstance(this.solicitud.getTramite().getIdProceso(), "to", this.getCorreosByCatEnte(this.solicitud.getTramite().getSolicitante()));
                JsfUti.messageInfo(null, "Datos Actualizados.", "");
            }else{
                JsfUti.messageError(null, "Error. Intente nuevamente.", "");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public SolicitudServicioConsulta() {
    }

    public HistoricoTramites getHist() {
        return hist;
    }

    public void setHist(HistoricoTramites hist) {
        this.hist = hist;
    }

    public SvSolicitudServicios getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SvSolicitudServicios solicitud) {
        this.solicitud = solicitud;
    }

    public boolean isAudiencia() {
        return audiencia;
    }

    public void setAudiencia(boolean audiencia) {
        this.audiencia = audiencia;
    }

    public List<HistoricTaskInstance> getTask() {
        return task;
    }

    public void setTask(List<HistoricTaskInstance> task) {
        this.task = task;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public CatEnteLazy getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(CatEnteLazy solicitantes) {
        this.solicitantes = solicitantes;
    }

}
