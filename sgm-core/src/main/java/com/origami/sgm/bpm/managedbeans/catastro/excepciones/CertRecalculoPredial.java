/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro.excepciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.historic.ComparativoEmision;
import com.origami.sgm.lazymodels.ComparativoEmsionLazy;
import com.origami.sgm.predio.models.AvaluosModel;
import com.origami.sgm.services.interfaces.catastro.AvaluosServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import util.Faces;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class CertRecalculoPredial implements Serializable {

    private static final Long serialVersionUID = 1L;
    private BigInteger numPredio;
    private RenLiquidacion liquidacion;
    private ComparativoEmision cpr;
    private static final Logger logx = Logger.getLogger(CertRecalculoPredial.class.getName());
    @Inject
    private ServletSession ss;
    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private AvaluosServices avaluos;
    private String path, emision;
    protected ComparativoEmsionLazy comparativoEmsiones;

    @PostConstruct
    protected void load() {
        if (sess != null) {
            ss.instanciarParametros();
            path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            comparativoEmsiones = new ComparativoEmsionLazy();
        }
    }

    public void calcular() {
        try {
            if (numPredio != null && emision != null) {
                Integer periodo = Integer.parseInt(new SimpleDateFormat("YYYY").format(new Date()));
                liquidacion = (RenLiquidacion) manager.find(QuerysFinanciero.getLiquidacionxAnioPredioTipo, new String[]{"anio", "tipo", "predio"}, new Object[]{periodo, 13L, numPredio});
                if (liquidacion != null) {
                    AvaluosModel vp = (AvaluosModel) avaluos.getLiquidacionesPagadas(liquidacion.getId()).get().get(0);
                    if (vp != null) {
                        cpr = new ComparativoEmision();
                        cpr.setNumPredio(numPredio);
                        cpr.setIdPredio(new BigInteger(liquidacion.getPredio().getId().toString()));
                        cpr.setFecCre(new Date());
                        cpr.setPeriodo(periodo);
                        cpr.setLiquidacion(new BigInteger(liquidacion.getId().toString()));
                        cpr.setContribuyente(liquidacion.getNombreComprador());
                        cpr.setAreaTotal(liquidacion.getAreaTotal());
                        cpr.setNumVersion(vp.getNumVersion());
                        cpr.setAvaluoSolar(liquidacion.getAvaluoSolar());
                        cpr.setAvaluoConstruccion(liquidacion.getAvaluoConstruccion());
                        cpr.setAvaluoMunicipal(liquidacion.getAvaluoMunicipal());
                        cpr.setResolucion(emision);
                        cpr.setAreaCalc(vp.getAreaTotalCalc());
                        cpr.setAvalSolCalc(vp.getAvaluoSolarCalc());
                        cpr.setAvalEdifCalc(vp.getAvaluoEdifCalc());
                        cpr.setAvalMunCalc(vp.getAvaluoMunicipalCalc());

                        cpr.setIpLiq(vp.getIpliq());
                        cpr.setTasaMantLiq(vp.getTasamantliq());
                        cpr.setEmisionLiq(vp.getEmisionliq());
                        cpr.setMejorasLiq(vp.getMejorasliq());
                        cpr.setSolNedifLiq(vp.getSolnedifliq());
                        cpr.setBomberosLiq(vp.getBomberosliq());
                        cpr.setDescuentoLiq(vp.getDescuentoliq());

                        cpr.setIpCalc(vp.getIpCalc());
                        cpr.setTasaMantCalc(vp.getTasaMantCalc());
                        cpr.setEmisionCalc(vp.getEmisionCalc());
                        cpr.setMejorasLiq(vp.getMejorasliq());
                        cpr.setSolNedifCalc(vp.getSolarEdifCalc());
                        cpr.setBomberosCalc(vp.getBomberosCalc());
                        cpr.setDescuentoLiq(vp.getDescuentoliq());

                        cpr.setAprobCatastro(Boolean.TRUE);
                        AclRol catRol = (AclRol) manager.find(Querys.getAclRolByNombre, new String[]{"nombre"}, new Object[]{"director_catastro"});
                        for (AclUser dir : catRol.getAclUserCollection()) {
                            cpr.setDirector(dir.getEnte().getNombres() + " " + dir.getEnte().getApellidos());
                            cpr.setFirmaCatastro(path + dir.getRutaImagen());
                        }
                        ComparativoEmision ce = (ComparativoEmision) manager.persist(cpr);
                        if (ce != null) {
                            this.imprimir(ce);
                        }
                    }
                }
            }
        } catch (InterruptedException | NumberFormatException | ExecutionException e) {
            logx.log(Level.SEVERE, null, e);
        }
    }

    public void imprimir(ComparativoEmision c) {
        try {
            if (c.getId() != null) {
                ss.agregarParametro("firma", cpr.getFirmaCatastro());
                ss.agregarParametro("id", cpr.getId());
                ss.agregarParametro("director", cpr.getDirector());
                ss.setTieneDatasource(Boolean.TRUE);
                ss.agregarParametro("logo", path + SisVars.logoReportes);
                ss.setNombreReporte("/catastro/emisionRecalculo");
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            }
        } catch (Exception e) {
        }
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public ComparativoEmision getCpr() {
        return cpr;
    }

    public void setCpr(ComparativoEmision cpr) {
        this.cpr = cpr;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public ComparativoEmsionLazy getComparativoEmsiones() {
        return comparativoEmsiones;
    }

    public void setComparativoEmsiones(ComparativoEmsionLazy comparativoEmsiones) {
        this.comparativoEmsiones = comparativoEmsiones;
    }

    public String getEmision() {
        return emision;
    }

    public void setEmision(String emision) {
        this.emision = emision;
    }

}
