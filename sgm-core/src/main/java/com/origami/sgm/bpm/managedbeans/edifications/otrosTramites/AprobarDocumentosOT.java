/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.otrosTramites;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class AprobarDocumentosOT extends BpmManageBeanBaseRoot implements Serializable {
    
    public static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession sess;
    @Inject
    private ReportesView reportes;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    private HistoricoTramites ht;
    private HashMap<String, Object> paramsActiviti;
    private Observaciones obs;
    private List<HistoricoReporteTramite> hrts;
    private Boolean aprobar;
    private OtrosTramites ot;
    protected GeDepartamento departamento;
    
    @PostConstruct
    public void initView() {
        if (sess.esLogueado() && sess.getTaskID() != null) {
            if (sess != null && sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
                if(ht==null)
                    return;
                ot = ht.getSubTipoTramite();
                paramsActiviti = new HashMap<>();
                obs = new Observaciones();
                hrts = (List<HistoricoReporteTramite>) ht.getHistoricoReporteTramiteCollection();
            }
            departamento=(GeDepartamento)services.find(GeDepartamento.class, 12L);
        }
    }
    
    /**
     * Abre el archivo pdf seleccionado en otra pestaña.
     * 
     * @param doc 
     */
    public void verDocumento(HistoricoReporteTramite doc) {
        this.showDocuments(doc.getUrl(), "pdf");
    }
    
    /**
     * Muestra el dialog para ingressar las observaciones de la tarea.
     * 
     * @param aprobado 
     */
    public void mostrarObservaciones(boolean aprobado) {
        aprobar = aprobado;
        JsfUti.executeJS("PF('obs').show();");
        JsfUti.update("frmObs");
    }
    
    /**
     * Completa la tarea y guarda las entidades necesarias en la base de datos.
     * 
     */
    public void completarTarea(){
        try{
            if(obs.getObservacion()!=null){

                paramsActiviti.put("aprobar", aprobar);
                paramsActiviti.put("aprobado", aprobar);
                if (departamento!=null && departamento.getAclRolCollection()!=null && !departamento.getAclRolCollection().isEmpty()) {
                    for (AclRol rol : departamento.getAclRolCollection()) {
                        if (rol.getId().equals(98L) && rol.getAclUserCollection()!=null && !rol.getAclUserCollection().isEmpty()) {
                            for (AclUser user : rol.getAclUserCollection()) {
                                paramsActiviti.put("renta", user.getUsuario());
                                break;
                            }
                            break;
                        }
                    }
                }
                if(aprobar && ot.getTipoDeTramite().getIdentificacion().compareTo(BigInteger.valueOf(new Long(2))) == 0){
                    ht.setEstado("Finalizado");
                    services.update(ht);
                }
                paramsActiviti.put("descripcion", ht.getTipoTramiteNombre());
                paramsActiviti.put("prioridad", 50);
                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setUserCre(sess.getName_user());
                obs.setTarea(this.getTaskDataByTaskID().getName());
                services.persist(obs);
                this.completeTask(this.getTaskId(), paramsActiviti);
                this.continuar();
            }else{
                JsfUti.messageError(null, "Error", "Debe ingresar una observación antes de continuar.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void observacionDefault(){
        if (obs!=null && obs.getObservacion()==null) {
            obs.setObservacion(this.getTaskDataByTaskID().getName());
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public List<HistoricoReporteTramite> getHrts() {
        return hrts;
    }

    public void setHrts(List<HistoricoReporteTramite> hrts) {
        this.hrts = hrts;
    }
    
}
