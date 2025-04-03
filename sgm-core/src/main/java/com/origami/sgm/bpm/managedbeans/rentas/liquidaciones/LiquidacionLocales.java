/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas.liquidaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.RenActivosLocalComercial;
import com.origami.sgm.entities.RenBalanceLocalComercial;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.RenParametrosInteresMulta;
import com.origami.sgm.lazymodels.LiquidacionesLazy;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 * @Date 05/08/2016
 */
@Named
@ViewScoped
public class LiquidacionLocales implements Serializable {

    private static final Logger LOG = Logger.getLogger(LiquidacionLocales.class.getName());

    private LiquidacionesLazy lazy;
    private RenLiquidacion liquidacion;
    private RenLocalComercial local;
    private RenActivosLocalComercial activosLocal;
    private RenBalanceLocalComercial balance;
    private RenPago pago;

    @Inject
    ServletSession ss;

    @Inject
    UserSession session;

    @javax.inject.Inject
    RentasServices services;
    @javax.inject.Inject
    RecaudacionesService recaudacion;
    @javax.inject.Inject
    private Entitymanager manager;

    @PostConstruct
    protected void initView() {
        try {
            if (!ss.estaVacio()) {
                local = (RenLocalComercial) ss.retornarValor("localComercial");
            }
               System.out.println("");
            if (local == null) {
                lazy = new LiquidacionesLazy(1);
            } else {
                lazy = new LiquidacionesLazy(1, local);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "initView()", e);
        }
    }

    public void detalle(RenLiquidacion liquidacion) {
        try {
            this.liquidacion = liquidacion;
            this.local = services.getLocalById(liquidacion);
            if (liquidacion.getRenPagoCollection() != null) {
                pago = new RenPago();
                BigDecimal total = BigDecimal.ZERO;
                for (RenPago p : liquidacion.getRenPagoCollection()) {
                    if (p.getEstado()) {
                        pago.setNumComprobante(p.getNumComprobante());
                        total = total.add(p.getValor());
                        pago.setObservacion(p.getObservacion());
                    }
                }
                pago.setValor(total);
            }
            if (this.local != null) {
                this.activosLocal = services.getActivosLocal(liquidacion);
                this.balance = services.getBalanceLocal(liquidacion);
            } else {
                JsfUti.messageInfo(null, "Informacion", "Liquidacion no tiene un local ingresado.");
//                return;
            }
            calcularInteres();
            JsfUti.executeJS("PF('detalle').show()");
            JsfUti.update("detalle");
            JsfUti.update("detalle:tabDetalle");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void imprimir(RenLiquidacion liquidacion) {
//        System.out.print(liquidacion.getTipoLiquidacion().getCodigoTituloReporte());
//        System.out.println(liquidacion.getTipoLiquidacion().getNombreTitulo());
        String path = Faces.getRealPath("//");
        ss.instanciarParametros();
        ss.setTieneDatasource(Boolean.TRUE);
        ss.agregarParametro("LOGO", path.concat(SisVars.logoReportes));
        ss.agregarParametro("LIQUIDACION", liquidacion.getId());
        ss.setNombreSubCarpeta("rentas");
        switch (liquidacion.getTipoLiquidacion().getCodigoTituloReporte().intValue()) {
            case 11:
                ss.setNombreReporte("activosTotales");
                break;
            case 14:
                ss.setNombreReporte("patenteAnual");
                break;
            case 15:
                ss.setNombreReporte("tasaHabilitacion");
                break;
            case 98:
                ss.setNombreReporte("turismo");
                break;
            case 206:
                ss.setNombreReporte("vallasPublicitarias");
                break;
            default:
                return;
        }
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void imprimirDetalle(RenLiquidacion liquidacion) {
        String path = Faces.getRealPath("//");
        ss.instanciarParametros();
        ss.setTieneDatasource(Boolean.TRUE);
        ss.agregarParametro("LOGO", path.concat(SisVars.logoReportes));
        ss.agregarParametro("LIQUIDACION", liquidacion.getId());
        ss.setNombreSubCarpeta("rentas");
        switch (liquidacion.getTipoLiquidacion().getCodigoTituloReporte().intValue()) {
            case 11:
                ss.setNombreReporte("detalleActivosTotales");
                break;
            case 14:
                ss.setNombreReporte("detallePatente");
                break;
            case 15:
                ss.setNombreReporte("detalleTasaHabilitacion");
                break;
            default:
                return;
        }
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void calcularInteres() {
        try {
            if (liquidacion != null) {
                List<RenParametrosInteresMulta> parametrosInteresMultas = recaudacion.getListParametrosInteresMulta(liquidacion);
                System.out.println(parametrosInteresMultas == null);
                if (parametrosInteresMultas != null && !parametrosInteresMultas.isEmpty()) {//VERIFICAR SI EMITE MULTA-INTERES
                    for (RenParametrosInteresMulta interesMulta : parametrosInteresMultas) {
                        if (interesMulta.getTipo().equalsIgnoreCase("I")) {
                            Calendar fecha = Calendar.getInstance();
                            fecha.set(Calendar.DAY_OF_MONTH, interesMulta.getDia().intValue());
                            fecha.set(Calendar.MONTH, interesMulta.getMes().intValue() - 1);
                            fecha.set(Calendar.YEAR, liquidacion.getAnio());
                            liquidacion.setInteres(recaudacion.generarInteres(liquidacion.getSaldo(), liquidacion.getAnio()));
                        }
                        if (interesMulta.getTipo().equalsIgnoreCase("M")) {
                            liquidacion.setRecargo(recaudacion.generarMultas(liquidacion, interesMulta));
                        }
                    }
                }
                liquidacion.calcularPago();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Calcular Multa", e);
        }
    }

    public void anular() {
        if (liquidacion != null) {
            liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(3L));
            manager.persist(liquidacion);
            JsfUti.messageInfo(null, "Informacion", "Liquidacion anulada correctamente.");
        }else{
            JsfUti.messageError(null, "Error", "Liquidacion es nula vuelva a interlo.");
        }
    }

    public LiquidacionesLazy getLazy() {
        return lazy;
    }

    public void setLazy(LiquidacionesLazy lazy) {
        this.lazy = lazy;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public RenLocalComercial getLocal() {
        return local;
    }

    public void setLocal(RenLocalComercial local) {
        this.local = local;
    }

    public RenActivosLocalComercial getActivosLocal() {
        return activosLocal;
    }

    public void setActivosLocal(RenActivosLocalComercial activosLocal) {
        this.activosLocal = activosLocal;
    }

    public RenBalanceLocalComercial getBalance() {
        return balance;
    }

    public void setBalance(RenBalanceLocalComercial balance) {
        this.balance = balance;
    }

    public RenPago getPago() {
        return pago;
    }

    public void setPago(RenPago pago) {
        this.pago = pago;
    }

    /**
     * Creates a new instance of LiquidacionLocales
     */
    public LiquidacionLocales() {
    }

}
