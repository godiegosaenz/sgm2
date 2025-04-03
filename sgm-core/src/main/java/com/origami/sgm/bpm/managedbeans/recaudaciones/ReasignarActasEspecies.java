/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.recaudaciones;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.RecActasEspeciesDet;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class ReasignarActasEspecies implements Serializable {

    public static final Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(ReasignarActasEspecies.class.getName());

    @Inject
    protected UserSession us;
    @Inject
    protected ServletSession ss;
    @javax.inject.Inject
    private RecaudacionesService rec;

    protected List<RecActasEspeciesDet> detalles;
    protected List<RecActasEspeciesDet> seleccionados = new ArrayList<>();
    protected List<AclUser> recaudadores;
    protected AclUser recSel;
    protected String respuesta;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            detalles = rec.getActasEspeciesActivas();
            recaudadores = rec.getUsuariosByRolId(73L); // 73 ID ROL ASISTENTE_RECAUDADOR
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void reasignarEspecies() {
        try {
            if (recSel != null) {
                if (!seleccionados.isEmpty()) {
                    BigDecimal valor = BigDecimal.ZERO;
                    for (RecActasEspeciesDet es : seleccionados) {
                        valor = valor.add(es.getValorUni().multiply(BigDecimal.valueOf(es.getDisponibles())));
                    }
                    respuesta = rec.reAsignarActaEspecies(seleccionados, recSel.getId(), us.getName_user(), valor);
                    JsfUti.messageInfo(null, "Mensaje", "Re-Asignacion con exito, en el acta: " + respuesta);
                    detalles = rec.getActasEspeciesActivas();
                    recSel = null;
                    JsfUti.update("mainForm");
                } else {
                    JsfUti.messageError(null, "Debe seleccionar las especies disponibles para la re-asignacion.", "");
                }
            } else {
                JsfUti.messageError(null, "Debe seleccionar el usuario al que va a resignar las especies.", "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, "ERROR", Messages.error);
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public String getNameUser(Long id) {
        return rec.getNameUserAssigne(id, Boolean.FALSE);
    }

    public List<RecActasEspeciesDet> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<RecActasEspeciesDet> detalles) {
        this.detalles = detalles;
    }

    public List<RecActasEspeciesDet> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(List<RecActasEspeciesDet> seleccionados) {
        this.seleccionados = seleccionados;
    }

    public List<AclUser> getRecaudadores() {
        return recaudadores;
    }

    public void setRecaudadores(List<AclUser> recaudadores) {
        this.recaudadores = recaudadores;
    }

    public AclUser getRecSel() {
        return recSel;
    }

    public void setRecSel(AclUser recSel) {
        this.recSel = recSel;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public UserSession getUs() {
        return us;
    }

    public void setUs(UserSession us) {
        this.us = us;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

}
