/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.financiero;

import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.FnRemisionLiquidacion;
import com.origami.sgm.entities.FnRemisionSolicitud;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.financiero.RemisionInteresServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import util.Utils;

/**
 *
 * @author origami-idea
 */
@Stateless(name = "remisionInteresEjb")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class RemisionInteresEjb implements RemisionInteresServices {

    @Inject
    private UserSession us;

    @Inject
    private Entitymanager manager;

    @Override
    public FnRemisionSolicitud saveFnRemisionSolicitudProceso(List<RenLiquidacion> liquidaciones) {
        try {
            liquidaciones = liquidacionesPermitidas(liquidaciones);
        } catch (ParseException ex) {
            Logger.getLogger(RemisionInteresEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
        BigDecimal valorInteres = BigDecimal.ZERO;
        BigDecimal valorRecargo = BigDecimal.ZERO;
        BigDecimal valorMulta = BigDecimal.ZERO;
        BigDecimal valorTotalPago = BigDecimal.ZERO;
        BigDecimal valorTotalRemision = BigDecimal.ZERO;

        FnRemisionSolicitud fnRemisionSolicitud;
        FnRemisionLiquidacion fnRemisionLiquidacion;

        List<FnRemisionLiquidacion> fnRemisionInteresLiquidacions;
        try {

            fnRemisionInteresLiquidacions = new ArrayList<>();
            fnRemisionLiquidacion = null;
            for (RenLiquidacion rl : liquidaciones) {
                fnRemisionLiquidacion = new FnRemisionLiquidacion();
                fnRemisionLiquidacion.setEstado(Boolean.FALSE);
                fnRemisionLiquidacion.setFechaAplicacion(new Date());
                fnRemisionLiquidacion.setUsuarioAplicacion(new AclUser(us.getUserId()));
                fnRemisionLiquidacion.setLiquidacion(rl);
                fnRemisionLiquidacion.setInteres(rl.getInteres());
                fnRemisionLiquidacion.setMultas(BigDecimal.ZERO);
                fnRemisionLiquidacion.setRecargo(rl.getRecargo());
                fnRemisionInteresLiquidacions.add(fnRemisionLiquidacion);
                valorInteres = valorInteres.add(rl.getInteres());
                valorRecargo = valorRecargo.add(rl.getRecargo());
                valorTotalPago = valorTotalPago.add(rl.getTotalPago());
            }
            valorTotalRemision = valorInteres.add(valorRecargo);
            fnRemisionSolicitud = new FnRemisionSolicitud();
            if (liquidaciones.size() > 1) {
                if (liquidaciones.get(liquidaciones.size() - 1).getComprador() != null) {
                    fnRemisionSolicitud.setSolicitante(liquidaciones.get(liquidaciones.size() - 1).getComprador());
                }
            }

            fnRemisionSolicitud.setMultas(valorMulta);
            fnRemisionSolicitud.setInteres(valorInteres);
            fnRemisionSolicitud.setRecargo(valorRecargo);
            fnRemisionSolicitud.setTotalRemision(valorTotalRemision);
            fnRemisionSolicitud.setTotalPago(valorTotalPago);
            fnRemisionSolicitud.setFechaAprobacion(new Date());
            fnRemisionSolicitud.setEstado(new FnEstadoExoneracion(2L));

            Date fechaPagoMaximo = new SimpleDateFormat("dd-MM-yyyy").parse(SisVars.fechaPublicacionOrdenanza);

            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaPagoMaximo);
            cal.add(Calendar.MONTH, 3);

            fechaPagoMaximo = cal.getTime();

            CtlgItem tipoSolicitud = null;

            if (Utils.isNotEmpty(fnRemisionInteresLiquidacions)) {
                RenLiquidacion liquidacion = fnRemisionInteresLiquidacions.get(0).getLiquidacion();
                ///VERIFICA A QUE TIPO SE LE  DARA LA  REMISION :  PREDIO - LIQUIDACION EN GENERAL
                if (liquidacion != null) {//PREDIOS
                    if (liquidacion.getPredio() != null) {
                        tipoSolicitud = (CtlgItem) manager.find(Querys.getCtlgItemByCatalogoCodeName,
                                new String[]{"catalogo", "codename"}, new Object[]{"solicitud.remision_interes", "sap"});
                    } else {
                        tipoSolicitud = (CtlgItem) manager.find(Querys.getCtlgItemByCatalogoCodeName,
                                new String[]{"catalogo", "codename"}, new Object[]{"solicitud.remision_interes", "sal"});
                    }
                }
            }
            fnRemisionSolicitud.setTramiteTipo(tipoSolicitud);
            fnRemisionSolicitud.setFechaIngreso(new Date());
            fnRemisionSolicitud.setUsuarioCreacion(new AclUser(us.getUserId()));
            fnRemisionSolicitud.setFechaPagoMaximo(fechaPagoMaximo);
            fnRemisionSolicitud.setExoneracionTipo(new FnExoneracionTipo(60L));

            fnRemisionSolicitud = (FnRemisionSolicitud) manager.persist(fnRemisionSolicitud);

            for (FnRemisionLiquidacion remisionLiquidacion : fnRemisionInteresLiquidacions) {
                remisionLiquidacion.setExoneracion(fnRemisionSolicitud);
                manager.persist(remisionLiquidacion);
            }
        } catch (Exception e) {
            Logger.getLogger(RemisionInteresEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return fnRemisionSolicitud;

    }

    @Override
    public FnRemisionSolicitud aprobarSolicitud(FnRemisionSolicitud fnRemisionSolicitud) {
        fnRemisionSolicitud.setEstado(new FnEstadoExoneracion(1L));
        BigDecimal valorRemision = BigDecimal.ZERO;
        String obs = "";
        for (FnRemisionLiquidacion remisionLiquidacion : fnRemisionSolicitud.getFnRemisionLiquidacionCollection()) {
            remisionLiquidacion.setEstado(Boolean.TRUE);
            valorRemision = BigDecimal.ZERO;
            obs = "VALOR REMISION DE INTERESES: $";
            valorRemision = remisionLiquidacion.getInteres().add(remisionLiquidacion.getMultas()).add(remisionLiquidacion.getRecargo());
            obs = obs + valorRemision.toString();
            if (remisionLiquidacion.getLiquidacion().getObservacion() != null) {
                remisionLiquidacion.getLiquidacion().setObservacion(remisionLiquidacion.getLiquidacion().getObservacion() + obs);
            } else {
                remisionLiquidacion.getLiquidacion().setObservacion(obs);
            }
            manager.persist(remisionLiquidacion.getLiquidacion());
            manager.persist(remisionLiquidacion);
        }
        return fnRemisionSolicitud = (FnRemisionSolicitud) manager.persist(fnRemisionSolicitud);
    }

    public List<RenLiquidacion> liquidacionesPermitidas(List<RenLiquidacion> renLiquidacions) throws ParseException {
        List<RenLiquidacion> liquidacions = new ArrayList<>();
        Date fechaMaximaRemision = new SimpleDateFormat("dd-MM-yyyy").parse(SisVars.fechaMaximaRemisionInteres);
        Date periodoLectura;
        String periodo = "";

        for (RenLiquidacion rl : renLiquidacions) {
            switch (rl.getTipoLiquidacion().getId().intValue()) {
                case 13: ///LA REMISION DE INTERES E DESDDE ABRIL 2 DEL 2018 LO RECARGOS DE INTERES ...!
                    if (rl.getAnio() <= 2017) {
                        liquidacions.add(rl);
                    }
                    break;
                default:
                    if (rl.getAnio() <= 2017) {
                        liquidacions.add(rl);
                    }
                    break;
            }
        }

        return liquidacions;
    }

    @Override
    public Boolean aplicaRemision(RenLiquidacion liquidacion) {
        Boolean aplicaRemision = Boolean.FALSE;

        FnRemisionLiquidacion fnRemisionLiquidacion = (FnRemisionLiquidacion) manager.find(QuerysFinanciero.getLiquidacionFnRemisionLiquidacion,
                new String[]{"liquidacion"},
                new Object[]{liquidacion.getId()});

        if (fnRemisionLiquidacion != null) {
            if (new Date().before(fnRemisionLiquidacion.getExoneracion().getFechaPagoMaximo())) {
                aplicaRemision = Boolean.TRUE;
            }
        } else {
            aplicaRemision = Boolean.FALSE;
        }

        return aplicaRemision;
    }

    @Override
    public Boolean aplicaRemisionPagoCuotaInicial(List<RenLiquidacion> liquidaciones) {
        Boolean aplicaRemision = Boolean.FALSE;

        for (RenLiquidacion rl : liquidaciones) {
            if (aplicaRemision(rl)) {
                aplicaRemision = Boolean.TRUE;
                break;
            }
        }
        return aplicaRemision;
    }

    @Override
    public FnRemisionSolicitud cancelarSolicitud(FnRemisionSolicitud fnRemisionSolicitud) {
        fnRemisionSolicitud.setEstado(new FnEstadoExoneracion(3L));
        for (FnRemisionLiquidacion remisionLiquidacion : fnRemisionSolicitud.getFnRemisionLiquidacionCollection()) {
            remisionLiquidacion.setEstado(Boolean.FALSE);
            manager.persist(remisionLiquidacion);
        }
        return fnRemisionSolicitud = (FnRemisionSolicitud) manager.persist(fnRemisionSolicitud);
    }

}
