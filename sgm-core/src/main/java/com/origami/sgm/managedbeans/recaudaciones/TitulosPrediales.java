/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.recaudaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.RubrosPorTipoLiq;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.FnSolicitudExoneracionPredios;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.lazymodels.RenLiquidacionesLazy;
import com.origami.sgm.lazymodels.RenLiquidacionesTitulosPredialesLazy;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author root
 */
@Named(value = "titulosPrediales")
@ViewScoped
public class TitulosPrediales implements Serializable {

    @Inject
    private Entitymanager manager;

    @Inject
    private UserSession uSession;

    @Inject
    private UserSession session;

    @Inject
    private ServletSession ss;

    @javax.inject.Inject
    protected RentasServices servicesRentas;

    @javax.inject.Inject
    private RecaudacionesService recaudacion;

    private BigDecimal cemParquesPlazas;
    private BigDecimal cemAlcantarillado;

    private List<RenLiquidacion> liquidacionseleccionadas;
    private RenLiquidacionesTitulosPredialesLazy liquidacionesLazy;
    private RenLiquidacion original, posterior, cemLiquidacion;
    private List<FnExoneracionLiquidacion> exoneraciones;
    private List<RubrosPorTipoLiq> detList1, detList2;
    private FnExoneracionLiquidacion exoneracion;
    private List<RenDetLiquidacion> detLiq;
    private String observacion;

    @PostConstruct
    public void initView() {
        cemParquesPlazas = BigDecimal.ZERO;
        cemAlcantarillado = BigDecimal.ZERO;
        liquidacionesLazy = new RenLiquidacionesTitulosPredialesLazy();
    }

    public void openDialog() {
        if (liquidacionseleccionadas.isEmpty()) {
            JsfUti.messageInfo(null, "Información", "Debe seleccionar emisiones");
            return;
        } else {
            JsfUti.executeJS("PF('dlgAnular').show()");
        }
    }

    public void generarComprobante(RenLiquidacion liquidacion) {

        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.borrarParametros();
        ss.instanciarParametros();
        ss.setNombreSubCarpeta("Emision");
        ss.agregarParametro("LOGO", path + SisVars.logoReportes);
        ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/").concat("/Emision/"));
        ss.setNombreReporte("emisionPredioUrbanoAnulado");
        ss.setTieneDatasource(Boolean.TRUE);

        ss.setTieneDatasource(Boolean.TRUE);
        ss.agregarParametro("ID_LIQUIDACION", liquidacion.getId());
        JsfUti.redirectNewTab("/sgmEE/Documento");

    }

    public void openDialogCem(RenLiquidacion liquidacion) {
        this.cemLiquidacion = liquidacion;
        JsfUti.executeJS("PF('dlgAddCem').show()");
        JsfUti.update("frmCEMValor");
    }

    public void addCem() {
        BigDecimal totalPago = this.cemLiquidacion.getTotalPago();
        for (RenDetLiquidacion dt : cemLiquidacion.getRenDetLiquidacionCollection()) {
            if (dt.getRubro() == 640L && !cemParquesPlazas.equals(BigDecimal.ZERO)) {
                JsfUti.messageError(null, "Información", "Predio Ya posee este Rubro PARQUES Y PLAZAS");
                JsfUti.executeJS("PF('dlgAddCem').hide()");
                return;
            }
            if (dt.getRubro() == 641L && !cemAlcantarillado.equals(BigDecimal.ZERO)) {
                JsfUti.messageError(null, "Información", "Predio Ya posee este Rubro ALCANTARILLADO");
                JsfUti.executeJS("PF('dlgAddCem').hide()");
                return;
            }
        }

        System.out.println("cemAlcantarillado " + this.cemAlcantarillado);
        System.out.println("cemParquesPlazas " + this.cemParquesPlazas);

        this.cemLiquidacion.setTotalPago(totalPago.add(this.cemAlcantarillado).add(this.cemParquesPlazas));
        this.cemLiquidacion.setSaldo(totalPago.add(this.cemAlcantarillado).add(this.cemParquesPlazas));
        System.out.println("totalPago" + totalPago);
        manager.persist(this.cemLiquidacion);
        RenDetLiquidacion detLiquidacion = new RenDetLiquidacion(cemParquesPlazas, this.cemLiquidacion, 640L);
        manager.persist(detLiquidacion);
        detLiquidacion = new RenDetLiquidacion(cemAlcantarillado, this.cemLiquidacion, 641L);
        manager.persist(detLiquidacion);

        JsfUti.executeJS("PF('dlgAddCem').hide()");

    }

    public void anularLiquidacion() {
        if (observacion == null || observacion.equals("")) {
            JsfUti.messageInfo(null, "Información", "Las observaciones son obligatorias");
            return;
        }
        Boolean prediosDiferentes = Boolean.FALSE;
        ///SE ORDERNA LA ,LISTA PARA OBTENER EL MENOR DE LOS ANIOS
        Collections.sort(liquidacionseleccionadas, (RenLiquidacion rl1, RenLiquidacion rl2)
                -> new Integer(rl1.getAnio())
                        .compareTo(rl2.getAnio()));
        CatPredio predio = null;
        List<RenLiquidacion> liquidacionesTemp = new ArrayList();
        List<CatPredio> prediosUrbanos = new ArrayList();
        FnSolicitudExoneracionPredios exoneracionPredios = null;
        List<FnSolicitudExoneracionPredios> prediosEnSolicitud = new ArrayList<>();
        for (RenLiquidacion rl : liquidacionseleccionadas) {
            exoneracionPredios = new FnSolicitudExoneracionPredios();
            exoneracionPredios.setPredio(rl.getPredio());
            prediosUrbanos.add(rl.getPredio());
            if (!prediosUrbanos.isEmpty()) {
                for (CatPredio cp : prediosUrbanos) {
                    if (!rl.getPredio().getId().equals(cp.getId())) {
                        prediosDiferentes = Boolean.TRUE;
                        break;
                    }
                }
            }

            rl.setObservacion(observacion);
            prediosEnSolicitud.add(exoneracionPredios);
            liquidacionesTemp.add(rl);
        }
        if (prediosDiferentes) {
            JsfUti.messageInfo(null, "Información", "Las anulaciones deben de ser un mismo predio");
            return;

        }
        FnExoneracionTipo tipo = manager.find(FnExoneracionTipo.class,
                40L);
        List<RenPago> pagos;
        if (!prediosUrbanos.isEmpty()) {
            predio = prediosUrbanos.get(0);
            if (tipo.getId() != 40L) {
                for (CatPredio pu : prediosUrbanos) {
                    pagos = recaudacion.getPagosByPredioTipoLiquidacionAnio(pu, null, new RenTipoLiquidacion(13L),
                            liquidacionseleccionadas.get(0).getAnio(), liquidacionseleccionadas.get(liquidacionseleccionadas.size() - 1).getAnio());
                    if (pagos != null && !pagos.isEmpty()) {
                        JsfUti.messageInfo(null, "Información", "Solo procede las Emisiones Pendientes de Pago");
                        return;
                    }
                }
            } else {
                for (CatPredio pu : prediosUrbanos) {
                    pagos = recaudacion.getPagosByPredioTipoLiquidacionAnioPagada(pu, null, new RenTipoLiquidacion(13L),
                            liquidacionseleccionadas.get(0).getAnio(), liquidacionseleccionadas.get(liquidacionseleccionadas.size() - 1).getAnio());
                    if (pagos != null && !pagos.isEmpty()) {
                        JsfUti.messageInfo(null, "Información", "Solo procede las Emisiones Pendientes de Pago");
                        return;
                    }
                }
            }
        }

        for (RenLiquidacion r : liquidacionesTemp) {
            manager.persist(r);
        }
        FnSolicitudExoneracion solicitud = new FnSolicitudExoneracion();
        solicitud.setExoneracionTipo(tipo);
        solicitud.setFechaIngreso(new Date());
        solicitud.setUsuarioCreacion(uSession.getName_user());
        solicitud.setSolicitante(liquidacionseleccionadas.get(liquidacionseleccionadas.size() - 1).getComprador());
        solicitud.setPredio(liquidacionseleccionadas.get(liquidacionseleccionadas.size() - 1).getPredio());
        solicitud.setAnioInicio(liquidacionseleccionadas.get(0).getAnio());
        solicitud.setAnioFin(liquidacionseleccionadas.get(liquidacionseleccionadas.size() - 1).getAnio());
        System.out.println("solicitud " + solicitud.getAnioInicio() + "FIN " + solicitud.getAnioFin());
        solicitud.setEstado(new FnEstadoExoneracion(2L));
        solicitud = (FnSolicitudExoneracion) manager.persist(solicitud);

        servicesRentas.registarDatoSolicitudExoneracion(solicitud, null, prediosEnSolicitud);
        exoneraciones = servicesRentas.aplicarExoneracion(null, solicitud, uSession.getName_user());

        if (exoneraciones != null && !exoneraciones.isEmpty()) {
            detList1 = new ArrayList();
            detList2 = new ArrayList();
            RenRubrosLiquidacion rubro;
            exoneracion = exoneraciones.get(0);
            if (exoneracion.getLiquidacionOriginal() != null) {
                original = exoneracion.getLiquidacionOriginal();
                detLiq = manager.findAll(QuerysFinanciero.getDetalleDeLiquidacion, new String[]{"liquidacion"}, new Object[]{original});

                for (RenDetLiquidacion temp : detLiq) {
                    rubro = (RenRubrosLiquidacion) manager.find(RenRubrosLiquidacion.class,
                            temp.getRubro());
                    detList1.add(new RubrosPorTipoLiq(rubro.getDescripcion(), temp.getValor()));
                }
            }
            if (exoneracion.getLiquidacionPosterior() != null) {
                posterior = exoneracion.getLiquidacionPosterior();
                posterior.setTotalPago(posterior.getTotalPago().setScale(2, RoundingMode.HALF_UP));
                posterior.setUsuarioIngreso(uSession.getName_user());
                posterior = (RenLiquidacion) manager.persist(posterior);
                posterior
                        = (RenLiquidacion) manager.find(RenLiquidacion.class,
                                posterior.getId());

                detLiq = manager.findAll(QuerysFinanciero.getDetalleDeLiquidacion, new String[]{"liquidacion"}, new Object[]{posterior});

                for (RenDetLiquidacion temp2 : detLiq) {
                    rubro = (RenRubrosLiquidacion) manager.find(RenRubrosLiquidacion.class,
                            temp2.getRubro());
                    detList2.add(new RubrosPorTipoLiq(rubro.getDescripcion(), temp2.getValor()));
                }
            }
//            JsfUti.update("frmExoAp");
            JsfUti.executeJS("PF('dlgAnular').hide()");
            JsfUti.messageInfo(null, "Info", "Exoneracion aplicada correctamente");
            exoneraciones = new ArrayList();
            exoneracion = new FnExoneracionLiquidacion();
            solicitud = new FnSolicitudExoneracion();
            posterior = new RenLiquidacion();
            original = new RenLiquidacion();
        } else {
            JsfUti.messageInfo(null, "Info", "Hubo un problema al aplicar la exoneracion, Revise las emisiones realizadas con anterioridad");
        }

    }

    public RenLiquidacionesTitulosPredialesLazy getLiquidacionesLazy() {
        return liquidacionesLazy;
    }

    public void setLiquidacionesLazy(RenLiquidacionesTitulosPredialesLazy liquidacionesLazy) {
        this.liquidacionesLazy = liquidacionesLazy;
    }

    public RenLiquidacion getOriginal() {
        return original;
    }

    public void setOriginal(RenLiquidacion original) {
        this.original = original;
    }

    public RenLiquidacion getPosterior() {
        return posterior;
    }

    public void setPosterior(RenLiquidacion posterior) {
        this.posterior = posterior;
    }

    public List<FnExoneracionLiquidacion> getExoneraciones() {
        return exoneraciones;
    }

    public void setExoneraciones(List<FnExoneracionLiquidacion> exoneraciones) {
        this.exoneraciones = exoneraciones;
    }

    public List<RenLiquidacion> getLiquidacionseleccionadas() {
        return liquidacionseleccionadas;
    }

    public void setLiquidacionseleccionadas(List<RenLiquidacion> liquidacionseleccionadas) {
        this.liquidacionseleccionadas = liquidacionseleccionadas;
    }

    public List<RubrosPorTipoLiq> getDetList1() {
        return detList1;
    }

    public void setDetList1(List<RubrosPorTipoLiq> detList1) {
        this.detList1 = detList1;
    }

    public List<RubrosPorTipoLiq> getDetList2() {
        return detList2;
    }

    public void setDetList2(List<RubrosPorTipoLiq> detList2) {
        this.detList2 = detList2;
    }

    public FnExoneracionLiquidacion getExoneracion() {
        return exoneracion;
    }

    public void setExoneracion(FnExoneracionLiquidacion exoneracion) {
        this.exoneracion = exoneracion;
    }

    public List<RenDetLiquidacion> getDetLiq() {
        return detLiq;
    }

    public void setDetLiq(List<RenDetLiquidacion> detLiq) {
        this.detLiq = detLiq;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
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

    public RenLiquidacion getCemLiquidacion() {
        return cemLiquidacion;
    }

    public void setCemLiquidacion(RenLiquidacion cemLiquidacion) {
        this.cemLiquidacion = cemLiquidacion;
    }

}
