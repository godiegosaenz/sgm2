package com.origami.sgm.bpm.managedbeans.rentas.liquidaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.RenActivosLocalComercial;
import com.origami.sgm.entities.RenDesvalorizacion;
import com.origami.sgm.entities.RenDetallePlusvalia;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.RenValoresPlusvalia;
import com.origami.sgm.managedbeans.component.Busquedas;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import util.Faces;
import util.GroovyUtil;
import util.JsfUti;
import util.MessagesRentas;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class PlusvaliaAlcabalas extends Busquedas implements Serializable {

    private static final Logger LOG = Logger.getLogger(PlusvaliaAlcabalas.class.getName());

    @javax.inject.Inject
    private RentasServices services;

    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;

    @Inject
    private UserSession session;
    @Inject
    private ServletSession ss;
    @Inject
    private Entitymanager manager;
    @Inject
    protected CatastroServices catas;

    private Integer tipo = 1;
    private Integer tipoCons = 2;
    private Boolean esMatriz = false;
    private Boolean seccion1 = false;
    private Boolean seccion2 = false;
    private Boolean exonerar = false;
    private Boolean alcabala = false;

    private CatPredio predio;
    private CatPredioPropietario vendedor, pro;
    private RenLiquidacion liquidacion;
    private RenTipoLiquidacion tipoLiquidacion;
    private List<RenTipoLiquidacion> tiposLiquidacions;
    private List<RenRubrosLiquidacion> rubrosLiquidacion;
    private RenValoresPlusvalia valoresCalc;
    private RenDesvalorizacion desvalorizacion;
    private List<RenDetallePlusvalia> detallePlusvalias;

    // Variables para locales comerciales
    private RenActivosLocalComercial local;
    // Fin variables locales comerciales

    // Inicio Variables para caculos de valores 
    private MatFormulaTramite mft;
    private GroovyUtil util;
    // Fin Variables formula

    //Inicio del tipo de predio que se va a liquidar
    /*
        PDC :PREDIOS DEL CANTON
        
     */
    private Boolean claveOtroCanton = Boolean.FALSE;
    private CatPredioModel predioModel;
    protected AclUser usr;
    //FIN DE ES ESO :V 

    @PostConstruct
    public void initView() {
        iniciarDatos();
    }

    public void iniciarDatos() {
        try {
            tiposLiquidacions = services.gettiposLiquidacionByCodTitRep(1);
            predio = new CatPredio();
            initLiquidacion();
            consultarRubros();
            valoresCalc = new RenValoresPlusvalia();
            vendedor = new CatPredioPropietario();
            predioModel = new CatPredioModel();
            predioModel.setProvincia(SisVars.PROVINCIA);
            predioModel.setCanton(SisVars.CANTON);
            usr = (AclUser) manager.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{session.getName_user()});
        } catch (Exception e) {
            LOG.log(Level.OFF, "Iniciar vista", e);
        }
    }

    public void consultarRubros() {
        rubrosLiquidacion = new ArrayList<>();
        if (tipoLiquidacion != null) {
            rubrosLiquidacion = services.getRubrosPorLiquidacion(tipoLiquidacion.getId());
            if (tipoLiquidacion.getPrefijo() != null) {
                mft = services.getMatFormulaByPrefijo(tipoLiquidacion.getPrefijo());
                if (mft != null) {
                    util = new GroovyUtil(mft.getFormula());
                } else {
                    JsfUti.messageError(null, "Error", "No se encontrado formulas de calculos para " + tipoLiquidacion.getNombreTransaccion());
                }
            }
            initLiquidacion();
            valoresCalc = new RenValoresPlusvalia();
            if (tipoLiquidacion.getPrefijo().equalsIgnoreCase("PLU")) {
                if (mft != null) {
                    obtenerFaccionAnio();
                }
                alcabala = false;
            } else if (tipoLiquidacion.getPrefijo().equalsIgnoreCase("ALC")) {
                alcabala = true;
            }

        }
    }

    public void initLiquidacion() {
        liquidacion = new RenLiquidacion();
        liquidacion.setTotalPago(new BigDecimal(0));
        liquidacion.setFechaContratoAnt(new Date());
        liquidacion.setCostoAdq(BigDecimal.ZERO);
        liquidacion.setCuantia(BigDecimal.ZERO);
    }

    @Override
    public void seleccionarPredios(SelectEvent event) {
        predios = (List<CatPredio>) event.getObject();
        detallePlusvalias = new ArrayList<>();
        for (CatPredio pred : predios) {
            RenDetallePlusvalia rdp = new RenDetallePlusvalia();
            rdp.setPrediosAsociados(pred);
            detallePlusvalias.add(rdp);
        }
        setEsPersonaComp(getComprador().getEsPersona());
    }

    public void consultar() {

        if (tipoLiquidacion == null) {
            JsfUti.messageError(null, "Advertencia", "Debe seleccionar el tipo de liquidación a realizar");
            return;
        }
        try {
            CatPredio temp = consultar(tipoCons, predio);

            if (temp != null) {
                predio = temp;
                esMatriz = predio.getPhh() != 0 && predio.getPhv() != 0;
                if (predio.getCatPredioPropietarioCollection() != null && predio.getCatPredioPropietarioCollection().size() == 1) {
                    vendedor = Utils.get(predio.getCatPredioPropietarioCollection(), 0);
                }
                seccion1 = true;
                claveOtroCanton = Boolean.FALSE;
            } else {
                seccion1 = false;
                JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.predioNoEncontrado);
            }
            JsfUti.update("frmAlcPlus");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
    }

    public String getNombreVendedor(CatEnte ente) {
        if (ente != null) {
            if (ente.getEsPersona()) {
                return Utils.isEmpty(ente.getApellidos()) + " " + Utils.isEmpty(ente.getNombres());
            } else {
                return Utils.isEmpty(ente.getRazonSocial());
            }
        }
        return "";
    }

    public void valorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (RenRubrosLiquidacion rb : rubrosLiquidacion) {
            if (rb.getCobrar()) {
                if (rb.getTipoValor().getId() == 1L) {
                    total = total.add(rb.getValor());
                } else {
                    if (rb.getValorTotal() != null) {
                        total = total.add(rb.getValorTotal());
                    }

                }
            }
        }
        liquidacion.setTotalPago(total.setScale(2, RoundingMode.HALF_UP));
    }

    public void procesar() {
        if (!claveOtroCanton) {
            if (vendedor == null) {
                JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.faltaIngresar.concat("nombre del Vendedor"
                        + ", debe seleccionar uno de la tabla de propietario"));
                return;
            }
            if (vendedor.getEnte() == null) {
                JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.faltaIngresar.concat("nombre del Vendedor"));
                return;
            }
        }

        if (this.getComprador() == null) {
            JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.faltaIngresar.concat("nombre del comprador"));
            return;
        }
        if (getComprador().getId() == null) {
            JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.faltaIngresar.concat("nombre del comprador"));
            return;
        }
        if (liquidacion.getCuantia() == null) {
            JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.cuantia);
            return;
        }
        if (liquidacion.getTotalPago().doubleValue() <= 0) {
            JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.total);
            return;
        }

        try {
            Object numLiquidacion;
            /*
                SI EL PREDIO AL QUE SE ESTA GENERANDO LAA ALCABALA Y LA PLUSVALIA PERTENECE AL MUNICIPIO
                CASO CONTRARIO SE ELIJE Y SE REGISTRA EL PREDIO DE OTRO CANTON CON UN TIPO DE PREDIO QUE NO ES URBANO 
                EL TIPO DE PREDIO SERA 'O' (OTRO)
             */
            if (!claveOtroCanton) {
                liquidacion.setComprador(vendedor.getEnte());
                liquidacion.setVendedor(getComprador());
                liquidacion.setPredio(predio);
            } else {
                liquidacion.setComprador(getComprador());
                liquidacion.setVendedor(getVendedorPredioOtroCanton());
                liquidacion.setPredio(savePredioOtroCanton());
            }

            liquidacion.setFechaIngreso(new Date());
            liquidacion.setUsuarioIngreso(session.getName_user());
            liquidacion.setEstadoLiquidacion(services.getEstadoLiquidacionByDesc(2L));
            liquidacion.setTipoLiquidacion(tipoLiquidacion);
            liquidacion.setCoactiva(false);
            if (desvalorizacion != null) {
                valoresCalc.setDesvalorizacion((desvalorizacion.getId() == null ? null : desvalorizacion));
            }
            if (alcabala) {
                detallePlusvalias = null;
            }
            valoresCalc.setRenDetallePlusvaliaCollection(detallePlusvalias);
            final String prefijo = tipoLiquidacion.getPrefijo();
            liquidacion = services.guardarLiquidacion(liquidacion, rubrosLiquidacion, prefijo, valoresCalc);

            // GUARDA EN EL SAC
            /*if (tipoLiquidacion.getCodigoTituloReporte() == 95) {
                numLiquidacion = permisoServices.grabaRTLiquidacionSac(95L, 0L, "LIQUIDA_PLUSVALIA",
                        liquidacion.getPredio() != null ? liquidacion.getPredio().getId() : 0L, Utils.getAnio(new Date()), 0L,
                        new Timestamp(new Date().getTime()), liquidacion.getTotalPago(), liquidacion.getTipoLiquidacion().getPrefijo(), session.getName_user(), false);

                permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(95L, liquidacion.getTotalPago(), session.getName_user(), liquidacion.getTipoLiquidacion().getPrefijo() + "-" + this.generadorCeroALaIzquierda((Long) numLiquidacion), (Long) numLiquidacion, Utils.getAnio(new Date()), 1);
            } else {
                numLiquidacion = permisoServices.grabaRTLiquidacionSac(93L, 0L, "LIQUIDA_ALCABALA",
                        liquidacion.getPredio() != null ? liquidacion.getPredio().getId() : 0L, Utils.getAnio(new Date()), 0L,
                        new Timestamp(new Date().getTime()), liquidacion.getTotalPago(), liquidacion.getTipoLiquidacion().getPrefijo(), session.getName_user(), false);

                permisoServices.guardarRT_RUBROS_X_LIQUIDACION_SAC_Gen(93L, liquidacion.getTotalPago(), session.getName_user(), liquidacion.getTipoLiquidacion().getPrefijo() + "-" + this.generadorCeroALaIzquierda((Long) numLiquidacion), (Long) numLiquidacion, Utils.getAnio(new Date()), 1);
            }
            if (numLiquidacion != null) {
                liquidacion.setNumLiquidacion(BigInteger.valueOf((Long) numLiquidacion));
                liquidacion.setIdLiquidacion(liquidacion.getTipoLiquidacion().getPrefijo() + "-" + numLiquidacion.toString());
            }*/
            if (liquidacion != null) {
                if (services.generarNumLiquidacion(liquidacion, prefijo)) {
                    JsfUti.executeJS("PF('dlgIdLiquidacion').show()");
                    JsfUti.executeJS("PF('obs').hide()");
                    JsfUti.update("numLiquidacion:dlgDilLiq");
                } else {
                    JsfUti.messageError(null, "Error", "Ocurrio un error al intentar guardar.");
                }
            } else {
                JsfUti.messageError(null, "Error", "Ocurrio un error al intentar guardar.");
                JsfUti.executeJS("PF('obs').hide()");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public CatPredio savePredioOtroCanton() {
        CatPredio px = null;
        try {
            px = new CatPredio();
            px.setProvincia(this.predioModel.getProvincia());
            px.setCanton(this.predioModel.getCanton());
            px.setParroquia(this.predioModel.getParroquiaShort());
            px.setZona(this.predioModel.getZona());
            px.setSector(this.predioModel.getSector());
            px.setMz(this.predioModel.getMz());
            px.setSolar(this.predioModel.getLote());
            px.setLote(this.predioModel.getLote());
            px.setBloque(this.predioModel.getBloque());
            px.setPiso(this.predioModel.getPiso());
            px.setUnidad(this.predioModel.getUnidad());
            px.setUsuarioCreador(usr);
            px.setInstCreacion(new Date());
            px.setEstado("A");
            //PREDIO DE OTRO CANTON 
            px.setTipoPredio("O");
            px.setCdla(new Short("0"));
            px.setMzdiv(new Short("0"));
            px.setDiv1(new Short("0"));
            px.setDiv2(new Short("0"));
            px.setDiv3(new Short("0"));
            px.setDiv4(new Short("0"));
            px.setDiv5(new Short("0"));
            px.setDiv6(new Short("0"));
            px.setDiv7(new Short("0"));
            px.setDiv8(new Short("0"));
            px.setDiv9(new Short("0"));
            px.setPhv(new Short("0"));
            px.setPhh(new Short("0"));
            px.setPropiedadHorizontal(false);

            px.setClaveCat(claveCatastral(predioModel));
            px = catas.guardarPredio(px);
            if (px != null && px.getId() != null) {
                if (px.getNumPredio() != null || predio.getNumPredio().compareTo(BigInteger.ZERO) <= 0) {
                    px = catas.generarNumPredio(px);
                }
            }
            if (px != null) {
                pro = new CatPredioPropietario();
                pro.setPredio(px);
                pro.setEnte(getVendedorPredioOtroCanton());
                pro.setEsResidente(false);
                pro.setUsuario(session.getName_user());
                pro.setEstado("A");
                pro.setFecha(new Date());
                pro = catas.guardarPropietario(pro, session.getName_user());
                return px;
            }
        } catch (NumberFormatException e) {
        }
        return px;
    }

    public void validarClaveOtroCanton() {
        predio = new CatPredio();
        if (tipoLiquidacion == null) {
            JsfUti.messageError(null, "Advertencia", "Debe seleccionar el tipo de liquidación a realizar");
            return;
        }
        try {
            if (predioModel.getProvincia() > 0 && predioModel.getCanton() > 0
                    && predioModel.getParroquiaShort() > 0 && predioModel.getZona() > 0 && predioModel.getSector() > 0
                    && predioModel.getMz() > 0 && predioModel.getLote() > 0) {
                seccion1 = false;
                claveOtroCanton = Boolean.TRUE;
            } else {
                JsfUti.messageWarning(null, "Advertencia", "Verifique los datos ingresados");
            }

            JsfUti.update("frmAlcPlus");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }

    }

    public void borrarDatos() {
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.instanciarParametros();
        ss.setNombreDocumento(liquidacion.getIdLiquidacion());
        ss.setNombreSubCarpeta("rentas/liquidaciones");
        ss.setTieneDatasource(true);
        ss.agregarParametro("LIQUIDACION", liquidacion.getId());
        ss.agregarParametro("LOGO", path + SisVars.sisLogo);

        if (tipoLiquidacion.getPrefijo().equalsIgnoreCase("PLU")) {
            ss.setNombreReporte("plusvalia");
            JsfUti.executeJS("PF('dlgConf').show()");
        } else {
            ss.setNombreReporte("alcabalas");
            iniciarDatos();
            tipoLiquidacion = null;
            tipoCons = 2;
            setComprador(null);
            predios = null;
            predio = new CatPredio();
            seccion1 = false;
            seccion2 = false;
            exonerar = false;
        }//JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void llenarAlcalbala(Boolean continuar) {
        if (continuar) {
            final Boolean p = getComprador().getEsPersona();
            tipoLiquidacion = tiposLiquidacions.get(0);
//            initLiquidacion();
            consultarRubros();
            JsfUti.update("frmAlcPlus");
            alcabala = true;
            setEsPersonaComp(p);
        } else {
            iniciarDatos();
            tipoLiquidacion = null;
            tipoCons = 2;
            setComprador(null);
            predios = null;
            predio = new CatPredio();
            seccion1 = false;
            seccion2 = false;
            exonerar = false;

        }
        JsfUti.update("frmAlcPlus");
    }

    // ****** Inicio Calculos plusvalia ********//
    public void obtenerFaccionAnio() {
        final CatPredioPropietario e = vendedor;
        if (tipoLiquidacion.getPrefijo().equalsIgnoreCase("PLU")) {
            if (liquidacion != null) {
                desvalorizacion = services.getDesvalorizacionAnio(Utils.getAnio(liquidacion.getFechaContratoAnt()));
                restarDiferenc();
                JsfUti.update("frmAlcPlus");
            }
        }
        vendedor = e;
    }

    public void restarDiferenc() {
        try {
            if (util != null) {
                if (liquidacion.getCuantia() == null) {
                    liquidacion.setCuantia(BigDecimal.ZERO);
                }
                if (liquidacion.getCostoAdq() == null) {
                    liquidacion.setCostoAdq(BigDecimal.ZERO);
                }
                valoresCalc.setDiferenciaNeta((BigDecimal) util.getExpression("restarDiferenc", new Object[]{liquidacion}));
                obtenerDifNet2();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }

    public void obtenerDifNet2() {
        if (util != null) {
            valoresCalc.setDiferenciaNeta2((BigDecimal) util.getExpression("obtenerDifNet2", new Object[]{valoresCalc}));
            rebajaGen();
        }
    }

    public void rebajaGen() {
        if (util != null) {
            int porcAnio = (int) util.getExpression("anios", new Object[]{liquidacion.getFechaContratoAnt()});
            if (porcAnio >= 100) {
                valoresCalc.setPorcentajeRebaja(100);
                baseDesv();
                for (RenRubrosLiquidacion rb : rubrosLiquidacion) {
                    if (rb.getTipoValor().getId() == 1L) {
                        rb.setCobrar(Boolean.TRUE);
                        liquidacion.setTotalPago(rb.getValor());
                        rb.setValorTotal(rb.getValor());
                    }
                }
            } else {
                valoresCalc.setRebajaGen((BigDecimal) util.getExpression("rebajaGeneral", new Object[]{valoresCalc, liquidacion.getFechaContratoAnt()}));
                valoresCalc.setPorcentajeRebaja(porcAnio);
                baseDesv();
                for (RenRubrosLiquidacion r : rubrosLiquidacion) {
                    r.setCobrar(Boolean.FALSE);
                    r.setValorTotal(BigDecimal.ZERO);
                    if (r.getTipoValor().getId() == 1L) {
                        if (valoresCalc.getDiferenciaNeta().compareTo(BigDecimal.ZERO) <= 0 && r.getValorTotal().compareTo(BigDecimal.ZERO) == 0) {
                            r.setCobrar(Boolean.TRUE);
                            liquidacion.setTotalPago(r.getValor());
                            r.setValorTotal(r.getValor());
                        }
                    }
                    if (r.getTipoValor().getId() == 2L) {
                        if (valoresCalc.getDiferenciaNeta().compareTo(BigDecimal.ZERO) > 0) {
                            r.setCobrar(Boolean.TRUE);
                            r.setValorTotal(liquidacion.getTotalPago().setScale(2, RoundingMode.HALF_UP));
                        }
                    }
                    r.getValorTotal().setScale(2, RoundingMode.HALF_UP);
                }
            }
        }
        JsfUti.update("frmAlcPlus");
    }

    public void baseDesv() {
        if (desvalorizacion == null) {
            desvalorizacion = new RenDesvalorizacion();
            desvalorizacion.setAnio(Utils.getAnio(new Date()));
            desvalorizacion.setValor(BigDecimal.ZERO);
        }
        valoresCalc.setDesvalorizacion(desvalorizacion);
        valoresCalc.setBaseDesvalorizacion((BigDecimal) util.getExpression("baseDesvalorizacion", new Object[]{valoresCalc}));
        valoresCalc.setDesvalorizacionMonet((BigDecimal) util.getExpression("DesvalorizavioMonetario", new Object[]{valoresCalc, desvalorizacion}));
        valoresCalc.setUtilidadImponib((BigDecimal) util.getExpression("utilidadImponible", new Object[]{valoresCalc}));
        liquidacion.setTotalPago((BigDecimal) util.getExpression("totalPagar", new Object[]{valoresCalc}));
    }
    //***** Fin Calculos Plusvalia *****//

    //***** Inicio Caculos Alcabalas *****//    
    public void calcularAlcabala() {
        try {
            if (liquidacion.getCuantia() != null) {
                liquidacion.setTotalPago((BigDecimal) util.getExpression("caculoValRubros",
                        new Object[]{valoresCalc, liquidacion, rubrosLiquidacion}));
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }

    }

    //***** Fin Calculos Alcabalas *****//
    public PlusvaliaAlcabalas() {
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

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
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

    public CatPredioPropietario getVendedor() {
        return vendedor;
    }

    public void setVendedor(CatPredioPropietario vendedor) {
        this.vendedor = vendedor;
    }

    public RentasServices getServices() {
        return services;
    }

    public void setServices(RentasServices services) {
        this.services = services;
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

    public RenValoresPlusvalia getValoresCalc() {
        return valoresCalc;
    }

    public void setValoresCalc(RenValoresPlusvalia valoresCalc) {
        this.valoresCalc = valoresCalc;
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

    public Boolean getEsMatriz() {
        return esMatriz;
    }

    public void setEsMatriz(Boolean esMatriz) {
        this.esMatriz = esMatriz;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Boolean getSeccion1() {
        return seccion1;
    }

    public void setSeccion1(Boolean seccion1) {
        this.seccion1 = seccion1;
    }

    public Boolean getSeccion2() {
        return seccion2;
    }

    public void setSeccion2(Boolean seccion2) {
        this.seccion2 = seccion2;
    }

    public List<RenDetallePlusvalia> getDetallePlusvalias() {
        return detallePlusvalias;
    }

    public void setDetallePlusvalias(List<RenDetallePlusvalia> detallePlusvalias) {
        this.detallePlusvalias = detallePlusvalias;
    }

    public Boolean getExonerar() {
        return exonerar;
    }

    public void setExonerar(Boolean exonerar) {
        this.exonerar = exonerar;
    }

    public Boolean getAlcabala() {
        return alcabala;
    }

    public void setAlcabala(Boolean alcabala) {
        this.alcabala = alcabala;
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public Boolean getClaveOtroCanton() {
        return claveOtroCanton;
    }

    public void setClaveOtroCanton(Boolean claveOtroCanton) {
        this.claveOtroCanton = claveOtroCanton;
    }

}
