/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.titulosPrediales;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.managedbeans.BusquedaPredios;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.JsfUti;
import util.MessagesRentas;

/**
 *
 * @author Henry
 */
@Named(value = "ingresoTituloPredial")
@ViewScoped
public class IngresoTituloPredial extends BusquedaPredios implements Serializable {

    @Inject
    private Entitymanager manager;
    @Inject
    private SeqGenMan seq;
    @Inject
    private UserSession session;
    @javax.inject.Inject
    private RecaudacionesService recaudacion;

    protected Integer tipoCons = 3;
    protected List<RenRubrosLiquidacion> rubros;
    protected RenLiquidacion liquidacion;
    protected BigDecimal total = new BigDecimal("0.00");
    protected List<RenLiquidacion> liquidaciones;

//    protected List<RenEmisionRubros> rubrosEmision;
    private CatPredioPropietario propietario;
    private BigDecimal cemParquesPlazas;
    private BigDecimal cemAlcantarillado;
    private BigDecimal avaluoSolar;
    private BigDecimal avaluoConstruccion;
    private BigDecimal avaluoMunicipal;

    @PostConstruct
    public void initView() {
        try {
            predioConsulta = new CatPredio();
            liquidacion = new RenLiquidacion();
            liquidacion.setAnio(Calendar.getInstance().get(Calendar.YEAR));
            cemAlcantarillado = new BigDecimal("0");
            cemParquesPlazas = new BigDecimal("0");
        } catch (Exception e) {
            Logger.getLogger(IngresoTituloPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void consultar() {
        try {
            BigInteger numPredio = predioConsulta.getNumPredio();
            List<CatPredio> p = consultar(tipoCons, predioConsulta);
            if (p != null && !p.isEmpty()) {
                predioConsulta = p.get(0);
                liquidacion.setAvaluoSolar(predioConsulta.getAvaluoSolar());
                liquidacion.setAvaluoConstruccion(predioConsulta.getAvaluoConstruccion());
                liquidacion.setAvaluoMunicipal(predioConsulta.getAvaluoMunicipal());
                avaluoSolar = predioConsulta.getAvaluoSolar();
                avaluoConstruccion = predioConsulta.getAvaluoConstruccion();
                avaluoMunicipal = predioConsulta.getAvaluoMunicipal();
            } else {
                predioConsulta = new CatPredio();
                predioConsulta.setNumPredio(numPredio);
                JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.predioNoEncontrado);
            }
        } catch (Exception e) {
            Logger.getLogger(IngresoTituloPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void validarClaveOtroCanton() {
        try {

        } catch (Exception e) {
            Logger.getLogger(IngresoTituloPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarValores() {
        try {
            if ((this.avaluoConstruccion.add(this.avaluoSolar)).compareTo(avaluoMunicipal) == 0) {
                RenLiquidacion lp = (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{predioConsulta, liquidacion.getAnio(), 13L, 2L});
                RenLiquidacion lc = (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{predioConsulta, liquidacion.getAnio(), 13L, 1L});
                if (lp == null && lc == null && predioConsulta != null) {

                    recaudacion.emisionUrbana(this.predioConsulta.getId(), liquidacion.getAnio().longValue(), session.getUserId(), avaluoSolar, avaluoConstruccion, avaluoMunicipal, cemParquesPlazas, cemAlcantarillado);
                    liquidacion = (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{predioConsulta, liquidacion.getAnio(), 13L, 3L});
                } else if (predioConsulta == null) {
                    JsfUti.messageInfo(null, "Mensaje", "Realice la busqueda del predio");
                } else if (lp != null) {
                    JsfUti.messageInfo(null, "Mensaje", "Emision ya existe para el predio " + predioConsulta.getNumPredio() + " en el año " + liquidacion.getAnio() + ". Liquidacion Pagada");
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Emision ya existe para el predio " + predioConsulta.getNumPredio() + " en el año " + liquidacion.getAnio() + ". Liquidacion Emitida");
                }
            } else {
                JsfUti.messageInfo(null, "Mensaje", "Verifique los valores ingresados");
            }

        } catch (Exception e) {
            Logger.getLogger(IngresoTituloPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String getNombreRubro(Long idRubro) {
        RenRubrosLiquidacion r = manager.find(RenRubrosLiquidacion.class, idRubro);
        return r.getDescripcion();
    }

    public void consultarLiquidaciones() {
        try {
            if (predioConsulta != null && predioConsulta.getId() != null) {
                liquidaciones = manager.findAll(QuerysFinanciero.obtenerLiquidacionesPrediales, new String[]{"predio", "tipoLiquidacion"}, new Object[]{predioConsulta, new RenTipoLiquidacion(13L)});
            }
        } catch (Exception e) {
            Logger.getLogger(IngresoTituloPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void registarTitulo() {
        try {
            if (liquidacion.getId() != null && liquidacion.getEstadoLiquidacion().getId() == 3L) {
                liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                manager.persist(liquidacion);
                JsfUti.messageInfo(null, "Mensaje", "Grabado Exitoso");

            } else {
                JsfUti.messageInfo(null, "Mensaje", "Emision ya existe para el predio " + predioConsulta.getNumPredio() + " en el año " + liquidacion.getAnio());
            }
        } catch (Exception e) {
            Logger.getLogger(IngresoTituloPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void nuevoRegistro() {
        JsfUti.redirectFaces("/vistaprocesos/titulosPrediales/ingresoTituloPredial.xhtml");
    }

    public CatPredioPropietario getPropietario() {
        return propietario;
    }

    public void setPropietario(CatPredioPropietario propietario) {
        try {
            if (propietario != null) {
                this.propietario = propietario;
                setContribuyenteConsulta(propietario.getEnte());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getTipoCons() {
        return tipoCons;
    }

    public void setTipoCons(Integer tipoCons) {
        this.tipoCons = tipoCons;
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public List<RenRubrosLiquidacion> getRubros() {
        return rubros;
    }

    public void setRubros(List<RenRubrosLiquidacion> rubros) {
        this.rubros = rubros;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<RenLiquidacion> getLiquidaciones() {
        return liquidaciones;
    }

    public void setLiquidaciones(List<RenLiquidacion> liquidaciones) {
        this.liquidaciones = liquidaciones;
    }

    public BigDecimal getCemParquesPlazas() {
        return cemParquesPlazas;
    }

    public void setCemParquesPlazas(BigDecimal cemParquesPlazas) {
        this.cemParquesPlazas = cemParquesPlazas;
    }

    public BigDecimal getCemAlcantarillado() {
        return cemAlcantarillado;
    }

    public void setCemAlcantarillado(BigDecimal cemAlcantarillado) {
        this.cemAlcantarillado = cemAlcantarillado;
    }

    public BigDecimal getAvaluoSolar() {
        return avaluoSolar;
    }

    public void setAvaluoSolar(BigDecimal avaluoSolar) {
        this.avaluoSolar = avaluoSolar;
    }

    public BigDecimal getAvaluoConstruccion() {
        return avaluoConstruccion;
    }

    public void setAvaluoConstruccion(BigDecimal avaluoConstruccion) {
        this.avaluoConstruccion = avaluoConstruccion;
    }

    public BigDecimal getAvaluoMunicipal() {
        return avaluoMunicipal;
    }

    public void setAvaluoMunicipal(BigDecimal avaluoMunicipal) {
        this.avaluoMunicipal = avaluoMunicipal;
    }

}
