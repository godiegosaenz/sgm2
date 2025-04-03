/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.managedbeans.ordenes;
import com.origami.censocat.models.ResumenOrdenes;
import com.origami.censocat.querys.Querys;
import com.origami.session.UserSession;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import util.Faces;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class ResumenGeneral implements Serializable {

    private List<ResumenOrdenes> resumen, resumenUsrs;
    
    @Inject
    private UserSession sess;

    @PostConstruct
    protected void load() {
        if (sess != null) {
            //resumen = ots.getResumenGeneral(Querys.getResumenGralOts, 1);
            //resumenUsrs = ots.getResumenGeneral(Querys.getResumenUsuarios(null), 3);
        }
    }

    public void verOrden(Long numOrden) {
        sess.setVarTemp(numOrden.toString());
        Faces.redirectFacesNewTab("/faces/ordenes/revision.xhtml");
    }

    public List<ResumenOrdenes> getResumen() {
        return resumen;
    }

    public void setResumen(List<ResumenOrdenes> resumen) {
        this.resumen = resumen;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public List<ResumenOrdenes> getResumenUsrs() {
        return resumenUsrs;
    }

    public void setResumenUsrs(List<ResumenOrdenes> resumenUsrs) {
        this.resumenUsrs = resumenUsrs;
    }

}
