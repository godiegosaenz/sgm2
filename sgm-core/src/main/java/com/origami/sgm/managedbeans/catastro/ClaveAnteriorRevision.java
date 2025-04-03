/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.session.UserSession;
import com.origami.sgm.entities.ClavesAnterioresRevision;
import com.origami.sgm.lazymodels.ClaveAnteriorRevisionLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author root
 */
@Named(value = "claveAnteriorRevision")
@ViewScoped
public class ClaveAnteriorRevision implements Serializable {

    @Inject
    private Entitymanager manager;
    @Inject
    private UserSession session;

    private ClavesAnterioresRevision anterioresRevision;
    private ClaveAnteriorRevisionLazy claveAnteriorRevisionLazy;

    @PostConstruct
    protected void init() {
        claveAnteriorRevisionLazy = new ClaveAnteriorRevisionLazy();
        anterioresRevision = new ClavesAnterioresRevision();
    }

    public void saveRevision(ClavesAnterioresRevision anterioresRevision) {

        if (anterioresRevision.getId() != null) {
            manager.persist(anterioresRevision);
            Faces.messageInfo(null, "Mensaje", "Datos Grabados Correctamente.");
        } else {
            if (anterioresRevision.getClave() == null) {
                Faces.messageInfo(null, "Mensaje", "Debe Ingresar la clave catastral anterior");
                
            } else {
                if (anterioresRevision.getClave().length() < 18) {
                    Faces.messageInfo(null, "Mensaje", "La Clave Catastral Anterior debe ser igual a 18 digitos");
                } else {
                    anterioresRevision.setFechaIngreso(new Date());
                    anterioresRevision.setRevisada(Boolean.FALSE);
                    manager.persist(anterioresRevision);
                    Faces.messageInfo(null, "Mensaje", "Datos Grabados Correctamente.");
                    JsfUti.executeJS("PF('dlgClaveAnterior').hide();");
                }

            }

        }

    }

    public ClaveAnteriorRevisionLazy getClaveAnteriorRevisionLazy() {
        return claveAnteriorRevisionLazy;
    }

    public void setClaveAnteriorRevisionLazy(ClaveAnteriorRevisionLazy claveAnteriorRevisionLazy) {
        this.claveAnteriorRevisionLazy = claveAnteriorRevisionLazy;
    }

    public ClavesAnterioresRevision getAnterioresRevision() {
        return anterioresRevision;
    }

    public void setAnterioresRevision(ClavesAnterioresRevision anterioresRevision) {
        this.anterioresRevision = anterioresRevision;
    }

}
