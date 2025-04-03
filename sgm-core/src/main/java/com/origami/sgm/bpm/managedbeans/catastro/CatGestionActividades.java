/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class CatGestionActividades extends BpmManageBeanBaseRoot implements Serializable {

    private static final long serialVersionUID = 1L;
    private GeTipoTramite tipo;
    private HistoricoTramites tramite;
    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UserSession sess;
    private AclUser responsable;
    private List<AclUser> responsables;
    private Boolean hab = false;
    private HashMap<String, Object> params;

    @PostConstruct
    protected void load() {
        try {
            if (sess != null) {
                tipo = (GeTipoTramite) manager.find(Querys.getTipoTramitexAbreviatura, new String[]{"abreviatura"}, new Object[]{"CAT"});
                if (tipo != null) {
                    tramite = new HistoricoTramites();
                    responsables = new ArrayList<>();
                    params = new HashMap();
                    AclUser dir = manager.find(AclUser.class, sess.getUserId());
                    if (dir.getEnte() != null && dir.getSisEnabled()) {
                        tramite.setSolicitante(dir.getEnte());
                        hab = true;
                        for (AclRol r : tipo.getDepartamento().getAclRolCollection()) {
                            for (AclUser u : r.getAclUserCollection()) {
                                if (u.getSisEnabled() && u.getEnte() != null) {
                                    if (!responsables.contains(u)) {
                                        responsables.add(u);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    hab = false;
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CatGestionActividades.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void crearActividad() {
        try {
            if (tramite.getSubTipoTramite() != null && responsable != null) {

            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CatGestionActividades.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public Boolean getHab() {
        return hab;
    }

    public void setHab(Boolean hab) {
        this.hab = hab;
    }

    public AclUser getResponsable() {
        return responsable;
    }

    public void setResponsable(AclUser responsable) {
        this.responsable = responsable;
    }

    public List<AclUser> getResponsables() {
        return responsables;
    }

    public void setResponsables(List<AclUser> responsables) {
        this.responsables = responsables;
    }

}
