/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.normasConstruccion;

import com.origami.session.ServletSession;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatSolicitudNormaConstruccion;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;

/**
 *
 * @author origami
 */
@Named
@ViewScoped
public class SolicitudNCEdicion implements Serializable{
    @Inject
    private ServletSession ss;
    
    @javax.inject.Inject
    private Entitymanager service;
    
    protected CatSolicitudNormaConstruccion solicitudNC;
    protected List<CatCiudadela> ciudadelaList;
    
    @PostConstruct
    public void initView() {
        try {
            if (ss.getParametros() != null && ss.getParametros().get("idSolicitudNorma") != null) {
                solicitudNC=(CatSolicitudNormaConstruccion)service.find(CatSolicitudNormaConstruccion.class, Long.parseLong(ss.getParametros().get("idSolicitudNorma").toString()));
                ciudadelaList = service.findAllOrdEntCopy(CatCiudadela.class, new String[]{"nombre"}, new Boolean[]{true});
            }else{
                Faces.redirectFaces("/faces/vistaprocesos/edificaciones/normasConstruccion/solicitudNCConsulta.xhtml");
            }
        } catch (Exception e) {
            Logger.getLogger(SolicitudNCEdicion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public CatSolicitudNormaConstruccion getSolicitudNC() {
        return solicitudNC;
    }

    public void setSolicitudNC(CatSolicitudNormaConstruccion solicitudNC) {
        this.solicitudNC = solicitudNC;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public List<CatCiudadela> getCiudadelaList() {
        return ciudadelaList;
    }

    public void setCiudadelaList(List<CatCiudadela> ciudadelaList) {
        this.ciudadelaList = ciudadelaList;
    }
    
}
