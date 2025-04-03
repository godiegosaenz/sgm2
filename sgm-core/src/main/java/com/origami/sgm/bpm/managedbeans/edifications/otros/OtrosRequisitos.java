/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.otros;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.DisparadorTramites;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.lazymodels.OtrosTramitesLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class OtrosRequisitos extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private Entitymanager serv;
    @Inject
    private ServletSession servletSession;
    @javax.inject.Inject
    private SeqGenMan seq;
    private Boolean userValido = false;
    private GeTipoTramite tramite;
    private CatEnte solicitante;
    private HashMap<String, Object> params;
    private HistoricoTramites ht = null;
    private Observaciones obs = null;
    private DisparadorTramites dis = null;
    private boolean iniciar = true;
    private String cedulaRuc;
    private List<CatPredio> predios;
    private CatPredio predio;
    private Boolean mostrarRequisitos = false;
    private OtrosTramitesLazy otrosLazy;
    private OtrosTramites tramSelec;
    private List<OtrosRequisitos> requisitos;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    public void initView() {
        try {
            if (sess != null) {
                obs = new Observaciones();
                params = new HashMap<>();
                if (sess.getActKey() != null) {
                    otrosLazy = new OtrosTramitesLazy();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(OtrosRequisitos.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void seleccionarTramite() {
        
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public Boolean getUserValido() {
        return userValido;
    }

    public void setUserValido(Boolean userValido) {
        this.userValido = userValido;
    }

    public GeTipoTramite getTramite() {
        return tramite;
    }

    public void setTramite(GeTipoTramite tramite) {
        this.tramite = tramite;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
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

    public DisparadorTramites getDis() {
        return dis;
    }

    public void setDis(DisparadorTramites dis) {
        this.dis = dis;
    }

    public boolean getIniciar() {
        return iniciar;
    }

    public void setIniciar(boolean iniciar) {
        this.iniciar = iniciar;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public List<CatPredio> getPredios() {
        return predios;
    }

    public void setPredios(List<CatPredio> predios) {
        this.predios = predios;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Boolean getMostrarRequisitos() {
        return mostrarRequisitos;
    }

    public void setMostrarRequisitos(Boolean mostrarRequisitos) {
        this.mostrarRequisitos = mostrarRequisitos;
    }

    public OtrosTramitesLazy getOtrosLazy() {
        return otrosLazy;
    }

    public void setOtrosLazy(OtrosTramitesLazy otrosLazy) {
        this.otrosLazy = otrosLazy;
    }

    public List<OtrosRequisitos> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<OtrosRequisitos> requisitos) {
        this.requisitos = requisitos;
    }

    public OtrosTramites getTramSelec() {
        return tramSelec;
    }

    public void setTramSelec(OtrosTramites tramSelec) {
        this.tramSelec = tramSelec;
    }
    

}
