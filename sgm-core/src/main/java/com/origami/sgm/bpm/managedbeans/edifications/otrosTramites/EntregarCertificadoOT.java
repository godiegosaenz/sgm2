/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.otrosTramites;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.hibernate.Hibernate;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class EntregarCertificadoOT extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private ServletSession servletSession;

    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    
    private Observaciones obs;
    private HistoricoTramites ht;
    private List<HistoricoReporteTramite> hrts;
    private GeTipoTramite tipoTramite;
    private Boolean validar;
    private MsgFormatoNotificacion formatoMsg;
    private HashMap<String, Object> paramsActiviti;
    private AclRol dirCatastro;
    private AclUser catastro;
    private List temp;
    private OtrosTramites oTramite;
    private Long cod_titulo;
    
    @PostConstruct
    public void init(){
        if (uSession != null && uSession.getTaskID() != null) {
            this.setTaskId(uSession.getTaskID());
            paramsActiviti = new HashMap<String, Object>();
            ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
            validar = false;
            if(ht!=null){
                oTramite = ht.getSubTipoTramite();
                String pagado = null;
                obs = new Observaciones();
                hrts = services.findAll(Querys.getHistoricoReporteTramiteByEstadoAndTramiteID, new String[]{"idTramite"}, new Object[]{ht});
                
                tipoTramite = (GeTipoTramite) services.find(GeTipoTramite.class, ht.getTipoTramite().getId());
               
//                if (ht.getNumLiquidacion() != null) {
//                    cod_titulo = (Long) services.find(QuerysFinanciero.getCodigoTituloReporte, new String[]{"prefijo", "nomTitulo"}, new Object[]{oTramite.getPrefijo(), oTramite.getTituloReporte()});
//                    if(cod_titulo == null){
//                        JsfUti.messageInfo(null, "Info", "Error al consultar el trámite.");
//                        return;
//                    }
//                }
                
                Boolean pagadoSgm= Boolean.FALSE;
                if (ht!=null && ht.getRenLiquidacionCollection()!=null && !ht.getRenLiquidacionCollection().isEmpty()) {
                    System.out.println("/*** "+ht.getRenLiquidacionCollection());
                    for (RenLiquidacion re : ht.getRenLiquidacionCollection()) {
                        if (re.getEstadoLiquidacion().getId().equals(1L)) {
                            pagadoSgm= Boolean.TRUE;
                        }
                    }
                }
                if(pagadoSgm){
                    JsfUti.messageInfo(null, "Info", "Tasa cancelada.");
                    validar = true;
                    formatoMsg = (MsgFormatoNotificacion) services.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{Long.parseLong("1")});
                }else {
                    JsfUti.messageError(null, "Error", "No ha sido cancelada la tasa.");
                    return;
                }
                dirCatastro = (AclRol) services.find(AclRol.class, new Long(68));
                Hibernate.initialize(dirCatastro.getAclUserCollection());
                temp =  (List) dirCatastro.getAclUserCollection();
                catastro = (AclUser) temp.get(0);
            }
        }
    }
    
    /**
     * Cuando se imprime el certificado al cliente se procede a terminar la tarea.
     */
    public void completarTarea(){
        String msg="";
        if(!validar){
            JsfUti.messageError(null, "Error", "No se puede completar la tarea porque no se ha cancelado la tasa.");
            return;
        }
        if (!obs.getObservacion().equals("") ) {
            try {
                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setUserCre(uSession.getName_user());
                obs.setTarea("Entregar Certificado Usuario");
                
                msg = formatoMsg.getHeader() + "Tiene una nueva tarea de asignación de técnico." + formatoMsg.getFooter();
                ht.setEstado("Finalizado");
                services.update(ht);
                
                /*paramsActiviti.put("to", getCorreosByCatEnte(catastro.getEnte()));
                paramsActiviti.put("message", msg);
                paramsActiviti.put("subject", "Trámite: "+ht.getId());*/
                
                this.completeTask(this.getTaskId(), paramsActiviti);
                this.continuar();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public GeTipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(GeTipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public Boolean getValidar() {
        return validar;
    }

    public void setValidar(Boolean validar) {
        this.validar = validar;
    }

    public Long getCod_titulo() {
        return cod_titulo;
    }

    public void setCod_titulo(Long cod_titulo) {
        this.cod_titulo = cod_titulo;
    }

    public List<HistoricoReporteTramite> getHrts() {
        return hrts;
    }

    public void setHrts(List<HistoricoReporteTramite> hrts) {
        this.hrts = hrts;
    }
    
}
