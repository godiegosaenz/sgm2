package com.origami.sgm.bpm.managedbeans.rentas.liquidaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CtlgSalario;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenActivosLocalComercial;
import com.origami.sgm.entities.RenBalanceLocalComercial;
import com.origami.sgm.entities.RenDesvalorizacion;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenFactorPorCapital;
import com.origami.sgm.entities.RenFactorPorMetro;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenLocalCantidadAccesorios;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTasaTurismo;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.lazymodels.RenLocalComercialLazy;
import com.origami.sgm.managedbeans.ClienteTramite;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import util.EntityBeanCopy;
import util.Faces;
import util.GroovyUtil;
import util.JsfUti;
import util.Messages;
import util.MessagesRentas;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class LocalesComerciales extends ClienteTramite implements Serializable {

    private static final Logger LOG = Logger.getLogger(LocalesComerciales.class.getName());

    @javax.inject.Inject
    private RentasServices services;

    @javax.inject.Inject
    private Entitymanager servs;

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;

    @Inject
    ServletSession ss;

    @javax.inject.Inject
    protected com.origami.sgm.services.ejbs.ServiceLists lists;

    @Inject
    private UserSession session;

    private Integer tipo = 1;
    private Integer tipoCons = 2;
    private Integer maxAnio;
    private Integer maxAnioAct;
    private Integer minAnio = 1990;
    private Boolean esPersonaProp = true;
    private Boolean esLocal = false, variosRotulos = false;
    private BigDecimal valor;

    private CatEnte propietario;
    private RenLiquidacion liquidacion;
    private RenTipoLiquidacion tipoLiquidacion;
    private List<RenTipoLiquidacion> tiposLiquidacions;
    private List<RenRubrosLiquidacion> rubrosLiquidacion;
    private RenDesvalorizacion desvalorizacion;

    private Boolean mostrarRequisitos;
    private String cedulaRuc;
    private List<RenLocalComercial> localesEnte;
    private RenLocalComercialLazy localesLazy;
    private RenLocalComercial localSel;
    private RenBalanceLocalComercial balance;
    private List<RenDetLiquidacion> detalle;

    // Variables para locales comerciales
    private RenActivosLocalComercial local;
    // Fin variables locales comerciales

    // Inicio Variables para caculos de valores 
    private MatFormulaTramite mft;
    private GroovyUtil util;
    // Fin Variables formula

//    @PostConstruct
//    public void initView() {
//        iniciarDatos();
//    }
    public void initView() {
        if (!JsfUti.isAjaxRequest()) {
            iniciarDatos();
        }
    }

    public void iniciarDatos() {
        tiposLiquidacions = services.gettiposLiquidacionByCodTitRep(2);

        initLiquidacion();
        consultarRubros();
        localesLazy = new RenLocalComercialLazy();
        maxAnio = Utils.getAnio(new Date()) - 1;
        maxAnioAct = Utils.getAnio(new Date());
    }

    public void selectLocal(RenLocalComercial local) {
        try {
            if (localesEnte == null) {
                localesEnte = new ArrayList<>();
            }
            if (!localesEnte.contains(local)) {
                localesEnte.add(local);
            } else {
                JsfUti.messageInfo(null, "Info", "No se puede agregar dos veces el mismo local comercial");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void generarValorLiquidacion() {
        try {
            RenFactorPorMetro factor1;
            RenFactorPorCapital factor2;
            BigDecimal valor;

            switch (this.tipoLiquidacion.getCodigoTituloReporte().intValue()) {
                case 53:  // Tasa Bomberos
                    for (RenRubrosLiquidacion temp : rubrosLiquidacion) {
                        if (temp.getCodigoRubro() == 1) {
                            detalle.get(0).setValor(liquidacion.getTotalPago());
                        }
                    }
                    break;
                case 11: // Activos Totales

                    break;
                case 14: // Patentes Comerciales el calculo de la patentes sse las hace sobre el 1 % dde l capita segun lo establece la ley  pilas pilas
                    /*
                        Art. 551.El valor al que se hace referencia en el primer inciso será el uno por ciento (1%) sobre el capital social o patrimonio de las sociedades, atendiendo
                        su naturaleza jurídica. Este monto constituirá exclusivamente un anticipo del impuesto a las patentes municipales, y su liquidación final la realizará
                        cada municipalidad, conforme a las ordenanzas respectivas
                     */
                    if (balance.getCapital() != null && balance.getCapital().compareTo(BigDecimal.ZERO) > 0) {
                        if (localSel != null) {
                            localSel.getRenActividadComercialCollection().size();
                            factor2 = (RenFactorPorCapital) servs.find(QuerysFinanciero.getValorBasePatenteComercial, new String[]{"capital"}, new Object[]{balance.getCapital()});
                            valor = balance.getCapital().multiply(new BigDecimal("0.01"));// util.getExpression("patenteComercial", new Object[]{balance.getCapital(), factor2, localSel});
                            System.out.println("valor" + valor);
                            liquidacion.setTotalPago(valor);
                            for (RenRubrosLiquidacion temp : rubrosLiquidacion) {
                                System.out.println("rubrosLiquidacion" + temp.getDescripcion());
                                if (temp.getCodigoRubro() == 1) {
                                    detalle.get(0).setValor(liquidacion.getTotalPago());
                                }
                            }
                        }
                    }
                    break;
                case 15: // Tasa de Habilitacion
                    factor1 = (RenFactorPorMetro) servs.find(QuerysFinanciero.getValorBaseTasaHabilitacion, new String[]{"metros"}, new Object[]{localSel.getArea()});
                    liquidacion.setTotalPago((BigDecimal) util.getExpression("tasaHabilitacion", new Object[]{factor1.getValor(), localSel, factor1.getFraccion()}));
                    for (RenRubrosLiquidacion temp : rubrosLiquidacion) {
                        if (temp.getCodigoRubro() == 1) {
                            detalle.get(0).setValor(liquidacion.getTotalPago());
                        }
                    }
                    break;
                case 98: // Turismo
                    List<RenLocalCantidadAccesorios> accesorioses = services.getAcesorios(localSel);
                    if (accesorioses == null) {
                        JsfUti.messageError(null, "Error", "Local no tiene ingresado la cantidad de Mesas, Habitaciones, o Plaza");
                        return;
                    }
                    if (localSel.getRenActividadComercialCollection() == null) {
                        JsfUti.messageError(null, "Advertencia", "Local no tiene ninguna actividad registrada");
                        return;
                    }
                    List<RenTasaTurismo> tasaTurismo = services.getTasasTurismo(localSel);
                    valor = (BigDecimal) util.getExpression("getTasaTurismo",
                            new Object[]{tasaTurismo, accesorioses});
                    if (valor != null) {
                        liquidacion.setTotalPago(valor);
                        for (RenDetLiquidacion rb : detalle) {
                            rb.setCobrar(true);
                            rb.setValor(valor);
                        }
                    }
                    if (tasaTurismo != null && !tasaTurismo.isEmpty()) {
                        this.valor = tasaTurismo.get(0).getValor();
                    } else {
                        JsfUti.messageError(null, "Error", "Local no tiene actividad turistica");
                        return;
                    }
                    liquidacion.setTotalPago(valor);
                    JsfUti.update("frmAlcPlus:dtLocal");
                    break;
                case 206: // Vallas Publicitarias
                    CtlgSalario salario = services.getSalarioBasico(liquidacion.getAnio());
                    if (salario != null) {
                        valor = (BigDecimal) util.getExpression("vallasPublicitarias",
                                new Object[]{liquidacion, salario.getValor()});
                        if (valor != null) {
                            liquidacion.setTotalPago(valor);
                            for (RenDetLiquidacion rb : detalle) {
                                rb.setCobrar(true);
                                rb.setValor(valor);
                            }
                            liquidacion.setTotalPago(valor);
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "No existe salario basico registrado para el año (" + liquidacion.getAnio() + ")");
                    }
                    JsfUti.update("frmAlcPlus:dtLocal");
                    break;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void buscarEnte() {
        if (cedulaRuc == null || cedulaRuc.trim().length() == 0) {
            JsfUti.messageError(null, "Advertencia", "Debe Ingresar el Numero de Cedula o RUC");
            return;
        }
        mostrarRequisitos = false;
        propietario = (CatEnte) servs.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        if (propietario != null) {
            mostrarRequisitos = true;
            localesEnte = propietario.getLocalesComercialesCollection();
            esPersonaProp = propietario.getEsPersona();
        } else {
            mostrarRequisitos = false;
            propietario = null;
            tipoLiquidacion = null;
            persona = new CatEnte();
            persona.setEsPersona(cedulaRuc.length() == 10);
            persona.setCiRuc(cedulaRuc);
            JsfUti.messageInfo(null, Messages.enteNoExiste, "");
            JsfUti.update("formSolicitante");
            JsfUti.executeJS("PF('dlgSolInf').show();");
        }

    }

    public void guardarSolicitante() {
        Boolean flag;
        if (persona.getId() == null) {
            flag = guardarCliente();
        } else {
            flag = editarCliente();
        }
        if (flag) {
            propietario = persona;
            JsfUti.executeJS("PF('dlgSolInf').hide();");
            JsfUti.messageInfo(null, "Info", "Se creó el usuario correctamente.");
        } else {
            JsfUti.messageError(null, "Error", "No se pudo guardar los datos correctamente. Modifique los datos e intente de nuevo.");
        }
    }

    public void cancelarGuardado() {
        emailNew = "";
        tlfnNew = "";
        persona = new CatEnte();
    }

    public void consultarRubros() {
        try {
            rubrosLiquidacion = new ArrayList<>();
            detalle = new ArrayList();
            if (tipoLiquidacion != null) {
                rubrosLiquidacion = services.getRubrosPorLiquidacion(tipoLiquidacion.getId());
                if (tipoLiquidacion.getPrefijo() != null) {
//                    mft = services.getMatFormulaByPrefijo(tipoLiquidacion.getPrefijo());
                    mft = services.getMatFormulaByPrefijo("ACT");
                    if (mft != null) {
                        util = new GroovyUtil(mft.getFormula());
                    } else {
                        JsfUti.messageError(null, "Advertencia", "No se encontro formulas de calculos para este tipo de liquidacion " + tipoLiquidacion.getPrefijo());
                    }
                }
                initLiquidacion();
                if (tipoLiquidacion.getCodigoTituloReporte().equals(11L)) {
                    local = new RenActivosLocalComercial();
                    local.setFechaIngreso(new Date());
                    local.setAnioBalance(maxAnio);
                    esLocal = true;

                    detalle.add(0, new RenDetLiquidacion(BigDecimal.ZERO, null, "Valor de Activos"));
                    detalle.add(1, new RenDetLiquidacion(BigDecimal.ZERO, null, "Valor de Pasivos"));
                    detalle.add(2, new RenDetLiquidacion(BigDecimal.ZERO, null, "Diferencia de Activos vs Pasivos"));
                    detalle.add(3, new RenDetLiquidacion(BigDecimal.ZERO, null, "Base Imponible para el Cálculo"));
                }
                if (tipoLiquidacion.getCodigoTituloReporte().equals(14L)) {
                    balance = new RenBalanceLocalComercial();
                }
                if (tipoLiquidacion.getCodigoTituloReporte() == 206) {
                    liquidacion.setCodigoLocal("rotulo");
                }

                for (RenRubrosLiquidacion temp : rubrosLiquidacion) {
                    detalle.add(new RenDetLiquidacion(temp.getValor(), temp.getId(), temp.getDescripcion()));
                }

                liquidacion.setAreaTotal(BigDecimal.ZERO);
            }
            if (propietario != null) {
                esPersonaProp = propietario.getEsPersona();
            }
//            cambiarPrefijo();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Cargar Rubros", e);
        }
        JsfUti.update("frmAlcPlus");
    }

    public void initLiquidacion() {
        liquidacion = new RenLiquidacion();
        liquidacion.setTotalPago(new BigDecimal(0));
        liquidacion.setFechaContratoAnt(new Date());
        liquidacion.setCostoAdq(BigDecimal.ZERO);
        liquidacion.setCuantia(BigDecimal.ZERO);
        liquidacion.setAnio(Utils.getAnio(new Date()));
        localSel = null;
    }

    public void buscarLocal() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "95%");
        options.put("height", "85%");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/localesComerciales", options, null);
    }

    public void seleccionarLocal(SelectEvent event) {
        try {
            selectLocal((RenLocalComercial) event.getObject());
            seleccionar((RenLocalComercial) event.getObject());
            esPersonaProp = propietario.getEsPersona();
            if (tipoLiquidacion.getCodigoTituloReporte() == 98) {
//                if (!this.localSel.getTurismo()) {
//                    JsfUti.messageError(null, "Advertencia", "Local comercial seleccionado no tiene categoria de Turismo");
//                    return;
//                }
            }
            JsfUti.update("frmAlcPlus");
        } catch (Exception e) {
            LOG.log(Level.OFF, "Seleccionar Local", e);
        }
    }

    public void seleccionar(RenLocalComercial local) {
        try {
            this.localSel = local;
            this.liquidacion.setLocalComercial(local);
            this.generarValorLiquidacion();
            JsfUti.messageInfo(null, "Info", "Local comercial seleccionado correctamente");
        } catch (Exception e) {
            LOG.log(Level.OFF, "Seleccionar Local", e);
        }
    }

    public void procesar() {
        try {
            if (localSel == null) {
                JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.faltaIngresar.concat("local comercial"));
                return;
            }
            if (localSel.getId() == null) {
                JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.faltaIngresar.concat("local comercial"));
                return;
            }
//            if (liquidacion.getTotalPago().compareTo(BigDecimal.ZERO) == 0) {
//                JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.total);
//                return;
//            }
            Object numLiquidacion = null;
            liquidacion.setComprador(getPropietario());
            liquidacion.setFechaIngreso(new Date());
            liquidacion.setUsuarioIngreso(session.getName_user());
            if (liquidacion.getEstadoLiquidacion() == null) {
                liquidacion.setEstadoLiquidacion(services.getEstadoLiquidacionByDesc(2L));
            }
            liquidacion.setSaldo(liquidacion.getTotalPago());
            liquidacion.setTipoLiquidacion(tipoLiquidacion);
            liquidacion.setLocalComercial(localSel);
            //cambiarPrefijo();
            if (local != null) {
                local.setLocalComercial(localSel);
            }
            if (balance != null) {
                balance.setLocalComercial(localSel);
            }
            liquidacion = services.guardarLiquidacionYRubros(liquidacion, detalle, tipoLiquidacion, local, balance, null, null);
            //
            if (liquidacion != null) {

                JsfUti.executeJS("PF('obs').hide()");
                JsfUti.executeJS("PF('dlgIdLiquidacion').show()");
                JsfUti.update("numLiq:dlgIdLiquidacion");
                imprimir();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Guardar Liquidacion LC", e);
        }
    }

    private void imprimir() {
        String path = Faces.getRealPath("//");
        ss.instanciarParametros();
        ss.setTieneDatasource(Boolean.TRUE);
        ss.agregarParametro("LOGO", path.concat(SisVars.logoReportes));
        ss.agregarParametro("LIQUIDACION", liquidacion.getId());
        ss.agregarParametro("NUM_TRAMITE", liquidacion.getNumReporte());
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

    public void cambiarPrefijo() {
        if (tipoLiquidacion != null) {
            switch (tipoLiquidacion.getCodigoTituloReporte().intValue()) {
                case 53: // Tasa Bomberos
                    tipoLiquidacion.setPrefijo("TSB");
                    break;
                case 11: // Activos Totales
                    tipoLiquidacion.setPrefijo("MIL");
                    break;
                case 14: // Patentes Comerciales
//                    tipoLiquidacion.setPrefijo("MIL");
                    break;
                case 15: // Tasa de Habilitacion
                    tipoLiquidacion.setPrefijo("THA");
                    break;
                case 98: // Turismo
                    tipoLiquidacion.setPrefijo("TUR");
                    break;
            }
        }
    }

    public void borrar() {
        iniciarDatos();
        localSel = null;
        esPersonaProp = true;
        localesEnte = null;
        propietario = null;
        tipoLiquidacion = null;
        cedulaRuc = null;
    }

    //***** Inicio Locales Comerciales *****//  
//    private final AlcabalasFm calc = new AlcabalasFm();
    public void sumarActivos() {
        if (local.getActivoTotal() == null) {
            local.setActivoTotal(BigDecimal.ZERO);
        }
        if (local.getActivoContingente() == null) {
            local.setActivoContingente(BigDecimal.ZERO);
        }
        detalle.get(0).setValor(
                (BigDecimal) util.getExpression("sumarActivos", new Object[]{local}));
//        if (local.getPorcentajeIngreso().compareTo(BigDecimal.ZERO) > 0) {
        impuesto();
//        }
    }

    public void sumarPasivos() {
        if (local.getPasivoTotal() == null) {
            local.setPasivoTotal(BigDecimal.ZERO);
        }
        if (local.getPasivoContingente() == null) {
            local.setPasivoContingente(BigDecimal.ZERO);
        }
        detalle.get(1).setValor(
                (BigDecimal) util.getExpression("sumarPasivos", new Object[]{local}));
        detalle.get(2).setValor(
                (BigDecimal) util.getExpression("diferenciaActivosVsPasivos", new Object[]{local}));
        impuesto();
    }

    public void modificarTotal() {
        try {
            switch (tipoLiquidacion.getCodigoTituloReporte().intValue()) {
                case 11:
                    detalle.get(4).setValor(liquidacion.getTotalPago());
                    break;
                default:
                    detalle.get(0).setValor(liquidacion.getTotalPago());
                    break;
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, "", e);
        }
    }

    public void impuesto() {
        BigDecimal base = (BigDecimal) util.getExpression("baseImponible", new Object[]{local});
        BigDecimal total = (BigDecimal) util.getExpression("impuesto", new Object[]{local});
//        System.out.println("Valor Total " + total.signum());
        if (total.signum() == -1) {
            total = BigDecimal.ZERO;
            liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(5L));
        }
        detalle.get(3).setValor(base);
        liquidacion.setTotalPago(total);
        detalle.get(4).setValor(liquidacion.getTotalPago());
//        //rubrosLiquidacion.get(4).setCobrar(true);
        detalle.get(5).setValor(BigDecimal.ZERO);
    }
    //***** Fin Locales Comerciales *****//    

    public List<RenActividadComercial> getActividades() {
        return lists.getActividadComercials();
    }

    public List<RenActividadComercial> getActividadesLocal() {
        List l = null;
        if (localSel != null) {
            l = (List) EntityBeanCopy.clone(localSel.getRenActividadComercialCollection());
        } else {
            l = null;
        }
        return l;
    }

    public void validar() {
        if (tipoLiquidacion.getCodigoTituloReporte().equals(11L)) {
            if (!local.getAnioBalance().equals(liquidacion.getAnio())) {
                JsfUti.messageError(null, "Año Liquidacion", "El año de la liquidacion debe ser igual al año del balance.");
                return;
            }
        }
        liquidacion.setLocalComercial(localSel);
        liquidacion.setTipoLiquidacion(tipoLiquidacion);
        Integer existeLiquidacion = null;
        if (!variosRotulos) {
            existeLiquidacion = services.existeLiquidacion(liquidacion);
        } else {
            existeLiquidacion = 3;
        }
        if (existeLiquidacion == 3) {
            JsfUti.executeJS("PF('obs').show()");
        } else if (existeLiquidacion == 2) {
            JsfUti.messageError(null, "Liquidación", "Ya existe una Liquidación activa de " + tipoLiquidacion.getNombreTitulo() + "para el local seleccionado");
        } else {
            JsfUti.messageError(null, "Liquidación", "Liquidación de " + tipoLiquidacion.getNombreTitulo() + "para el local seleccionado, ya fue pagado.");
        }
    }

    public void seleccionarComprador(SelectEvent event) {
        CatEnte repLagTem = (CatEnte) EntityBeanCopy.clone(event.getObject());
        persona.setRepresentanteLegal(BigInteger.valueOf(repLagTem.getId()));

        persona.setNombresCompletos(repLagTem.getNombreCompleto());
    }

    public LocalesComerciales() {
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Integer getTipoCons() {
        return tipoCons;
    }

    public void setTipoCons(Integer tipoCons) {
        this.tipoCons = tipoCons;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public List<RenRubrosLiquidacion> getRubrosLiquidacion() {
        return rubrosLiquidacion;
    }

    public void setRubrosLiquidacion(List<RenRubrosLiquidacion> rubrosLiquidacion) {
        this.rubrosLiquidacion = rubrosLiquidacion;
    }

    public RentasServices getServices() {
        return services;
    }

    public void setServices(RentasServices services) {
        this.services = services;
    }

    public CatEnte getPropietario() {
        return propietario;
    }

    public void setPropietario(CatEnte propietario) {
        this.propietario = propietario;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public RenTipoLiquidacion getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(RenTipoLiquidacion tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public List<RenTipoLiquidacion> getTiposLiquidacions() {
        return tiposLiquidacions;
    }

    public void setTiposLiquidacions(List<RenTipoLiquidacion> tiposLiquidacions) {
        this.tiposLiquidacions = tiposLiquidacions;
    }

    public RenDesvalorizacion getDesvalorizacion() {
        return desvalorizacion;
    }

    public void setDesvalorizacion(RenDesvalorizacion desvalorizacion) {
        this.desvalorizacion = desvalorizacion;
    }

    public RenActivosLocalComercial getLocal() {
        return local;
    }

    public void setLocal(RenActivosLocalComercial local) {
        this.local = local;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public List<RenLocalComercial> getLocalesEnte() {
        return localesEnte;
    }

    public void setLocalesEnte(List<RenLocalComercial> localesEnte) {
        this.localesEnte = localesEnte;
    }

    public RenLocalComercialLazy getLocalesLazy() {
        return localesLazy;
    }

    public void setLocalesLazy(RenLocalComercialLazy localesLazy) {
        this.localesLazy = localesLazy;
    }

    public RenLocalComercial getLocalSel() {
        return localSel;
    }

    public void setLocalSel(RenLocalComercial localSel) {
        this.localSel = localSel;
    }

    public Boolean getEsPersonaProp() {
        return esPersonaProp;
    }

    public void setEsPersonaProp(Boolean esPersonaProp) {
        this.esPersonaProp = esPersonaProp;
    }

    public RenBalanceLocalComercial getBalance() {
        return balance;
    }

    public void setBalance(RenBalanceLocalComercial balance) {
        this.balance = balance;
    }

    public List<RenDetLiquidacion> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<RenDetLiquidacion> detalle) {
        this.detalle = detalle;
    }

    public Integer getMaxAnio() {
        return maxAnio;
    }

    public void setMaxAnio(Integer maxAnio) {
        this.maxAnio = maxAnio;
    }

    public Integer getMinAnio() {
        return minAnio;
    }

    public void setMinAnio(Integer minAnio) {
        this.minAnio = minAnio;
    }

    public Integer getMaxAnioAct() {
        return maxAnioAct;
    }

    public void setMaxAnioAct(Integer maxAnioAct) {
        this.maxAnioAct = maxAnioAct;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getEsLocal() {
        return esLocal;
    }

    public void setEsLocal(Boolean esLocal) {
        this.esLocal = esLocal;
    }

    public Boolean getVariosRotulos() {
        return variosRotulos;
    }

    public void setVariosRotulos(Boolean variosRotulos) {
        this.variosRotulos = variosRotulos;
    }

}
