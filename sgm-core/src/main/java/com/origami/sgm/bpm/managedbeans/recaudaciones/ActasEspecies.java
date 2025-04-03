/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.recaudaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.RecActasEspecies;
import com.origami.sgm.entities.RecActasEspeciesDet;
import com.origami.sgm.entities.RecEspecies;
import com.origami.sgm.lazymodels.RecActasEspeciesLazy;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
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
public class ActasEspecies implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Inject
    protected UserSession us;

    @Inject
    protected ServletSession ss;

    @javax.inject.Inject
    private RecaudacionesService rec;
    
    @Inject
    private Entitymanager manager; 

    protected RecActasEspeciesLazy actaslazy;
    protected List<RecActasEspeciesDet> listDetalles;
    protected RecActasEspeciesDet detalle;
    protected List<AclUser> recaudadores;
    protected AclUser recSel;
    protected List<RecEspecies> especies;
    protected RecEspecies espSel;

    protected BigDecimal total = BigDecimal.ZERO;

    protected AclUser sistemaUser;
    private Map<String, Object> paramt;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    public void getUser()  {
        paramt = new HashMap<>();
        paramt.put("departamento", new GeDepartamento(11L));
        paramt.put("isDirector", false);
        AclRol rol = (AclRol) manager.findObjectByParameter(AclRol.class, paramt);
        sistemaUser = new AclUser();
        for (AclUser u : rol.getAclUserCollection()) {
            if (u.getUserIsDirector() && u.getSisEnabled()) {
                sistemaUser = u;
                break;
            }
        }
    }

    protected void iniView() {
        try {
            actaslazy = new RecActasEspeciesLazy(Boolean.TRUE);
            recaudadores = rec.getUsuariosByRolId(73L); // 73 ID ROL ASISTENTE_RECAUDADOR
            especies = rec.getEspeciesActivas();
        } catch (Exception e) {
            Logger.getLogger(ActasEspecies.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String getNombreUsuarioAsignado(RecActasEspecies ac) {
        if (ac.getUsuarioAsignado() != null) {
            return rec.getNameUserAssigne(ac.getUsuarioAsignado(), true).toUpperCase();
        } else {
            return "";
        }
    }

    public void generarActa(RecActasEspecies acta) {
        try {
            if (acta.getId() != null) {
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                ss.instanciarParametros();
                ss.setTieneDatasource(true);
                ss.setNombreReporte("actaEspecies");
                ss.setNombreSubCarpeta("recaudaciones");
                ss.agregarParametro("ID_ACTA", acta.getId());
                ss.agregarParametro("NOMBRECANTON", SisVars.NOMBRECANTON);
                ss.agregarParametro("LOGO", path  + SisVars.logoReportes);
                ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "reportes/recaudaciones/");
                if (acta.getTesorero() != null) {
                    ss.agregarParametro("TESORERO", rec.getNameUserAssigne(acta.getTesorero(), true));
                }
                if (acta.getUsuarioAsignado() != null) {
                    ss.agregarParametro("CAJERO", rec.getNameUserAssigne(acta.getUsuarioAsignado(), true));
                }
                JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
            }
        } catch (Exception e) {
            Logger.getLogger(ActasEspecies.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void actaNueva() {
        listDetalles = new ArrayList<>();
        espSel = null;
        recSel = null;
        total = BigDecimal.ZERO;
        JsfUti.update("formActa");
        JsfUti.executeJS("PF('dlgCrearActa').show()");
    }

    public void showDlgAgregarEspecie() {
        if (recSel != null) {
            if (espSel != null) {
                if (espSel.getRubro() != null) {
                    detalle = new RecActasEspeciesDet();
                    detalle.setEspecie(espSel);
                    detalle.setValorUni(espSel.getRubro().getValor());
                    JsfUti.update("formEspecie");
                    JsfUti.executeJS("PF('dlgIngresoEspecie').show()");
                } else {
                    JsfUti.messageError(null, "La Especie no tiene un rubro asignado.", "");
                }
            } else {
                JsfUti.messageError(null, "Debe seleccionar un tipo de especie", "");
            }
        } else {
            JsfUti.messageError(null, "Debe seleccionar al cajero", "");
        }
    }

    public void agregarEspecie() {
        if (detalle.getValorUni() != null && detalle.getCantidad() != null && detalle.getDesde() != null) {
            if (detalle.getCantidad() > 0 && detalle.getDesde().compareTo(0L) > 0) {
                detalle.setValorTotal(detalle.getValorUni().multiply(BigDecimal.valueOf(detalle.getCantidad())));
                detalle.setDisponibles(detalle.getCantidad());
                detalle.setHasta(detalle.getDesde() + detalle.getCantidad() - 1);
                listDetalles.add(detalle);
                this.calcularTotal();
                JsfUti.update("formActa:dtDetActa");
                JsfUti.executeJS("PF('dlgIngresoEspecie').hide()");
            } else {
                JsfUti.messageError(null, "Cantidad y Valor Desde deben ser mayor a cero.", "");
            }
        } else {
            JsfUti.messageError(null, "Todos los valores son obligatorios.", "");
        }
    }

    public void calcularTotal() {
        total = BigDecimal.ZERO;
        for (RecActasEspeciesDet det : listDetalles) {
            total = total.add(det.getValorTotal());
        }
    }

    public void eliminarDetalle(int indice) {
        listDetalles.remove(indice);
        this.calcularTotal();
    }

    public void guardarActa() {
        try {
            if (!listDetalles.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                RecActasEspecies acta = new RecActasEspecies();
                acta.setAnio(cal.get(Calendar.YEAR));
                acta.setFechaIngreso(cal.getTime());
                acta.setTotal(total);
                acta.setUsuarioIngreso(us.getName_user());
                acta.setUsuarioAsignado(recSel.getId());
                acta.setRecActasEspeciesDetCollection(listDetalles);
                String valor = rec.saveActaEspecies(acta);
                if (valor != null) {
                    actaslazy = new RecActasEspeciesLazy(Boolean.TRUE);
                    listDetalles = new ArrayList<>();
                    recSel = null;
                    espSel = null;
                    total = BigDecimal.ZERO;
                    JsfUti.update("mainForm");
                    JsfUti.executeJS("PF('dlgCrearActa').hide()");
                    JsfUti.messageInfo(null, "Acta No " + valor + ", guardada con exito.", "");
                } else {
                    getUser();
                    JsfUti.messageError(null, Messages.error + sistemaUser.getEnte(), "");
                }
            } else {
                JsfUti.messageError(null, "Lista Vacia", "Debe ingresar las especies para asignar a la caja");
            }
        } catch (Exception e) {
            Logger.getLogger(ActasEspecies.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<RecEspecies> getEspecies() {
        return especies;
    }

    public void setEspecies(List<RecEspecies> especies) {
        this.especies = especies;
    }

    public AclUser getRecSel() {
        return recSel;
    }

    public void setRecSel(AclUser recSel) {
        this.recSel = recSel;
    }

    public RecEspecies getEspSel() {
        return espSel;
    }

    public void setEspSel(RecEspecies espSel) {
        this.espSel = espSel;
    }

    public List<RecActasEspeciesDet> getListDetalles() {
        return listDetalles;
    }

    public void setListDetalles(List<RecActasEspeciesDet> listDetalles) {
        this.listDetalles = listDetalles;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public RecActasEspeciesDet getDetalle() {
        return detalle;
    }

    public void setDetalle(RecActasEspeciesDet detalle) {
        this.detalle = detalle;
    }

    public RecActasEspeciesLazy getActaslazy() {
        return actaslazy;
    }

    public void setActaslazy(RecActasEspeciesLazy actaslazy) {
        this.actaslazy = actaslazy;
    }

    public List<AclUser> getRecaudadores() {
        return recaudadores;
    }

    public void setRecaudadores(List<AclUser> recaudadores) {
        this.recaudadores = recaudadores;
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

    public AclUser getSistemaUser() {
        return sistemaUser;
    }

    public void setSistemaUser(AclUser sistemaUser) {
        this.sistemaUser = sistemaUser;
    }

}
