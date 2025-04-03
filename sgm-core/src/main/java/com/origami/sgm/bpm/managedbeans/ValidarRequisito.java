/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans;

import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author origami-idea, carlosLoorVargas
 */
@Named
@ViewScoped
public class ValidarRequisito implements Serializable {

    @Inject
    private UserSession session;
    
    private List<GeTipoTramite> tramites;
    private GeTipoTramite tipoTramite;
    private List<GeRequisitosTramite> requisitos;
    private String entrada;
    private ArrayList entradas;

    @javax.inject.Inject
    private Entitymanager aclServices;

    @PostConstruct
    public void init() {
        entradas = new ArrayList();
        tramites = aclServices.findAll(Querys.getGeTipoTramiteById, new String[]{"id"}, new Object[]{1L});
    }

    public void listarRequisitos() {
        List<GeRequisitosTramite> list = new ArrayList();
        if (tipoTramite != null) {
            requisitos = (List<GeRequisitosTramite>) tipoTramite.getGeRequisitosTramiteCollection();
            try {
                for (GeRequisitosTramite temp : requisitos) {
                    if (temp.getEstado() != null) {
                        if (temp.getEstado().equals("A")) {
                            list.add(temp);
                        }
                    }
                }
                requisitos = list;
            } catch (Exception e) {
                Logger.getLogger(ValidarRequisito.class.getName()).log(Level.SEVERE, null, e);
            }

        }
    }
    
    public String agregarEntrada(){
        String s = "";
        entradas.add(s);
        return s;
    }

    public void comenzarTramiteRedirect(GeTipoTramite tr) {
        try {
            session.setActKey(tr.getActivitykey());
            JsfUti.redirectFaces(tr.getFaceletInicial());
        } catch (Exception e) {
            Logger.getLogger(ValidarRequisito.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<GeTipoTramite> getTramites() {
        return tramites;
    }

    public void setTramites(List<GeTipoTramite> tramites) {
        this.tramites = tramites;

    }

    public GeTipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(GeTipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public List<GeRequisitosTramite> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<GeRequisitosTramite> requisitos) {
        this.requisitos = requisitos;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

}
