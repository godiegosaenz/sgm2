/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;
import util.VerCedulaUtils;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class AdministradorEnte implements Serializable {

    public static final Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(AdministradorEnte.class.getName());

    @javax.inject.Inject
    private Entitymanager em;

    @javax.inject.Inject
    private DatoSeguroServices dss;

    @javax.inject.Inject
    private SeqGenMan sg;

    @Inject
    private UserSession us;

    private VerCedulaUtils vcu;
    private CatEnteLazy cel;
    private CatEnte ente = new CatEnte();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            cel = new CatEnteLazy();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void showDlgNewEnte() {
        ente = new CatEnte();
        ente.setExcepcionales(false);
        ente.setEsPersona(true);
        JsfUti.update("formNewEnte");
        JsfUti.executeJS("PF('dlgNewCliente').show();");
    }

    public void showDlgEditEnte(CatEnte catente) {
        ente = catente;
        JsfUti.update("formEditEnte");
        JsfUti.executeJS("PF('dlgEditEnte').show();");
    }

    public void editEnte() {
        try {
            if (ente.getNombres() != null || ente.getApellidos() != null || ente.getRazonSocial() != null) {
                ente.setUserMod(us.getName_user());
                ente.setFechaMod(new Date());
                em.update(ente);
                cel = new CatEnteLazy();
                JsfUti.update("mainForm");
                JsfUti.executeJS("PF('dlgEditEnte').hide();");
            } else {
                JsfUti.messageError(null, "Error", "Los campos de nombres o razon social son abligatorios");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, "MENSAJE DE ERROR", "ERROR DE APLICACION!!!");
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void validacionCedula() {
        try {
            if (ente.getCiRuc() != null) {
                if (this.existeEnte(ente.getCiRuc())) {
                    JsfUti.messageInfo(null, Messages.ciRucExiste, "");
                } else {
                    if (ente.getEsPersona()) {
                        this.buscarDatoSeguro();
                    }
                }
            }
        } catch (Exception e) {
            JsfUti.messageError(null, "MENSAJE DE ERROR", "ERROR DE APLICACION!!!");
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public Boolean existeEnte(String ciruc) {
        CatEnte temp = (CatEnte) em.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{ciruc});
        return temp != null;
    }

    /*
    * SOLO FUNCIONA PARA PERSONAS NATURALES
    */
    public void buscarDatoSeguro() {
        try {
            DatoSeguro ds = dss.getDatos(ente.getCiRuc(), false, 0);
            if (ds != null) {
                ente = dss.llenarEnte(ds, ente, false);
            }
        } catch (Exception e) {
            JsfUti.messageError(null, "MENSAJE DE ERROR", "ERROR DE APLICACION!!!");
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void saveEnte() {
        try {
            if (ente.getNombres() != null || ente.getApellidos() != null || ente.getRazonSocial() != null) {
                if (ente.getCiRuc() == null) {
                    ente.setUserCre(us.getName_user());
                    ente.setFechaCre(new Date());
                    ente.setExcepcionales(true);
                    sg.guardarOActualizarEnte(ente);
                    this.hideDlg();
                } else if (this.existeEnte(ente.getCiRuc())) {
                    JsfUti.messageInfo(null, Messages.ciRucExiste, "");
                } else {
                    vcu = new VerCedulaUtils();
                    ente.setUserCre(us.getName_user());
                    ente.setFechaCre(new Date());
                    if (ente.getEsPersona()) {
                        if (vcu.isCIValida(ente.getCiRuc())) {
                            em.persist(ente);
                            this.hideDlg();
                        } else {
                            JsfUti.messageInfo(null, "Tipo Persona NATURAL", Messages.cedulaCIinvalida);
                        }
                    } else {
                        if (vcu.isRucValido(ente.getCiRuc())) {
                            em.persist(ente);
                            this.hideDlg();
                        } else {
                            JsfUti.messageInfo(null, "Tipo Persona JURIDICA", Messages.cedulaCIinvalida);
                        }
                    }
                }
            } else {
                JsfUti.messageError(null, "Error", "Los campos de nombres o razon social son abligatorios");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, "MENSAJE DE ERROR", "ERROR DE APLICACION!!!");
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void hideDlg() {
        ente = new CatEnte();
        cel = new CatEnteLazy();
        JsfUti.executeJS("PF('dlgNewCliente').hide();");
        JsfUti.update("mainForm");
    }

    public CatEnteLazy getCel() {
        return cel;
    }

    public void setCel(CatEnteLazy cel) {
        this.cel = cel;
    }

    public UserSession getUs() {
        return us;
    }

    public void setUs(UserSession us) {
        this.us = us;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

}
