/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.permisoconstruccion;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.ResTareasUsuarios;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
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
import util.Faces;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class VerificarPredio extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private UserSession sess;
    @Inject
    private ServletSession ss;

    @javax.inject.Inject
    private Entitymanager serv;
    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    private HistoricoTramites tramite;
    private ResTareasUsuarios resumenTecnico;
    private List<ResTareasUsuarios> resTecnicos;
    private Observaciones obs;
    private HashMap<String, Object> params;
    private MsgFormatoNotificacion msg;

    private CatPredio predio;
    private Boolean existePredio = false;
    private Boolean modificado = false;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    public void initView() {
        try {
            if (sess != null && sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());

                tramite = (HistoricoTramites) serv.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
                obs = new Observaciones();
                params = new HashMap<>();
                msg = (MsgFormatoNotificacion) acl.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{1L});

                CatPredio p = permisoServices.getCatPredioByCiudadelaMzSolar(tramite.getUrbanizacion().getId(), tramite.getMz(), tramite.getSolar());

                if (p != null) {
                    predio = p;
                    modificado = p.getNumPredio() != null;
                    existePredio = true;
                } else {
                    existePredio = false;
                }
                if(!modificado){
                   modificado = tramite.getNumPredio() != null; 
                }
                
            }
        } catch (Exception e) {
            Logger.getLogger(VerificarPredio.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void validar() {
        if (tramite.getNumPredio() == null) {
            JsfUti.messageError(null, "", "Debe ingresar el Número del Predio.");
            return;
        }
        JsfUti.executeJS("PF('obs').show()");
        JsfUti.update("frmObs");
    }

    public void completarTarea() {
        try {
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(tramite);
            obs.setUserCre(sess.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());
            if (serv.persist(obs) != null) {
                serv.persist(tramite);
                params.put("tdocs", false);
                params.put("aprobado", true);
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            }

        } catch (Exception e) {
            Logger.getLogger(VerificarPredio.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void editar() {
        ss.instanciarParametros();
        ss.agregarParametro("idPredio", predio.getId());
        ss.agregarParametro("edit", true);
        Faces.redirectFaces("/faces/vistaprocesos/catastro/editarPredio.xhtml");
    }

    public void crear() {
        ss.instanciarParametros();
        ss.agregarParametro("proceso", true);
        ss.agregarParametro("idTramite", tramite.getId());
        Faces.redirectFaces("/faces/vistaprocesos/catastro/agregarPredio.xhtml");
    }

    public ResTareasUsuarios getResumenTecnico() {
        return resumenTecnico;
    }

    public void setResumenTecnico(ResTareasUsuarios resumenTecnico) {
        this.resumenTecnico = resumenTecnico;
    }

    public List<ResTareasUsuarios> getResTecnicos() {
        return resTecnicos;
    }

    public void setResTecnicos(List<ResTareasUsuarios> resTecnicos) {
        this.resTecnicos = resTecnicos;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Boolean getExistePredio() {
        return existePredio;
    }

    public void setExistePredio(Boolean existePredio) {
        this.existePredio = existePredio;
    }

    public Boolean getModificado() {
        return modificado;
    }

    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }

}
