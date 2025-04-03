/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.alcaldia.solicitudServicio;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.SolicitudDepartamentoModel;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.SvSolicitudServicios;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Attachment;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class SolicitudDepartamentos extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(SolicitudDepartamentos.class.getName());

    @javax.inject.Inject
    private SolicitudServicosServices service;
    @Inject
    private ServletSession ss;

    protected List<SvSolicitudServicios> solicitudes;
    protected String estado = "todas";
    protected Date fechaDesde;
    protected Date fechaHasta;
    protected String path;
    
    private HistoricoTramites hist;
    private SvSolicitudServicios solicitud;
    private boolean audiencia = false;
    private List<HistoricTaskInstance> task;

    @PostConstruct
    public void initView() {
        path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("//");
        estado = "todas";
        if (!session.getDepts().isEmpty() && session.getEsDirector()) {
            solicitudes = service.getListSolicitudDepartamento(session.getDepts().get(0), estado, fechaDesde, fechaHasta);
        }

    }

    public void actualizar() {
        solicitudes = service.getListSolicitudDepartamento(session.getDepts().get(0), estado, fechaDesde, fechaHasta);
    }

    public void generarReporte() throws SQLException {
//        ComboPooledDataSource dataSource = null;
        ss.instanciarParametros();
        ss.setNombreReporte("solicitudesPorDepartamento");
        ss.setTieneDatasource(true);
        ss.setNombreSubCarpeta("solicitudServicio");

        try {
//            dataSource = (ComboPooledDataSource) ApplicationContextUtils.getBean("dataSource");
//            ss.agregarParametro("SUBREPORT_CONNECTION", dataSource.getConnection());
//            ss.agregarParametro("SUBREPORT_DIR", path + "/reportes/solicitudServicio/");
            
            ss.agregarParametro("LOGO", path + SisVars.logoReportes);

            List<SolicitudDepartamentoModel> listaSolicitud = service.getModelSolicitud(estado, fechaDesde, fechaHasta, session.getDepts().get(0));
//            for (SolicitudDepartamentoModel m : listaSolicitud) {
//                m.setAsignado(usuariosCand(m.getSolicitud()));
//            }
            ss.agregarParametro("list", listaSolicitud);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoDS");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
//            if (dataSource != null) {
//                dataSource.getConnection().close();
//            }
        } finally {
//            if (dataSource != null) {
//                dataSource.getConnection().close();
//            }
        }
    }
    
    public void ver(SvSolicitudServicios s) {
        try {
            hist = new HistoricoTramites();
            solicitud = s;
            audiencia = s.getTipoServicio().getId().compareTo(783L) == 0;
            HistoricoTramites ht = service.getPropiedadHorizontalServices().getPermiso().getHistoricoTramiteById(s.getTramite().getId());
            if (ht != null) {
                hist = ht;
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
    
    public List<Attachment> getDocumentos() {
        if (hist != null) {
            return this.getProcessInstanceAllAttachmentsFiles(hist.getIdProceso());
        }
        return null;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public List<SvSolicitudServicios> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<SvSolicitudServicios> solicitudes) {
        this.solicitudes = solicitudes;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
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

}
