/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.ObservacionesLocal;
import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenActivosLocalComercial;
import com.origami.sgm.entities.RenActividadContribuyente;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenLocalCantidadAccesorios;
import com.origami.sgm.entities.RenLocalCategoria;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenLocalTipoAccesorio;
import com.origami.sgm.entities.RenLocalUbicacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.RenTipoLocalComercial;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.lazymodels.RenActividadLocalLazy;
import com.origami.sgm.lazymodels.RenLocalComercialLazy;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
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
import util.HiberUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class LocalComercialView implements Serializable {

    private static final Logger LOG = Logger.getLogger(LocalComercialView.class.getName());

    private static final Long serialVersionUID = 1L;

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;

    @javax.inject.Inject
    protected RentasServices servicesRentas;

    @Inject
    private ServletSession ss;

    @Inject
    private ReportesView reportes;

    @javax.inject.Inject
    private RecaudacionesService recaudacion;

    private PdfReporte reporte;
    private String mensaje;
    private String observaciones;

    private RenLocalComercialLazy localesComercialesLazy;
    private BaseLazyDataModel<RenLocalUbicacion> ubicaciones;
    private RenActividadLocalLazy actividadesLocal;

    private RenLocalComercial localComercial;
    private CatPredio predio;
    private CatEnte ente;
    private CatEnteLazy enteListLazy;
    private CatPredioLazy predioListLazy;
    private RenActivosLocalComercial activosLocal;
    private List<RenActividadComercial> actividadesSeleccionadas, actividadesSeleccionadas2;
    private RenLocalCantidadAccesorios cantidad;
    private List<RenLocalTipoAccesorio> tipos;
    private RenActividadComercial actividad;
    private RenLocalComercial local;

    private Integer anio;
    private Integer mes;

    private List<RenActividadContribuyente> declaracionContribuyente;

    @PostConstruct
    public void initView() {

        try {
            if (uSession.esLogueado()) {
                localesComercialesLazy = new RenLocalComercialLazy(Boolean.FALSE);
                actividadesLocal = new RenActividadLocalLazy();
                enteListLazy = new CatEnteLazy();
                predioListLazy = new CatPredioLazy("A");
                reporte = new PdfReporte();
                declaracionContribuyente = new ArrayList<>();
                anio = Utils.getAnio(new Date());
                mes = Utils.getMes(new Date());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editarLocal(RenLocalComercial local) {
        try {
            actividad = new RenActividadComercial();
            ubicaciones = new BaseLazyDataModel<>(RenLocalUbicacion.class, "descripcion", "ASC");
            actividadesLocal = new RenActividadLocalLazy();
            this.localComercial = local;
            cantidad = (RenLocalCantidadAccesorios) services.find(QuerysFinanciero.getRenLocalCantidadLast, new String[]{"local"}, new Object[]{this.localComercial});
            ente = localComercial.getPropietario();
            if (localComercial.getNumPredio() != null) {
                predio = (CatPredio) services.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{localComercial.getNumPredio()});
            }
            actividadesSeleccionadas = (List<RenActividadComercial>) localComercial.getRenActividadComercialCollection();
            if (localComercial.getActivosLocalComercialCollection() != null && !localComercial.getActivosLocalComercialCollection().isEmpty()) {
                activosLocal = (RenActivosLocalComercial) ((List) localComercial.getActivosLocalComercialCollection()).get(0);
            } else {
                activosLocal = new RenActivosLocalComercial();
                activosLocal.setEstado(Boolean.TRUE);
                activosLocal.setFechaIngreso(new Date());
                activosLocal.setUsuarioIngreso(uSession.getName_user());
            }
            if (cantidad == null) {
                cantidad = new RenLocalCantidadAccesorios();
                cantidad.setAnio(Utils.getAnio(new Date()));
                cantidad.setEstado(Boolean.TRUE);
                cantidad.setFechaIngreso(new Date());
                cantidad.setUsuarioIngreso(uSession.getName_user());
            }
            tipos = (List) services.findAllEntCopy(QuerysFinanciero.getRenLocalTipoAccesorioList, new String[]{}, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarActividadDelLocal(RenActividadComercial actv) {
        localComercial.getRenActividadComercialCollection().remove(actv);
        actividadesLocal = new RenActividadLocalLazy(localComercial.getRenActividadComercialCollection());
    }

    public void actualizarLocal() {
        if (observaciones == null && observaciones.length() == 0) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar las Observaciones");
            return;
        }
        try {
            if (localComercial.getId() != null) {
                ObservacionesLocal ol = new ObservacionesLocal(localComercial, observaciones, uSession.getName_user(), "Edicion de estado del Local");
                if (services.persist(ol) != null) {
                    HiberUtil.newTransaction();
                    if (services.update(localComercial)) {
                        JsfUti.messageInfo(null, "Informacion", "Local Actializado Correctamente");
                        inicarDatos();
                    } else {
                        JsfUti.messageError(null, "Advertencia", "Ocurrio un error al actualizar Local");
                    }
                } else {
                    JsfUti.messageError(null, "Advertencia", "Ocurrio un error al actualizar Local");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inicarDatos() {
        localComercial = null;
        observaciones = null;

    }

    public void newActivityByTaxPayer() {
        Faces.redirectFaces("/faces/rentas/mantenimiento/actividadContribuyente/nuevaActividadContribuyente.xhtml");
    }

    public void agregarCantidad() {
        if (localComercial.getCantidadAccesoriosCollection() == null) {
            localComercial.setCantidadAccesoriosCollection(new ArrayList());
        }
        if (localComercial.getId() != null) {
            cantidad.setLocalComercial(localComercial);
            services.persist(cantidad);
        }
        localComercial.getCantidadAccesoriosCollection().add(cantidad);

        cantidad = new RenLocalCantidadAccesorios();
        cantidad.setAnio(Utils.getAnio(new Date()));
        cantidad.setEstado(Boolean.TRUE);
        cantidad.setFechaIngreso(new Date());
        cantidad.setUsuarioIngreso(uSession.getName_user());
    }

    public void eliminarCantidadNew(RenLocalCantidadAccesorios acc) {
        localComercial.getCantidadAccesoriosCollection().remove(acc);
    }

    public void eliminarCantidadEdit(RenLocalCantidadAccesorios acc) {
        if (acc.getId() != null) {
            acc.setEstado(Boolean.FALSE);
            services.update(acc);
        }

        localComercial.getCantidadAccesoriosCollection().remove(acc);
    }

    public void predioSeleccionado(CatPredio predio) {
        this.predio = predio;
        JsfUti.messageInfo(null, "Info", "El predio seleccionado es el del número: " + predio.getNumPredio());
    }

    public void enteSeleccionado(CatEnte ente) {
        this.ente = ente;
        if (ente.getEsPersona()) {
            JsfUti.messageInfo(null, "Info", "El ente propietario es: " + ente.getNombres() + " " + ente.getApellidos());
        } else {
            JsfUti.messageInfo(null, "Info", "El ente propietario es: " + ente.getRazonSocial());
        }
    }

    public void razonSocialSeleccionado(CatEnte ente) {
        try {
            this.localComercial.setRazonSocial(ente);
            JsfUti.messageInfo(null, "Info", "La Razón Social es: " + ente.getNombreCompleto());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "razonSocialSeleccionado", e);
        }
    }

    public void nuevoLocalComercial() {
        localComercial = new RenLocalComercial();
        cantidad = new RenLocalCantidadAccesorios();
        cantidad.setAnio(Utils.getAnio(new Date()));
        cantidad.setEstado(Boolean.TRUE);
        cantidad.setFechaIngreso(new Date());
        cantidad.setUsuarioIngreso(uSession.getName_user());
        localComercial.setEstado(Boolean.TRUE);
        localComercial.setFechaIngreso(new Date());
        localComercial.setUsuarioIngreso(uSession.getName_user());
        localComercial.setEstadoLocalComercial(BigInteger.valueOf(1));
        activosLocal = new RenActivosLocalComercial();
        activosLocal.setEstado(Boolean.TRUE);
        activosLocal.setFechaIngreso(new Date());
        activosLocal.setUsuarioIngreso(uSession.getName_user());
        enteListLazy = new CatEnteLazy();
        predioListLazy = new CatPredioLazy("A");
        ubicaciones = new BaseLazyDataModel<>(RenLocalUbicacion.class, "descripcion", "ASC");
        actividadesLocal = new RenActividadLocalLazy();
        tipos = (List) services.findAllEntCopy(QuerysFinanciero.getRenLocalTipoAccesorioList, new String[]{}, new Object[]{});
        actividad = new RenActividadComercial();
        actividad.setEstado(Boolean.TRUE);
        actividad.setFechaIngreso(new Date());
        actividad.setUsuarioIngreso(uSession.getName_user());
    }

    public void guardarNuevoLocal() {
        try {
            if (ente == null) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar un propietario");
                return;
            }
//            if (predio == null) {
//                JsfUti.messageInfo(null, "Info", "Debe seleccionar un predio");
//                return;
//            }
            if (actividadesSeleccionadas == null || actividadesSeleccionadas.isEmpty()) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar al menos una actividad");
                return;
            }
            if (localComercial.getNombreLocal() == null || localComercial.getNombreLocal().isEmpty()) {
                JsfUti.messageInfo(null, "Info", "Debe ingresar el nombre del Local");
                return;
            }
            if (localComercial.getArea() == null) {
                JsfUti.messageInfo(null, "Info", "Debe ingresar el Area del Local");
                return;
            }
            if (localComercial.getTipoLocal() == null) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar el tipo de local");
                return;
            }
            if (localComercial.getUbicacion() == null) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar la Ubicacion");
                return;
            }

            localComercial.setPropietario(ente);
            if (predio != null) {
                localComercial.setNumPredio(predio.getNumPredio());
            }
            localComercial.setRenActividadComercialCollection(actividadesSeleccionadas);
            //activosLocal.setLocalComercial(localComercial);
            localComercial = servicesRentas.guardarLocalComercial(localComercial, activosLocal);
            if (localComercial != null) {
                JsfUti.messageInfo(null, "Info", "Local comercial creado correctamente");
                JsfUti.executeJS("PF('dlgNew').hide()");
            } else {
                JsfUti.messageError(null, "Info", "Hubo un error al crear el local comercial. Inténtelo nuevamente");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BaseLazyDataModel<RenLocalUbicacion> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(BaseLazyDataModel<RenLocalUbicacion> ubicaciones) {
        this.ubicaciones = ubicaciones;
    }

    public void seleccionarUbicacion(RenLocalUbicacion ubicacion) {
        this.localComercial.setUbicacion(ubicacion);
        JsfUti.messageInfo(null, "Info", "Ubicacion seleccionada: " + ubicacion.getDescripcion());
    }

    public void guardarLocalEdit() {
        Boolean validar = false;
        try {
            if (ente == null) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar un propietario");
                return;
            }
//            if (predio == null) {
//                JsfUti.messageInfo(null, "Info", "Debe seleccionar un predio");
//                return;
//            }
            if (localComercial.getRenActividadComercialCollection() == null || localComercial.getRenActividadComercialCollection().isEmpty()) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar al menos una actividad");
                return;
            }
            localComercial.setPropietario(ente);
            if (predio != null) {
                localComercial.setNumPredio(predio.getNumPredio());
            }
//            services.update(localComercial);
            if (servicesRentas.guardarLocalComercial(localComercial, activosLocal) != null) {

                JsfUti.messageInfo(null, "Info", "Local comercial actualizado correctamente");
            } else {
                JsfUti.messageError(null, "Info", "Hubo un error al crear el local comercial. Inténtelo nuevamente");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void agregarActvs() {
        try {
            if (this.localComercial.getRenActividadComercialCollection() == null || this.localComercial.getRenActividadComercialCollection().isEmpty()) {
                localComercial.setRenActividadComercialCollection(actividadesSeleccionadas);
                for (RenActividadComercial temp : actividadesSeleccionadas) {
                }
            } else {
                for (RenActividadComercial temp : actividadesSeleccionadas) {
                    if (!localComercial.getRenActividadComercialCollection().contains(temp)) {
                        localComercial.getRenActividadComercialCollection().add(temp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void accionLocal(RenLocalComercial localComercial, Integer x) {
        this.localComercial = localComercial;
        switch (x) {
            case 2:
                mensaje = "Observaciones para inhabilitar Local";
                this.localComercial.setEstadoLocalComercial(BigInteger.valueOf(2));
                this.localComercial.setEstado(false);
                break;
            default:
                mensaje = "Observaciones para habilitar Local";
                this.localComercial.setEstadoLocalComercial(BigInteger.valueOf(1));
                this.localComercial.setEstado(true);
                break;
        }
        JsfUti.executeJS("PF('obs').show()");
        JsfUti.update("frmObsCor");
    }

    public void imprimirCertificado(RenLocalComercial local) {

        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.instanciarParametros();
        ss.agregarParametro("LOGO", path + SisVars.sisLogo);
        ss.agregarParametro("ID", local.getId());
        ss.setTieneDatasource(Boolean.TRUE);
        ss.setNombreSubCarpeta("rentas/localComercial/");
        ss.setNombreReporte("sLocalComercial");
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    /*REPORTE DE PEDEF GENERALES*/
    public void nuevoReporteGeneral() {
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.instanciarParametros();
        ss.agregarParametro("LOGO", path + SisVars.sisLogo);
        //ss.agregarParametro("ID", local.getId());
        ss.setTieneDatasource(Boolean.TRUE);
        ss.setNombreSubCarpeta("rentas/localComercial/");
        ss.setNombreReporte("sLocalListaComercial");
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void eliminarActividad(RenActividadComercial act) {
        try {
            act.setEstado(Boolean.FALSE);
            services.persist(act);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editarActividad() {
        try {
            if (actividad.getDescripcion() == null) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar la descripcion");
                return;
            }
            services.update(actividad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verLiquidaciones(RenLocalComercial local) {
        ss.instanciarParametros();
        ss.agregarParametro("localComercial", local);
        JsfUti.redirectFacesNewTab("/rentas/liquidaciones/liquidacionesLocales.xhtml");
    }

    public void nuevaActividad() {
        actividad = new RenActividadComercial();
    }

    public void guardarActividad() {
        if (actividad.getDescripcion() == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar la descripcion");
            return;
        }
        try {
            actividad.setDescripcion(actividad.getDescripcion().toUpperCase());
            actividad.setEstado(Boolean.TRUE);
            actividad = servicesRentas.guardarActividad(actividad);
            if (actividad != null) {
                actividadesSeleccionadas.add(actividad);
                agregarActvs();
                JsfUti.messageInfo(null, "Informacion", "Actividad Agregada Correctamente");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void emisionMensualLocalComercial() {
        try {
            List<RenLocalComercial> locales = (List<RenLocalComercial>) services.findAll("SELECT rl FROM RenLocalComercial rl WHERE rl.estadoLocalComercial = 1");
            RenTipoLiquidacion tipoLiquidacion = (RenTipoLiquidacion) services.find("SELECT rt FROM RenTipoLiquidacion rt WHERE rt.id = 46");
            RenLiquidacion liquidacion, liquidacionSearch;
            RenRubrosLiquidacion rubroSelect;
            rubroSelect = new RenRubrosLiquidacion(39L);
            rubroSelect.setCantidad(1);
            rubroSelect.setAnio(anio);
            rubroSelect.setMes(mes);
            rubroSelect.setValor(new BigDecimal("4.00"));

            List<RenRubrosLiquidacion> rubrosSeleccionado;
            Boolean generaEmison = Boolean.TRUE;
            for (RenLocalComercial rl : locales) {
                liquidacionSearch = (RenLiquidacion) services.find(Querys.getLocalComercial,
                        new String[]{"mes", "anio", "localComercial"},
                        new Object[]{mes, anio, rl.getId()});
                if (liquidacionSearch != null) {
                    if (liquidacionSearch.getEstadoLiquidacion().getId().equals(1L)) {
                        generaEmison = Boolean.FALSE;
                    } else {
                        liquidacionSearch.setEstadoLiquidacion(new RenEstadoLiquidacion(3L));
                        services.persist(liquidacionSearch);
                    }

                }
                if (generaEmison) {
                    rubrosSeleccionado = new ArrayList<>();

                    liquidacion = new RenLiquidacion();
                    liquidacion.setLocalComercial(rl);
                    liquidacion.setAnio(anio);
                    liquidacion.setMes(mes);
                    liquidacion.setComprador(rl.getPropietario());
                    liquidacion.setTipoLiquidacion(tipoLiquidacion);
                    liquidacion.setCostoAdq(BigDecimal.ZERO);
                    liquidacion.setCuantia(BigDecimal.ZERO);
                    liquidacion.setFechaContratoAnt(new Date());
                    liquidacion.setCodigoLocal(rl.getNombreLocal());
                    liquidacion.setValorComercial(BigDecimal.ZERO);
                    liquidacion.setValorCatastral(BigDecimal.ZERO);
                    liquidacion.setValorHipoteca(BigDecimal.ZERO);
                    liquidacion.setValorNominal(BigDecimal.ZERO);
                    liquidacion.setValorMora(BigDecimal.ZERO);
                    liquidacion.setTotalAdicionales(BigDecimal.ZERO);
                    liquidacion.setOtros(BigDecimal.ZERO);
                    liquidacion.setValorCompra(BigDecimal.ZERO);
                    liquidacion.setValorVenta(BigDecimal.ZERO);
                    liquidacion.setPatrimonio(BigDecimal.ZERO);
                    liquidacion.setValorMejoras(BigDecimal.ZERO);
                    liquidacion.setAvaluoConstruccion(BigDecimal.ZERO);
                    liquidacion.setAvaluoMunicipal(BigDecimal.ZERO);
                    liquidacion.setAvaluoSolar(BigDecimal.ZERO);
                    //liquidacion.setBandaImpositiva(BigDecimal.ZERO);
                    liquidacion.setValorExoneracion(BigDecimal.ZERO);
                    liquidacion.setAreaTotal(rl.getArea().setScale(2, RoundingMode.HALF_UP));
                    liquidacion.setEstaExonerado(Boolean.FALSE);

                    rubroSelect.setValorTotal(rubroSelect.getValor().multiply(liquidacion.getLocalComercial().getArea()).setScale(2, RoundingMode.HALF_UP));
                    rubrosSeleccionado.add(rubroSelect);
                    liquidacion.setTotalPago(rubroSelect.getValorTotal());
                    liquidacion.setSaldo(rubroSelect.getValorTotal());
                    liquidacion.setUsuarioIngreso(uSession.getName_user());
                    liquidacion.getTipoLiquidacion().setRenRubrosLiquidacionCollection(rubrosSeleccionado);

                    recaudacion.grabarLiquidacion(liquidacion);
                }
            }
            JsfUti.messageInfo(null, "Informacion", "Se emitio correctamente - Mes: " + mes + "-" + anio + ""
                    + " Cantidad de locales emitidos: " + locales.size());
            JsfUti.executeJS("PF('dlgEmisionMensualLocales').hide()");
        } catch (Exception e) {
            e.printStackTrace();
            JsfUti.messageError(null, "Error", "Ocurrió un error al emitir");
            JsfUti.executeJS("PF('dlgEmisionMensualLocales').hide()");
        }
    }

    public List<RenTipoLocalComercial> getTipoLocal() {
        return servicesRentas.getTipoLocals();
    }

    public List<RenLocalCategoria> getCategorias() {
        return servicesRentas.getCategorias();
    }

    public RenActivosLocalComercial getActivosLocal() {
        return activosLocal;
    }

    public void setActivosLocal(RenActivosLocalComercial activosLocal) {
        this.activosLocal = activosLocal;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public RenLocalComercialLazy getLocalesComercialesLazy() {
        return localesComercialesLazy;
    }

    public void setLocalesComercialesLazy(RenLocalComercialLazy localesComercialesLazy) {
        this.localesComercialesLazy = localesComercialesLazy;
    }

    public RenLocalComercial getLocalComercial() {
        return localComercial;
    }

    public void setLocalComercial(RenLocalComercial localComercial) {
        this.localComercial = localComercial;
    }

    public CatEnteLazy getEnteListLazy() {
        return enteListLazy;
    }

    public void setEnteListLazy(CatEnteLazy enteListLazy) {
        this.enteListLazy = enteListLazy;
    }

    public CatPredioLazy getPredioListLazy() {
        return predioListLazy;
    }

    public void setPredioListLazy(CatPredioLazy predioListLazy) {
        this.predioListLazy = predioListLazy;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public RenActividadLocalLazy getActividadesLocal() {
        return actividadesLocal;
    }

    public void setActividadesLocal(RenActividadLocalLazy actividadesLocal) {
        this.actividadesLocal = actividadesLocal;
    }

    public List<RenActividadComercial> getActividadesSeleccionadas() {
        return actividadesSeleccionadas;
    }

    public void setActividadesSeleccionadas(List<RenActividadComercial> actividadesSeleccionadas) {
        this.actividadesSeleccionadas = actividadesSeleccionadas;
    }

    public RenLocalCantidadAccesorios getCantidad() {
        return cantidad;
    }

    public void setCantidad(RenLocalCantidadAccesorios cantidad) {
        this.cantidad = cantidad;
    }

    public List<RenLocalTipoAccesorio> getTipos() {
        return tipos;
    }

    public void setTipos(List<RenLocalTipoAccesorio> tipos) {
        this.tipos = tipos;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public RenActividadComercial getActividad() {
        return actividad;
    }

    public void setActividad(RenActividadComercial actividad) {
        this.actividad = actividad;
    }

    public RenLocalComercial getLocal() {
        return local;
    }

    public void setLocal(RenLocalComercial local) {
        this.local = local;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public List<RenActividadContribuyente> getDeclaracionContribuyente() {
        return declaracionContribuyente;
    }

    public void setDeclaracionContribuyente(List<RenActividadContribuyente> declaracionContribuyente) {
        this.declaracionContribuyente = declaracionContribuyente;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

}
