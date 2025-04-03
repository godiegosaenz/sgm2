/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas.liquidaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.RenValoresPlusvalia;
import com.origami.sgm.lazymodels.LiquidacionesLazy;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 * @Date 05/08/2016
 */
@Named
@ViewScoped
public class LiquidacionesPropiedad implements Serializable {

    private static final Logger LOG = Logger.getLogger(LiquidacionesPropiedad.class.getName());

    private LiquidacionesLazy lazy;
    private LiquidacionesLazy antiguas;
    private LiquidacionesLazy inactivas;
    private RenLiquidacion liquidacion;
    private RenValoresPlusvalia valores;
    private Boolean modificar;
    private List<RenLiquidacion> liquidaciones;

    @Inject
    ServletSession ss;

    @Inject
    UserSession session;
    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private RentasServices services;

    @PostConstruct
    protected void initView() {
        lazy = new LiquidacionesLazy(2, false);
        antiguas = new LiquidacionesLazy(2, true);
        inactivas = new LiquidacionesLazy(2, 3L);
    }

    public void detalle(RenLiquidacion liquidacion) {
        try {
            this.liquidacion = liquidacion;
            this.valores = liquidacion.getRenValoresPlusvalia();
            JsfUti.executeJS("PF('detalle').show()");
            JsfUti.update("detalle");
            JsfUti.update("detalle:tabDetalle");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void imprimir(RenLiquidacion liquidacion) {
        try {
            String path = Faces.getRealPath("//");
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.agregarParametro("LOGO", path.concat(SisVars.logoReportes));
            ss.agregarParametro("LIQUIDACION", liquidacion.getId());
            ss.setNombreSubCarpeta("rentas/liquidaciones");
            switch (liquidacion.getTipoLiquidacion().getCodigoTituloReporte().intValue()) {
                case 93:
                    ss.setNombreReporte("alcabalas");
                    break;
                default:
                    ss.setNombreReporte("plusvalia");
                    break;
            }
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void anular() {
        if (liquidacion != null) {
            liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(3L));
            manager.persist(liquidacion);
            JsfUti.messageInfo(null, "Informacion", "Liquidacion anulada correctamente.");
        } else {
            JsfUti.messageError(null, "Error", "Liquidacion es nula vuelva a interlo.");
        }
    }

    public void anular(RenLiquidacion liquidacion) {
        if (liquidacion != null) {
            liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(3L));
            manager.persist(liquidacion);
            JsfUti.messageInfo(null, "Informacion", "Liquidacion anulada correctamente.");
        } else {
            JsfUti.messageError(null, "Error", "Liquidacion es nula vuelva a interlo.");
        }
    }

    public void activar(RenLiquidacion liquidacion) {
        if (liquidacion != null) {
            liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
            manager.persist(liquidacion);
            JsfUti.messageInfo(null, "Informacion", "Liquidacion actulizada correctamente.");
        } else {
            JsfUti.messageError(null, "Error", "Liquidacion es nula vuelva a interlo.");
        }
    }

    public void activar() {
        if (liquidacion != null) {
            liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
            manager.persist(liquidacion);
            JsfUti.messageInfo(null, "Informacion", "Liquidacion actulizada correctamente.");
        } else {
            JsfUti.messageError(null, "Error", "Liquidacion es nula vuelva a interlo.");
        }
    }

    public Boolean habilitar() {
        return session.getRoles().contains(98L) || session.getRoles().contains(189L) || session.getRoles().contains(9L);
    }

    public void modificar() {
        if (liquidacion != null) {
            if (modificar) {
                manager.persist(liquidacion);
                if (liquidacion.getRenPagoCollection() != null && liquidacion.getRenPagoCollection().size() > 0) {
                    for (RenPago renPago : liquidacion.getRenPagoCollection()) {
                        if (renPago.getEstado()) {
                            renPago.setContribuyente(liquidacion.getComprador());
                            manager.persist(renPago);
                        }
                    }
                }
                JsfUti.messageInfo(null, "Informacion", "Liquidacion actulizada correctamente.");
            }
        } else {
            JsfUti.messageError(null, "Error", "Liquidacion es nula vuelva a interlo.");
        }
    }

    public void buscarEnte() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "75%");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/dialogEnte", options, null);
    }

    public void inactivarLiquidaciones() {
        if (liquidaciones != null) {
            if (services.inactivarLiquidaciones(liquidaciones)) {
                JsfUti.messageInfo(null, "Informacion", "Registros inactivos correctamente.");
            } else {
                JsfUti.messageError(null, "Error", "Ocurrio un error.");
            }
        } else {
            JsfUti.messageError(null, "Error", "No ha seleccionado ningun registro.");
        }
    }

    public void seleccionarComprador(SelectEvent event) {
        liquidacion.setComprador((CatEnte) event.getObject());
        modificar = true;
    }

    public void seleccionarVendedor(SelectEvent event) {
        liquidacion.setVendedor((CatEnte) event.getObject());
        modificar = true;
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

    public RenValoresPlusvalia getValores() {
        return valores;
    }

    public void setValores(RenValoresPlusvalia valores) {
        this.valores = valores;
    }

    public LiquidacionesLazy getAntiguas() {
        return antiguas;
    }

    public void setAntiguas(LiquidacionesLazy antiguas) {
        this.antiguas = antiguas;
    }

    public LiquidacionesLazy getInactivas() {
        return inactivas;
    }

    public void setInactivas(LiquidacionesLazy inactivas) {
        this.inactivas = inactivas;
    }

    /**
     * Creates a new instance of LiquidacionesPropiedad
     */
    public LiquidacionesPropiedad() {
    }

    public Boolean getModificar() {
        return modificar;
    }

    public void setModificar(Boolean modificar) {
        this.modificar = modificar;
    }

    public List<RenLiquidacion> getLiquidaciones() {
        return liquidaciones;
    }

    public void setLiquidaciones(List<RenLiquidacion> liquidaciones) {
        this.liquidaciones = liquidaciones;
    }

}
