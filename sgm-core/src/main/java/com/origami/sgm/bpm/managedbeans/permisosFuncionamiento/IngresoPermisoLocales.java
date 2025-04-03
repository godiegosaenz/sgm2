/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.permisosFuncionamiento;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenActivosLocalComercial;
import com.origami.sgm.entities.RenAfiliacionCamaraProduccion;
import com.origami.sgm.entities.RenBalanceLocalComercial;
import com.origami.sgm.entities.RenClaseLocal;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenLocalCantidadAccesorios;
import com.origami.sgm.entities.RenLocalCategoria;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenLocalTipoAccesorio;
import com.origami.sgm.entities.RenLocalUbicacion;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.RenTipoLocalComercial;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.lazymodels.RenLocalComercialLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.activiti.engine.runtime.ProcessInstance;
import util.EntityBeanCopy;
import util.GroovyUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 * @Date 27/08/2016
 */
@Named
@ViewScoped
public class IngresoPermisoLocales extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(IngresoPermisoLocales.class.getName());

    @javax.inject.Inject
    private RentasServices services;
    @Inject
    private UserSession session;
    @javax.inject.Inject
    private Entitymanager servicesACL;

    private String ciRuc;
    private String ciRucContador;
    private String obs;
    private BigDecimal baseImponible;

    private CatEnte contribuyente;
    private CatPredio predio, predioSel;
    private RenLocalComercial local;
    private List<RenLocalComercial> localesContribuyente;
    private List<RenTipoLiquidacion> tipoLiquidacion;

    // permiso funcionamiento
    private RenPermisosFuncionamientoLocalComercial permisosFuncionamiento;
    private RenClaseLocal claseLocal;
    private RenAfiliacionCamaraProduccion afiliacionCamara;
    private Integer tipoPersona = 1;

    private RenLocalComercialLazy localesComercialesLazy;
    private CatEnteLazy enteListLazy;
    private CatPredioLazy predioListLazy;
    private List<RenActividadComercial> actividadesComercialesList, actividadesSeleccionadas;
    private RenActivosLocalComercial activos;
    private RenBalanceLocalComercial balance;
    private RenLocalCantidadAccesorios cantidad;
    private List<RenLocalTipoAccesorio> tipos;

    private MatFormulaTramite formula;
    private GroovyUtil util;

    private Integer maxAnio;
    private Integer maxAnioAct;
    private Integer minAnio = 1990;

    @PostConstruct
    protected void initView() {
        initVar();
    }

    private void initVar() {
        local = new RenLocalComercial();
        maxAnio = Utils.getAnio(new Date()) - 1;
        maxAnioAct = Utils.getAnio(new Date());
        predio = new CatPredio();
        permisosFuncionamiento = new RenPermisosFuncionamientoLocalComercial();
        permisosFuncionamiento.setPrimeraVez(false);
        permisosFuncionamiento.setContador(new CatEnte());
        localesComercialesLazy = new RenLocalComercialLazy();
        enteListLazy = new CatEnteLazy();
        predioListLazy = new CatPredioLazy("A");
    }

    public void buscarEnte(int t) {
        CatEnte temp = null;
        try {
            switch (t) {
                case 1:
                    if (ciRuc == null) {
                        JsfUti.messageError(null, "Advertencia", "Debe Ingresar el Número de Identificación");
                        return;
                    }
                    if (!Utils.validateNumberPattern(ciRuc)) {
                        JsfUti.messageError(null, "Advertencia", "Número de Identificación invalido");
                        return;
                    }
//                    CatEnte temp = services.consultarEnte(ciRuc, (ciRuc.length() == 13), session.getName_user());
                    if (temp == null) {
                        temp = services.permisoServices().getCatEnteByCiRucByEsPersona(ciRuc, (ciRuc.length() == 10));
                        if (temp != null) {
                            contribuyente = clonarEnte(temp);
                        } else {
                            JsfUti.messageError(null, "Advertencia", "Número de Identificación invalido");
                            return;
                        }
                    } else {
                        contribuyente = clonarEnte(temp);
                    }

                    if (contribuyente != null) {
                        localesContribuyente = (List<RenLocalComercial>) EntityBeanCopy.clone(temp.getLocalesComercialesCollection());
                    }
                    JsfUti.update("frmPermisoFuncion");
                    break;
                case 2:
                    if (ciRucContador == null) {
                        JsfUti.messageError(null, "Advertencia", "Debe Ingresar el Número de Identificación");
                        return;
                    }
                    if (!Utils.validateNumberPattern(ciRucContador)) {
                        JsfUti.messageError(null, "Advertencia", "Número de Identificación invalido");
                        return;
                    }

//                    CatEnte tempc = services.consultarEnte(ciRucContador, (ciRucContador.length() == 13), session.getName_user());
                    if (temp == null) {
                        temp = services.permisoServices().getCatEnteByCiRucByEsPersona(ciRucContador, (ciRucContador.length() == 10));
                        if (temp != null) {
                            permisosFuncionamiento.setContador(clonarEnte(temp));
                        } else {
                            JsfUti.messageError(null, "Advertencia", "Número de Identificación invalido");
                            return;
                        }
                    } else {
                        permisosFuncionamiento.setContador(clonarEnte(temp));
                    }
                    break;
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, "Consulta ente " + ciRuc, e);
        }

    }

    public void agregarLocal(RenLocalComercial local) {
        if (localesContribuyente == null) {
            localesContribuyente = new ArrayList();
        }
        localesContribuyente.add(local);
        this.seleccionar(local);
    }

    public CatEnte clonarEnte(final CatEnte ente) {
        CatEnte t = (CatEnte) EntityBeanCopy.clone(ente);
        if (ente.getEnteCorreoCollection() != null) {
            ente.getEnteCorreoCollection().size();
            t.setEnteCorreoCollection((List<EnteCorreo>) EntityBeanCopy.clone(ente.getEnteCorreoCollection()));
        }
        if (ente.getEnteTelefonoCollection() != null) {
            ente.getEnteTelefonoCollection().size();
            t.setEnteTelefonoCollection((List<EnteTelefono>) EntityBeanCopy.clone(ente.getEnteTelefonoCollection()));
        }
        if (ente.getLocalesComercialesCollection() != null) {
            ente.getLocalesComercialesCollection().size();
            t.setLocalesComercialesCollection(clonarLocal(ente.getLocalesComercialesCollection()));
        }
        if (ente.getLocalesComercialesCollection2() != null) {
            ente.getLocalesComercialesCollection2().size();
            t.setLocalesComercialesCollection2(clonarLocal(ente.getLocalesComercialesCollection2()));
        }
        return t;
    }

    private List<RenLocalComercial> clonarLocal(List<RenLocalComercial> locales) {
        List<RenLocalComercial> result = new ArrayList<>();
        for (RenLocalComercial lc : locales) {
            RenLocalComercial lcClone = (RenLocalComercial) EntityBeanCopy.clone(lc);
            if (lc.getRenActividadComercialCollection() != null) {
                lc.getRenActividadComercialCollection().size();
                lcClone.setRenActividadComercialCollection((Collection<RenActividadComercial>) EntityBeanCopy.clone(lc.getRenActividadComercialCollection()));
            }
            if (lc.getUbicacion() != null) {
                lc.getUbicacion().getId();
                lcClone.setUbicacion((RenLocalUbicacion) EntityBeanCopy.clone(lc.getUbicacion()));
            }
            if (lc.getCategoria() != null) {
                lc.getCategoria().getId();
                lcClone.setCategoria((RenLocalCategoria) EntityBeanCopy.clone(lc.getCategoria()));
            }
            if (lc.getTipoLocal() != null) {
                lc.getTipoLocal().getId();
                lcClone.setTipoLocal((RenTipoLocalComercial) EntityBeanCopy.clone(lc.getTipoLocal()));
            }
            if (lc.getPropietario() != null) {
                lc.getPropietario().getId();
                lcClone.setPropietario((CatEnte) EntityBeanCopy.clone(lc.getPropietario()));
            }
            if (lc.getRazonSocial() != null) {
                lc.getRazonSocial().getId();
                lcClone.setRazonSocial((CatEnte) EntityBeanCopy.clone(lc.getRazonSocial()));
            }
            if (lcClone.getContabilidad() == null) {
                lcClone.setContabilidad(false);
            }
            result.add(lcClone);
        }
        return result;
    }

    public void buscarPredio() {
        if (predio.getNumPredio() != null) {
            CatPredio temp = services.permisoServices().getFichaServices().getPredioByNum(predio.getNumPredio().longValue());
            if (temp != null) {
                predio = temp;
            }
        }
    }

    public void nuevoLocalComercial() {
        try {
            local = new RenLocalComercial();
            actividadesComercialesList = new ArrayList();
            //activos = new RenActivosLocalComercial();

            local.setEstadoLocalComercial(BigInteger.ONE);
            cantidad = new RenLocalCantidadAccesorios();
            cantidad.setAnio(Utils.getAnio(new Date()));
            cantidad.setEstado(Boolean.TRUE);
            cantidad.setFechaIngreso(new Date());
            cantidad.setUsuarioIngreso(session.getName_user());

            tipos = (List) servicesACL.findAllEntCopy(QuerysFinanciero.getRenLocalTipoAccesorioList, new String[]{}, new Object[]{});

            for (RenActividadComercial temp : (List<RenActividadComercial>) servicesACL.findAll(QuerysFinanciero.getRenActividadesComercialesList, new String[]{}, new Object[]{})) {
                actividadesComercialesList.add((RenActividadComercial) EntityBeanCopy.clone(temp));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void localComercialNew() {
        if (permisosFuncionamiento.getPrimeraVez()) {
            this.nuevoLocalComercial();
            JsfUti.update("frmNew");
            JsfUti.messageInfo(null, "Info", "Ingrese los datos del nuevo local comercial");
            JsfUti.executeJS("PF('dlgNew').show()");
        }
    }

    public void eliminarCantidadNew(RenLocalCantidadAccesorios acc) {
        local.getCantidadAccesoriosCollection().remove(acc);
    }

    public void agregarCantidad() {
        if (local.getCantidadAccesoriosCollection() == null) {
            local.setCantidadAccesoriosCollection(new ArrayList());
        }
        if (local.getId() != null) {
            cantidad.setLocalComercial(local);
            servicesACL.persist(cantidad);
        }
        local.getCantidadAccesoriosCollection().add(cantidad);

        cantidad = new RenLocalCantidadAccesorios();
        cantidad.setAnio(Utils.getAnio(new Date()));
        cantidad.setEstado(Boolean.TRUE);
        cantidad.setFechaIngreso(new Date());
        cantidad.setUsuarioIngreso(session.getName_user());
    }

    public void guardarNuevoLocal() {
        try {
            if (predio == null) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar un predio");
                return;
            }
            if (actividadesSeleccionadas == null || actividadesSeleccionadas.isEmpty()) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar al menos una actividad");
                return;
            }
            if (actividadesSeleccionadas == null || actividadesSeleccionadas.isEmpty()) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar al menos una actividad");
                return;
            }
            if (local.getTipoLocal() == null) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar el tipo de local");
                return;
            }
            if (local.getUbicacion() == null) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar la Ubicacion");
                return;
            }
            local.setUsuarioIngreso(session.getName_user());
            local.setFechaIngreso(new Date());
            local.setEstado(Boolean.TRUE);
            local.setNumPredio(predio.getNumPredio());
            local.setTurismo(Boolean.FALSE);
            local.setEstadoLocalComercial(BigInteger.valueOf(1));
            local.setRenActividadComercialCollection(actividadesSeleccionadas);
            local.setPropietario(contribuyente);

            if (localesContribuyente == null) {
                localesContribuyente = new ArrayList();
            }
            //local = (RenLocalComercial)servicesACL.persist(local);
            //if(local != null){
            localesContribuyente.add(local);
            this.seleccionar(local);
            cargarPermiso();
            JsfUti.executeJS("PF('dlgNew').hide()");
            //}else{
            //    JsfUti.messageInfo(null, "Info", "Hubo un error al guardar el local comercial");
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelarNewLocal() {
        //permisosFuncionamiento.setPrimeraVez(Boolean.FALSE);
        JsfUti.update("frmPermisoFuncion");
    }

    public void predioSeleccionado(CatPredio predio) {
        this.predio = predio;
        JsfUti.messageInfo(null, "Info", "El predio seleccionado es el del número: " + predio.getNumPredio());
    }

    public void seleccionarUbicacion(RenLocalUbicacion ubicacion) {
        this.local.setUbicacion(ubicacion);
        JsfUti.messageInfo(null, "Info", "Ubicacion seleccionada: " + ubicacion.getDescripcion());
    }

    public void seleccionar(RenLocalComercial lc) {

        this.local = (RenLocalComercial) EntityBeanCopy.clone(lc);
        this.local.setContabilidad(Boolean.TRUE);

        if (this.local.getNumPredio()
                != null) {
            CatPredio temp = services.permisoServices().getFichaServices().getPredioByNum(this.local.getNumPredio().longValue());
            if (temp != null) {
                this.predio = temp;
            } else {
                this.predio = new CatPredio();
                JsfUti.messageInfo(null, "Info", "El local comercial no tiene un predio asociado.");
            }
        } else {
            this.predio = new CatPredio();
            JsfUti.messageInfo(null, "Info", "El local comercial no tiene un predio asociado.");
        }
        if (claseLocal
                != null) {
            cargarPermiso();
        }
        JsfUti.messageInfo(null, "Info", "Local comercial seleccionado correctamente.");
        if (this.local.getId() == null) {
            return;
        } else {
            List<RenLiquidacion> existeLiquidacion = services.getLiquidacionesLocal(local, Utils.getAnio(new Date()));
            if (existeLiquidacion != null && !existeLiquidacion.isEmpty()) {
                JsfUti.messageError(null, "Error", "Ya fueron ingresados los permisos para el local seleccionado");
                return;
            }
        }
    }

    public void verificarPermiso() {

        try {
            for (RenTipoLiquidacion t : tipoLiquidacion) {
                if (t.getCodigoTituloReporte().equals(206l)) {
                    JsfUti.messageInfo(null, "Informacion", "Este permiso es obligatorio.");
                    return;
                }
            }
            System.out.println("Permisos " + tipoLiquidacion.size());

            actulizarPermisoSolicit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "veificarPermiso()", e);
        }
    }

    public void baseImponible() {
        try {
            if (activos == null) {
                baseImponible = BigDecimal.ZERO;
                return;
            }
            BigDecimal act = (activos.getActivoTotal() == null) ? BigDecimal.ZERO : activos.getActivoTotal();
            BigDecimal pasT = (activos.getPasivoTotal() == null) ? BigDecimal.ZERO : activos.getPasivoTotal();
            BigDecimal pasC = (activos.getPasivoContingente() == null) ? BigDecimal.ZERO : activos.getPasivoContingente();

            if (activos.getPorcentajeIngreso() != null && activos.getPorcentajeIngreso().doubleValue() > 0) {
                BigDecimal porc = activos.getPorcentajeIngreso().divide(BigDecimal.valueOf(100));
                baseImponible = act.subtract(pasT.add(pasC)).multiply(porc);
            }
            baseImponible = act.subtract(pasT.add(pasC));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "CACULO DE BASE IMPONIBLE.", e);
            baseImponible = BigDecimal.ZERO;
        }
    }

    public void cargarPermiso() {
        if (local == null) {
            JsfUti.messageError(null, "Advertencia", "Debe seleccionar o ingresar el Local");
            return;
        }
        try {
            if (claseLocal != null) {
                tipoLiquidacion = new ArrayList<>();
                for (RenTipoLiquidacion tl : getPermiso()) {

                    switch (claseLocal.getId().intValue()) {
                        case 1:
                            if (tl.getCodigoTituloReporte().compareTo(14L) == 0) {
                                tipoLiquidacion.add(tl);
                                if (balance == null) {
                                    balance = new RenBalanceLocalComercial();
                                }
                            }
                            break;
                        case 2:
                            if (tl.getCodigoTituloReporte().compareTo(14L) == 0
                                    || tl.getCodigoTituloReporte().compareTo(15L) == 0
                                    || tl.getCodigoTituloReporte().compareTo(98L) == 0) {
                                tipoLiquidacion.add(tl);
                                if (balance == null) {
                                    balance = new RenBalanceLocalComercial();
                                }
                                permisoTurismo(tl);
                            }
                            break;
                        case 3:
                            if (tl.getCodigoTituloReporte().compareTo(14L) == 0
                                    || tl.getCodigoTituloReporte().compareTo(15L) == 0
                                    || tl.getCodigoTituloReporte().compareTo(98L) == 0) {
                                tipoLiquidacion.add(tl);
                                if (balance == null) {
                                    balance = new RenBalanceLocalComercial();
                                }
                                permisoTurismo(tl);
                            }
                            break;
                    }
                    if (local != null) {
                        if (tl.getCodigoTituloReporte().compareTo(11L) == 0 && local.getContabilidad()) {
                            tipoLiquidacion.add(tl);
                            if (activos == null) {
                                activos = new RenActivosLocalComercial();
                            }
                            if (formula == null) {
                                formula = services.getMatFormulaByPrefijo("ACT");
                                if (formula != null) {
                                    util = new GroovyUtil(formula.getFormula());
                                }
                            }
                        }
                    }
                }
                actulizarPermisoSolicit();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }

    private void permisoTurismo(RenTipoLiquidacion tl) {
        if (local.getRenActividadComercialCollection() != null && local.getTipoLocal() != null) {
            for (RenActividadComercial actl : local.getRenActividadComercialCollection()) {
                if (services.actividadTuristica(actl, local.getTipoLocal())) {
                    tipoLiquidacion.add(tl);
                    break;
                }
            }
        }
    }

    public void actulizarPermisoSolicit() {
        try {
            permisosFuncionamiento.setRotulos(false);
            permisosFuncionamiento.setActivos(false);
            permisosFuncionamiento.setPatente(false);
            permisosFuncionamiento.setTasaHabilitacion(false);
            permisosFuncionamiento.setTurismo(false);
            for (RenTipoLiquidacion lo : tipoLiquidacion) {
                if (null != lo.getCodigoTituloReporte()) {
                    switch (lo.getCodigoTituloReporte().intValue()) {
                        case 11:
                            permisosFuncionamiento.setActivos(true);
                            break;
                        case 14:
                            permisosFuncionamiento.setPatente(true);
                            break;
                        case 15:
                            permisosFuncionamiento.setTasaHabilitacion(true);
                            break;
                        //                    permisosFuncionamiento.setBomberos(true);
                        case 53:
                            break;
                        case 98:
                            permisosFuncionamiento.setTurismo(true);
                            local.setTurismo(Boolean.TRUE);
                            break;
                        case 206:
                            if (permisosFuncionamiento.getTipoRotulo() == null) {
                                permisosFuncionamiento.setTipoRotulo("rotulo");
                            }
                            permisosFuncionamiento.setRotulos(true);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<RenLiquidacion> getPermisoAnteriores() {
        if (local.getId() != null) {
            return services.getLiquidacionesLocal(local, Utils.getAnio(new Date()) - 1);
        }

        return null;
    }

    public List<RenTipoLiquidacion> getPermiso() {
        if (claseLocal != null) {
            return (List<RenTipoLiquidacion>) EntityBeanCopy.clone(services.gettiposLiquidacionByCodTitRep(2));
        }
        return null;
    }

    /// --- LISTADO 
    public List<RenClaseLocal> getClasesLocal() {
        return services.getClasesLocal();
    }

    public List<RenAfiliacionCamaraProduccion> getAfiliacionesCamara() {
        return services.getAfiliacionCamara();
    }

    public List<GeRequisitosTramite> getRequisitosTramites() {
        try {
            
            if (permisosFuncionamiento != null) {
                
                List<GeRequisitosTramite> result = new ArrayList<>();
                List<GeRequisitosTramite> requisitosTramites = new ArrayList<>();
                requisitosTramites = services.requisitosTramite(true);
                if (requisitosTramites == null) {
                    return null;
                }else{
                    
                }

                if (permisosFuncionamiento.getPrimeraVez()) {
                    for(int i = 0; i < requisitosTramites.size(); i++){
                        if (requisitosTramites.get(i).getStringValue() != null) {
                            if (requisitosTramites.get(i).getStringValue().contains("P")) {
                                result.add(requisitosTramites.get(i));
                            }
                        }
                    }
                } else {
                    
                    for (GeRequisitosTramite rq : requisitosTramites) {
                        if (rq.getStringValue() != null) {
                            if (rq.getStringValue().contains("R")) {
                                if (rq.getId() == 62 || rq.getId() == 63) {
                                    String concat = rq.getNombre().concat(" (Opcional)");
                                    rq.setDescripcion(concat);
                                }
                                result.add(rq);
                            }
                        }
                    }
                }
                return result;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "REQUISITOS", e);
        }
        return null;
    }

    private Boolean datosPermisos() {
        if (permisosFuncionamiento.getActivos()) {
            if (activos.getActivoTotal() == null) {
                JsfUti.messageError(null, "Advertencia", "Debe llenar los campos correspondientes a Activos Totales correspondientes al Canton");
                return false;
            }
        }
        if (permisosFuncionamiento.getPatente()) {
            if (balance.getCapital() == null) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar el capital en la seccion de Patente Anual");
                return false;
            }
            if (balance.getAnioBalance() == null) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar el año del Balance");
                return false;
            }
        }
        if (permisosFuncionamiento.getRotulos()) {
            if (permisosFuncionamiento.getMtrsRotulo() == null) {
                JsfUti.messageError(null, "Advertencia", "Debe Ingresar los metros cuadrados del Publicidad en la seccion de Rotulos Publicitarios");
                return false;
            }
        }

        return true;
    }

    /**
     * Realiza el registro del tramite de permiso de funcionamiento se realizan
     * las validaciones necesarias en el ingreso de los datos del solicitante.
     */
    public void procesar() {
        /*List<RenLiquidacion> existeLiquidacion = services.getLiquidacionesLocal(local, Utils.getAnio(new Date()));
        if (existeLiquidacion != null && !existeLiquidacion.isEmpty()) {
            JsfUti.messageError(null, "Error", "Ya fueron ingresados los permisos para el local seleccionado");
            return;
        }*/
        if (services.existePermiso(local)) {
            JsfUti.messageError(null, "Error", "Ya fue ingresado un trámite de permiso de funcionamiento");
            return;
        }
        if (contribuyente == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar los datos del contribuyente.");
            return;
        }
        if (claseLocal == null) {
            JsfUti.messageError(null, "Advertencia", "Debe seleccionar la clasificacion de personas.");
            return;
        }
        if (local == null || local.getNombreLocal() == null) {
            JsfUti.messageError(null, "Advertencia", "Debe seleccionar el local.");
            return;
        }
        if (predio == null || predio.getId() == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar la Matricula Inmobiliaria.");
            return;
        }
        if (permisosFuncionamiento.getAfiliacionCamara() == null) {
            JsfUti.messageError(null, "Advertencia", "Debe seleccionar la afiliación de la cámara del local.");
            return;
        }

//        if (Utils.isEmpty(tipoLiquidacion)) {
//            JsfUti.messageError(null, "Advertencia", "Debe seleccionar por lo menos un permiso para inicar el tramite.");
//            return;
//        }
        /*if (!datosPermisos()) {
            return;
        }*/
        if (obs == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar las observaciones.");
            return;
        }

        try {
            this.actulizarPermisoSolicit();
            local.setNumPredio(predio.getNumPredio());
            permisosFuncionamiento.setEsPublico(false);
            permisosFuncionamiento.setFechaEmision(new Date());
            permisosFuncionamiento.setLocalComercial(local);
            permisosFuncionamiento.setUsuarioIngreso(session.getName_user());

            if (permisosFuncionamiento.getContador() != null) {
                if (permisosFuncionamiento.getContador().getId() == null) {
                    permisosFuncionamiento.setContador(null);
                }
            }
            permisosFuncionamiento.setTipo(tipoPersona);
            permisosFuncionamiento.setClaseLocal(claseLocal);

            if (permisosFuncionamiento.getPrimeraVez() && local.getId() == null) {
                local.setPropietario(contribuyente);
                if ((local = services.guardarLocalComercial(local, null)) != null) {
                    JsfUti.messageInfo(null, "Info", "Local comercial creado correctamente");
                } else {
                    JsfUti.messageError(null, "Info", "Hubo un error al crear el local comercial. Inténtelo nuevamente");
                    return;
                }
            }

            if (!permisosFuncionamiento.getActivos()) {
                activos = null;
            }
            if (!permisosFuncionamiento.getPatente()) {
                balance = null;
            }

            if (activos != null) {
                activos.setFechaIngreso(new Date());
                activos.setUsuarioIngreso(session.getName_user());
                activos.setEstado(Boolean.TRUE);
            }
            if (balance != null) {
                balance.setEstado(Boolean.TRUE);
            }

            permisosFuncionamiento.setLocalComercial(local);

            permisosFuncionamiento = services.guadarPermisoFuncionamiento(permisosFuncionamiento, contribuyente, session.getUserId(), activos, balance);
            if (permisosFuncionamiento != null || permisosFuncionamiento.getId() != null) {
                HashMap<String, Object> paramt = new HashMap<>();
                GeTipoTramite tramite = permisosFuncionamiento.getHt().getTipoTramite();
                paramt.put("tiene_archivos", !this.getFiles().isEmpty());
                paramt.put("reasignar", 2);
                paramt.put("carpeta", tramite.getCarpeta());
                paramt.put("listaArchivos", this.getFiles());
                paramt.put("listaArchivosFinal", new ArrayList<>());
                paramt.put("prioridad", 50);
                paramt.put("tipo_comisaria", 0);
                paramt.put("descripcion", tramite.getDescripcion());
                paramt.put("tramite", permisosFuncionamiento.getHt().getId());

                paramt.put("task_def", tramite.getDescripcion());
                if (claseLocal.getId() != 1l) {
                    paramt.put("es_bombero", true);
                } else {
                    paramt.put("es_bombero", false);
                }

                paramt.put("es_turismo", permisosFuncionamiento.getTurismo());
                paramt.put("aprobado", false);
                paramt.put("primera_vez", permisosFuncionamiento.getPrimeraVez());

                if (permisosFuncionamiento.getHt().getTipoTramite() == null
                        && permisosFuncionamiento.getHt().getTipoTramite().getParametrosDisparadorCollection() == null) {
                    System.out.println("Tipo tramite es nulo o no tiene Parametros para el flujo");
                }
                for (ParametrosDisparador parm : permisosFuncionamiento.getHt().getTipoTramite().getParametrosDisparadorCollection()) {
                    if (parm.getEstado()) {
                        if (parm.getVarInterfaz() != null && parm.getVarInterfaz().trim().length() > 0) {
                            paramt.put(parm.getVarInterfaz(), parm.getInterfaz());
                        }
                        if (parm.getVarResp() != null && parm.getResponsable() != null) {
                            paramt.put(parm.getVarResp(), parm.getResponsable().getUsuario());
                        }
                    }
                }
                ProcessInstance pro = null;
                try {
                    pro = this.startProcessByDefinitionKey(tramite.getCarpeta(), paramt);
                } catch (Exception e) {
                }
                if (pro != null) {
//                    paramt.put("carpeta", permisosFuncionamiento.getHt().getId() + "-" + pro.getId());
                    permisosFuncionamiento.getHt().setCarpetaRep(permisosFuncionamiento.getHt().getId() + "-" + pro.getId());
                    permisosFuncionamiento.getHt().setIdProcesoTemp(pro.getId());
                    permisosFuncionamiento.getHt().setIdProceso(pro.getId());
                    permisosFuncionamiento.getHt().setNumPredio(predio.getNumPredio());
                    Observaciones o = new Observaciones();
                    o.setEstado(Boolean.TRUE);
                    o.setFecCre(new Date());
//                    o.setIdTramite(permisosFuncionamiento.getHt());
                    o.setTarea("Ingreso de Tramite - Municipio");
                    o.setObservacion(obs);
                    o.setUserCre(session.getName_user());
                    try {

                        if (services.updateHtAndPermisoFunc(null, permisosFuncionamiento, o)) {
                            JsfUti.executeJS("PF('obs').hide()");
                            JsfUti.executeJS("PF('dlgIdLiquidacion').show()");
                            JsfUti.update("numTra:dlgIdLiquidacion");
                            JsfUti.update("numTra:dlgDilLiq");
                        } else {
                            JsfUti.messageError(null, "Advertencia", "Ocurrio un error al intentar Guardar.");
                        }
                    } catch (Exception e) {
                        LOG.log(Level.OFF, "Guardar Observacion y actualizar Ht", e);
                    }
                }
            } else {
                JsfUti.messageError(null, "Advertencia", "Ocurrio un error al intentar Guardar.");
            }

        } catch (Exception e) {
            LOG.log(Level.OFF, "Registrar Tramite Permiso Funcionamiento " + contribuyente.getCiRuc(), e);
        }
    }

    public void mostrarDetalles(RenLocalComercial local) {
        this.local = local;
    }

    public void cerrar() {
        JsfUti.redirectFaces("/vistaprocesos/permisosFuncionamiento/ingresoPermisoLocales.xhtml");
    }

    public List<RenLocalUbicacion> getUbicacionesLocal() {
        return services.getUbicaciones();
    }

    public List<RenTipoLocalComercial> getTipoLocal() {
        return services.getTipoLocals();
    }

    public List<RenLocalCategoria> getCategorias() {
        return services.getCategorias();
    }

    public CatEnte getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(CatEnte contribuyente) {
        this.contribuyente = contribuyente;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public RenLocalComercial getLocal() {
        return local;
    }

    public void setLocal(RenLocalComercial local) {
        this.local = local;
    }

    public String getCiRuc() {
        return ciRuc;
    }

    public void setCiRuc(String ciRuc) {
        this.ciRuc = ciRuc;
    }

    public RenPermisosFuncionamientoLocalComercial getPermisosFuncionamiento() {
        return permisosFuncionamiento;
    }

    public void setPermisosFuncionamiento(RenPermisosFuncionamientoLocalComercial permisosFuncionamiento) {
        this.permisosFuncionamiento = permisosFuncionamiento;
    }

    public RenClaseLocal getClaseLocal() {
        return claseLocal;
    }

    public void setClaseLocal(RenClaseLocal claseLocal) {
        this.claseLocal = claseLocal;
    }

    public RenAfiliacionCamaraProduccion getAfiliacionCamara() {
        return afiliacionCamara;
    }

    public void setAfiliacionCamara(RenAfiliacionCamaraProduccion afiliacionCamara) {
        this.afiliacionCamara = afiliacionCamara;
    }

    public List<RenLocalComercial> getLocalesContribuyente() {
        return localesContribuyente;
    }

    public void setLocalesContribuyente(List<RenLocalComercial> localesContribuyente) {
        this.localesContribuyente = localesContribuyente;
    }

    public Integer getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(Integer tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public List<RenTipoLiquidacion> getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(List<RenTipoLiquidacion> tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getCiRucContador() {
        return ciRucContador;
    }

    public void setCiRucContador(String ciRucContador) {
        this.ciRucContador = ciRucContador;
    }

    /**
     * Creates a new instance of IngresoPermisoLocales
     */
    public IngresoPermisoLocales() {
    }

    public CatPredio getPredioSel() {
        return predioSel;
    }

    public void setPredioSel(CatPredio predioSel) {
        this.predioSel = predioSel;
    }

    public RenLocalComercialLazy getLocalesComercialesLazy() {
        return localesComercialesLazy;
    }

    public void setLocalesComercialesLazy(RenLocalComercialLazy localesComercialesLazy) {
        this.localesComercialesLazy = localesComercialesLazy;
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

    public List<RenActividadComercial> getActividadesComercialesList() {
        return actividadesComercialesList;
    }

    public void setActividadesComercialesList(List<RenActividadComercial> actividadesComercialesList) {
        this.actividadesComercialesList = actividadesComercialesList;
    }

    public List<RenActividadComercial> getActividadesSeleccionadas() {
        return actividadesSeleccionadas;
    }

    public void setActividadesSeleccionadas(List<RenActividadComercial> actividadesSeleccionadas) {
        this.actividadesSeleccionadas = actividadesSeleccionadas;
    }

    public RenActivosLocalComercial getActivos() {
        return activos;
    }

    public void setActivos(RenActivosLocalComercial activos) {
        this.activos = activos;
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

    public RenBalanceLocalComercial getBalance() {
        return balance;
    }

    public void setBalance(RenBalanceLocalComercial balance) {
        this.balance = balance;
    }

    public Integer getMaxAnio() {
        return maxAnio;
    }

    public void setMaxAnio(Integer maxAnio) {
        this.maxAnio = maxAnio;
    }

    public Integer getMaxAnioAct() {
        return maxAnioAct;
    }

    public void setMaxAnioAct(Integer maxAnioAct) {
        this.maxAnioAct = maxAnioAct;
    }

    public Integer getMinAnio() {
        return minAnio;
    }

    public void setMinAnio(Integer minAnio) {
        this.minAnio = minAnio;
    }

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(BigDecimal baseImponible) {
        this.baseImponible = baseImponible;
    }

}
