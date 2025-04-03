package com.origami.sgm.managedbeans.catastro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.catastro.GestionPredios;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AvalBandaImpositiva;
import com.origami.sgm.entities.AvalDetCobroImpuestoPredios;
import com.origami.sgm.entities.AvalImpuestoPredios;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.models.PrediosManzanaDTO;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.services.interfaces.catastro.AvaluosServices;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.Utils;

@Named
@ViewScoped
public class AvaluosPrediales
        implements Serializable {

    @Inject
    private Entitymanager manager;
    @Inject
    private UserSession session;
    @Inject
    protected ServletSession ss;
    @Inject
    protected CatastroServices catas;
    @Inject
    private AvaluosServices avaluosServices;
    private CatPredioLazy predios;
    private List<CatPredio> prediosSeleccionados;
    private Integer anioEmisionFin;
    private Integer anioEmisionInicio;
    private Integer anioTotlesEmision;
    private Integer anioEmisionSeleccionadaPredio;
    private AvalBandaImpositiva avalBandaImpositivaSeleccionada;
    private CatPredio predioSeleccionado;
    private AvalImpuestoPredios avalImpuestoPredios;
    private static final Logger logx = Logger.getLogger(AvaluosPrediales.class.getName());

    private AvalBandaImpositiva avalBandaImpositiva;
    private List<AvalBandaImpositiva> avalBandaImpositivaList;
    private List<PrediosManzanaDTO> prediosXManzana;
    private Boolean controlPredeterminada = Boolean.TRUE;
    private String tipoDefinicion = "M";
    private List<AvalImpuestoPredios> impuestoPrediosList;
    private List<Short> zonaList;
    private List<Short> mzList;
    private List<Short> sectorList;
    private List<Short> lotesList;
    private Short zona = null;
    private Short mz = null;
    private Short sector = null;
    private Short lotes = null;
    protected CatParroquia parroquia;
    private List<RenRubrosLiquidacion> rubrosList;
    private List<RenRubrosLiquidacion> rubrosSeleccionados;
    private Map<String, Object> parametros;

    ////VERIFICA QUE LA FECHA ESTE DENTRO DEL 1 DE DICIEMBRE Y EL 5 DE ENERO
    private Date habilitarEmisionTotal;
    private Boolean habilitarBotonEmision;

    public AvaluosPrediales() {
    }

    @PostConstruct
    protected void init() {
        zonaList = new ArrayList();
        mzList = new ArrayList();
        sectorList = new ArrayList();
        prediosSeleccionados = new ArrayList();
        avalBandaImpositivaList = manager.findAll(Querys.getBandaImpositivaActivas);
        predios = new CatPredioLazy(true);
        prediosXManzana = manager.getSqlQueryParametros(PrediosManzanaDTO.class, Querys.getPrediosXManzana, new String[]{"estado"}, new Object[]{"A"});
        avalBandaImpositiva = new AvalBandaImpositiva();
        avalBandaImpositivaSeleccionada = new AvalBandaImpositiva();
        avalImpuestoPredios = new AvalImpuestoPredios();
        impuestoPrediosList = manager.findAll(Querys.getAvalImpuestoPrediosActivo);
        validatePredeterminada(false, null);
        rubrosList = manager.findAll(QuerysFinanciero.getRubrosByTipoLiquidacionCodRubroASC, new String[]{"tipoLiq"}, new Object[]{13L});
        rubrosSeleccionados = new ArrayList();
        habilitarEmisionTotal();
    }

    public void habilitarEmisionTotal() {
        habilitarEmisionTotal = new Date();

        if ((Utils.getMes(habilitarEmisionTotal) == 12 || Utils.getMes(habilitarEmisionTotal) == 1)
                && Utils.getDia(habilitarEmisionTotal) > 1) {
            habilitarBotonEmision = Boolean.TRUE;
        } else {
            habilitarBotonEmision = Boolean.FALSE;
        }
    }

    public void saveAvalBandaImpositiva(Boolean control) {
        if (avalBandaImpositiva != null) {
            if ((avalBandaImpositiva.getAnioFin() != null) || (avalBandaImpositiva.getAnioInicio() != null) || (avalBandaImpositiva.getMultiploImpuestoPredial() != null)) {

                if (control) {
                    avalBandaImpositiva.setPredeterminada(Boolean.TRUE);
                }
                AvalBandaImpositiva bandaImpositiva = avaluosServices.saveOrUpdateAvalBandaImpositiva(avalBandaImpositiva);
                if (bandaImpositiva != null) {
                    mensajeCorrecto();
                    avalBandaImpositiva = new AvalBandaImpositiva();
                    avalBandaImpositivaList.add(bandaImpositiva);
                    if (control) {
                        saveBandaImpositivaPorParroquiaZonaSectorMz(bandaImpositiva);
                    }
                    validatePredeterminada(false, null);
                } else {
                    mensajeError();
                }

            } else {
                mensajeAdvertencia();
            }
        } else {
            mensajeAdvertencia();
        }
    }

    public Boolean validateAnio(Integer anioInicio, Integer anioFin) {
        Boolean result = Boolean.TRUE;
        if (anioInicio != 0
                && anioInicio <= Utils.getAnio(new Date()) && anioInicio >= 2000
                && anioFin != 0
                && anioFin >= Utils.getAnio(new Date()) && anioFin >= 2000) {
        } else {
            mensajeAdvertencia();
            return Boolean.FALSE;
        }
        return result;
    }

    public void saveBandaImpositivaPorParroquiaZonaSectorMz(AvalBandaImpositiva bandaImpositiva) {
        AvalImpuestoPredios aipTemp = avaluosServices.saveAvalImpuestoPredios(avalImpuestoPredios);
    }

    public void reportePredioUsuario() {
        try {

            ss.borrarDatos();
            ss.instanciarParametros();
            ss.setNombreSubCarpeta("recaudaciones");
            ss.setTieneDatasource(true);
            ss.setFondoBlanco(Boolean.TRUE);
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/") + "reportes/recaudaciones/");
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            ss.agregarParametro("TIPO_LIQUIDACION", 13L);
            ss.agregarParametro("ANIO", anioTotlesEmision);
            ss.agregarParametro("USUARIO", session.getName_user());
            ss.agregarParametro("LOGO", path + SisVars.sisLogo);
            ss.setNombreReporte("sReporteEmisionValorInicial");
            Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

        } catch (NumberFormatException e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void reportePredioTitulosPrediales() {
        try {

            ss.borrarDatos();
            ss.instanciarParametros();
            ss.setNombreSubCarpeta("recaudaciones");
            ss.setTieneDatasource(true);
            ss.setFondoBlanco(Boolean.TRUE);
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/") + "reportes/recaudaciones/");
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            ss.agregarParametro("TIPO_LIQUIDACION", 13L);
            ss.agregarParametro("ANIO", anioEmisionInicio);
            ss.agregarParametro("USUARIO", session.getName_user());
            ss.agregarParametro("LOGO", path + SisVars.sisLogo);
            ss.setNombreReporte("sReporteEmisionTitulos");
            Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

        } catch (NumberFormatException e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void validatePredeterminada(Boolean control, AvalBandaImpositiva impositiva) {
        if ((avalBandaImpositivaList != null) && (!avalBandaImpositivaList.isEmpty())) {
            for (AvalBandaImpositiva abi : avalBandaImpositivaList) {
                if (abi.getPredeterminada() == Boolean.TRUE) {
                    controlPredeterminada = Boolean.FALSE;
                    if ((control == Boolean.TRUE) && (impositiva != null)
                            && (!abi.equals(impositiva))) {
                        abi.setPredeterminada(Boolean.FALSE);
                        avaluosServices.saveOrUpdateAvalBandaImpositiva(abi);
                    }
                } else {
                    controlPredeterminada = Boolean.TRUE;
                }
            }
        }
    }

    public void definirPredeterminadaBandaImpositiva(AvalBandaImpositiva impositiva, Boolean predeterminada) {
        if (predeterminada == Boolean.TRUE) {
            impositiva.setPredeterminada(Boolean.TRUE);
        } else {
            impositiva.setPredeterminada(Boolean.FALSE);
        }
        impositiva = avaluosServices.saveOrUpdateAvalBandaImpositiva(impositiva);

        validatePredeterminada(Boolean.valueOf(true), impositiva);

        setValuesBandaImpositivaSetAvalImpuestoPredio(null, Boolean.FALSE, impositiva);

        definirBaseImpositivaPorParroquiaSectorMz(impositiva);
    }

    public void setValuesBandaImpositivaSetAvalImpuestoPredio(PrediosManzanaDTO pmdto, Boolean control, AvalBandaImpositiva bandaImposi) {
        avalImpuestoPredios = new AvalImpuestoPredios();
        if (Objects.equals(control, Boolean.TRUE)) {
            avalImpuestoPredios.setParroquia(pmdto.getParroquia().shortValue());
            avalImpuestoPredios.setSector(pmdto.getSector().shortValue());
            avalImpuestoPredios.setZona(pmdto.getZona().shortValue());
            avalImpuestoPredios.setMz(pmdto.getMz().shortValue());
        } else {
            avalImpuestoPredios.setBandaImpositiva(bandaImposi);
            avalImpuestoPredios.setAnioFin(bandaImposi.getAnioFin());
            avalImpuestoPredios.setAnioInicio(bandaImposi.getAnioInicio());
            avalImpuestoPredios.setParroquia((short) -1);
            avalImpuestoPredios.setSector((short) -1);
            avalImpuestoPredios.setZona((short) -1);
            avalImpuestoPredios.setMz((short) -1);
        }
    }

    public void definirBaseImpositivaPorParroquiaSectorMz(AvalBandaImpositiva banda) {
        if (banda != null) {
            avalImpuestoPredios.setBandaImpositiva(banda);
        } else {
            avalImpuestoPredios.setBandaImpositiva(avalBandaImpositivaSeleccionada);
        }
        avalImpuestoPredios.setEstado("A");
        avalImpuestoPredios.setSolar((short) -1);
        switch (tipoDefinicion) {
            case "P":
                avalImpuestoPredios.setZona((short) -1);
                avalImpuestoPredios.setSector((short) -1);
                avalImpuestoPredios.setMz((short) -1);
                break;
            case "Z":
                avalImpuestoPredios.setSector((short) -1);
                avalImpuestoPredios.setMz((short) -1);
                break;
            case "S":
                avalImpuestoPredios.setMz((short) -1);
                break;
        }

        AvalImpuestoPredios aipResult = avaluosServices.saveAvalImpuestoPredios(avalImpuestoPredios);
        if (aipResult != null) {
            mensajeCorrecto();
        } else {
            mensajeError();
        }
    }

    public void saveTaxPaymentProperty() {
        if (avalBandaImpositivaSeleccionada != null) {
            List<AvalDetCobroImpuestoPredios> detCobroImpuestoPredios = null;
            if (avalBandaImpositivaSeleccionada.getId() != null) {
                avalImpuestoPredios.setBandaImpositiva(avalBandaImpositivaSeleccionada);
                avalImpuestoPredios.setEstado("A");
                validateSelection();

                switch (tipoDefinicion) {
                    case "":
                        avalImpuestoPredios.setParroquia((short) -1);
                        avalImpuestoPredios.setZona((short) -1);
                        avalImpuestoPredios.setSector((short) -1);
                        avalImpuestoPredios.setMz((short) -1);
                        avalImpuestoPredios.setSolar((short) -1);
                        break;
                    case "P":
                        avalImpuestoPredios.setParroquia(parroquia.getCodigoParroquia().shortValue());
                        avalImpuestoPredios.setZona((short) -1);
                        avalImpuestoPredios.setSector((short) -1);
                        avalImpuestoPredios.setMz((short) -1);
                        avalImpuestoPredios.setSolar((short) -1);
                        break;
                    case "Z":
                        avalImpuestoPredios.setParroquia(parroquia.getCodigoParroquia().shortValue());
                        avalImpuestoPredios.setZona(zona.shortValue());
                        avalImpuestoPredios.setSector((short) -1);
                        avalImpuestoPredios.setMz((short) -1);
                        avalImpuestoPredios.setSolar((short) -1);
                        break;
                    case "S":
                        avalImpuestoPredios.setParroquia(parroquia.getCodigoParroquia().shortValue());
                        avalImpuestoPredios.setZona(zona.shortValue());
                        avalImpuestoPredios.setSector(sector.shortValue());
                        avalImpuestoPredios.setMz((short) -1);
                        avalImpuestoPredios.setSolar((short) -1);
                        break;
                    case "M":
                        avalImpuestoPredios.setParroquia(parroquia.getCodigoParroquia().shortValue());
                        avalImpuestoPredios.setZona(zona.shortValue());
                        avalImpuestoPredios.setSector(sector.shortValue());
                        avalImpuestoPredios.setMz(mz.shortValue());
                        avalImpuestoPredios.setSolar((short) -1);
                        break;
                    case "L":
                        avalImpuestoPredios.setParroquia(parroquia.getCodigoParroquia().shortValue());
                        avalImpuestoPredios.setZona(zona.shortValue());
                        avalImpuestoPredios.setSector(sector.shortValue());
                        avalImpuestoPredios.setMz(mz.shortValue());
                        avalImpuestoPredios.setSolar(lotes.shortValue());
                        break;
                }

                if (rubrosSeleccionados != null) {
                    if (!rubrosSeleccionados.isEmpty()) {
                        AvalDetCobroImpuestoPredios cobroImpuestoPredios = new AvalDetCobroImpuestoPredios();
                        detCobroImpuestoPredios = new ArrayList();
                        for (RenRubrosLiquidacion rubrosLiquidacion : rubrosSeleccionados) {
                            cobroImpuestoPredios = new AvalDetCobroImpuestoPredios();
                            cobroImpuestoPredios.setIdRubroCobrar(rubrosLiquidacion);
                            detCobroImpuestoPredios.add(cobroImpuestoPredios);
                        }
                        AvalImpuestoPredios aipResult = avaluosServices.saveAvalImpuestoPrediosAndDetCobro(avalImpuestoPredios, detCobroImpuestoPredios);
                        if (aipResult != null) {
                            rubrosSeleccionados = new ArrayList();
                            avalBandaImpositivaSeleccionada = new AvalBandaImpositiva();
                            mensajeCorrecto();
                        } else {
                            mensajeAdvertencia();
                        }
                    } else {
                        mensajeAdvertencia();
                    }
                }
            }
        } else {
            mensajeAdvertencia();
        }
    }

    public void cleansVars() {
        zonaList = new ArrayList();
        sectorList = new ArrayList();
        mzList = new ArrayList();
        zona = null;
        sector = null;
        mz = null;
    }

    public void validateSelection() {
        if (parroquia != null) {
            if ((parroquia.getCodigoParroquia() != null) && (zona == null) && (sector == null) && (mz == null) && (lotes == null)) {
                tipoDefinicion = "P";
            } else {
                if ((parroquia.getCodigoParroquia() != null) && (zona != null) && (sector == null) && (mz == null) && (lotes == null)) {
                    tipoDefinicion = "Z";
                }

                if ((parroquia.getCodigoParroquia() != null) && (zona != null) && (sector != null) && (mz == null) && (lotes == null)) {
                    tipoDefinicion = "S";
                }

                if ((parroquia.getCodigoParroquia() != null) && (zona != null) && (sector != null) && (mz != null) && (lotes == null)) {
                    tipoDefinicion = "M";
                }

                if ((parroquia.getCodigoParroquia() != null) && (zona != null) && (sector != null) && (mz != null) && (lotes != null)) {
                    tipoDefinicion = "L";
                }
            }
        } else {
            tipoDefinicion = "";
        }
    }

    public void inactivaBandaImpositviva(AvalBandaImpositiva impositiva) {
        if (impositiva.getPredeterminada() == Boolean.TRUE) {
            Faces.messageFatal(null, "Error !", "Las Bases impositivas predeterminadas no se pueden Eliminar");
            return;
        }
        impositiva.setEstado("I");
        avaluosServices.saveOrUpdateAvalBandaImpositiva(impositiva);
        avalBandaImpositivaList.remove(impositiva);
    }

    public void inactivaImpuestoPredial(AvalImpuestoPredios impuestoPredios) {
        impuestoPredios.setEstado("I");
        avaluosServices.saveAvalImpuestoPredios(impuestoPredios);
        impuestoPrediosList.remove(impuestoPredios);
    }

    public CatPredio validarEmisionExitente(List<CatPredio> cps, Boolean control) {
        CatPredio cp = null;
        try {
            for (CatPredio predio : cps) {
                cp = new CatPredio();
                parametros = new HashMap();
                parametros.put("idPredio", predio.getId());
                parametros.put("anio", anioEmisionInicio);

                RenLiquidacion emision = (RenLiquidacion) manager.findObjectByParameter(Querys.emisionExistente, parametros);
                if (emision != null) {
                    cp = predio;
                    break;
                }
            }

            return cp;
        } catch (Exception e) {
            Logger.getLogger(EmisionesPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
        return cp;
    }

    public void startEmisionPredial(Boolean control) {
        Boolean poseeDeuda = Boolean.FALSE;
        for (CatPredio cp : prediosSeleccionados) {
            if (poseeDeudas(cp)) {
                Faces.messageFatal(null, "Predio Posee Emisiones Al Año Actual", "");
                poseeDeuda = Boolean.TRUE;
            }
            if (poseePagos(cp)) {
                Faces.messageFatal(null, "Predio Posee Emisiones Al Año Actual", "");
                poseeDeuda = Boolean.TRUE;
            }
        }
        if (!poseeDeuda) {
            Object o = avaluosServices.generateEmisionPredial(prediosSeleccionados, anioEmisionSeleccionadaPredio, anioEmisionSeleccionadaPredio, control, session.getName_user());
            mensajeCorrecto();
        }

        if (control == Boolean.FALSE) {
        }
    }

    public void bajaPredios() {
        try {
            RenLiquidacion liquidacion = null;
            for (CatPredio cp : prediosSeleccionados) {
                liquidacion = (RenLiquidacion) manager.find(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionYEstado, new String[]{"predio", "anio", "idTipoLiquidacion", "estadoLiquidacion"}, new Object[]{cp,
                    Utils.getAnio(new Date()), new Long(13L), new Long(2L)});
                if (liquidacion != null) {
                    liquidacion.setEstadoLiquidacion((RenEstadoLiquidacion) manager.find(RenEstadoLiquidacion.class, Long.valueOf(4L)));
                    manager.persist(liquidacion);
                }
            }
            mensajeCorrecto();
        } catch (Exception e) {
            mensajeError();
        }
    }

    public Boolean poseeDeudas(CatPredio cp) {
        RenLiquidacion liq = catas.getDeudasPredioAnioActual(cp, anioEmisionSeleccionadaPredio);
        if (liq != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean poseePagos(CatPredio cp) {
        RenLiquidacion liq = catas.getPagadasPredioAnioActual(cp);
        if (liq != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public List<CatParroquia> getParroquias() {
        Map<String, Object> paramt = new HashMap();
        paramt.put("idCanton", manager.find(Querys.getParroquiasByCanton, new String[]{"codigoNacional", "codNac"}, new Object[]{SisVars.CANTON, SisVars.PROVINCIA}));
        return manager.findObjectByParameterOrderList(CatParroquia.class, paramt, new String[]{"idCanton"}, Boolean.valueOf(true));
    }

    public void loadZonas() {
        zonaList = new ArrayList();
        sectorList = new ArrayList();
        mzList = new ArrayList();
        lotesList = new ArrayList();
        lotes = null;
        zona = null;
        sector = null;
        mz = null;

        if ((parroquia != null)
                && (parroquia.getCodigoParroquia() != null)) {
            zonaList = manager.findAll(Querys.getZonasByParroquia, new String[]{"parroquia"}, new Object[]{Short.valueOf(parroquia.getCodigoParroquia().shortValue())});
            Collections.sort(zonaList);
        }
    }

    public void loadSectorByZonas() {
        sectorList = new ArrayList();
        mzList = new ArrayList();
        sector = null;
        mz = null;
        lotes = null;
        lotesList = new ArrayList();
        if (zona != null) {
            sectorList = manager.findAll(Querys.getSectorByZonas, new String[]{"parroquia", "zona"}, new Object[]{Short.valueOf(parroquia.getCodigoParroquia().shortValue()), zona});
            Collections.sort(sectorList);
        }
    }

    public void loadMzBySector() {
        mz = null;
        mzList = new ArrayList();
        lotes = null;
        lotesList = new ArrayList();
        if (sector != null) {
            mzList = manager.findAll(Querys.getMzBySector, new String[]{"parroquia", "zona", "sector"}, new Object[]{Short.valueOf(parroquia.getCodigoParroquia().shortValue()), zona, sector});
            Collections.sort(mzList);
        }
    }

    public void loadLotesByMz() {
        lotesList = new ArrayList();
        lotes = null;
        if (mz != null) {
            lotesList = manager.findAll(Querys.getLotesByMz, new String[]{"parroquia", "zona", "sector", "mz"}, new Object[]{Short.valueOf(parroquia.getCodigoParroquia().shortValue()), zona, sector, mz});
            Collections.sort(lotesList);
        } else {
            Faces.messageWarning(null, "Advertencia!", "La parroquia seleccionada no tiene definido su codigo.");
        }
    }

    public void mensajeCorrecto() {
        Faces.messageInfo(null, "Éxito !", "Datos Registrados Correctamente");
    }

    public void mensajeError() {
        Faces.messageFatal(null, "Error !", "Ocurrio un error mientras se persistian los datos");
    }

    public void mensajeAdvertencia() {
        Faces.messageFatal(null, "Advertencia !", "Verifique que los datos esten seleccionados Correctamente");
    }

    public void emitirAvaluo() {
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public CatPredioLazy getPredios() {
        return predios;
    }

    public void setPredios(CatPredioLazy predios) {
        this.predios = predios;
    }

    public CatPredio getPredioSeleccionado() {
        return predioSeleccionado;
    }

    public void setPredioSeleccionado(CatPredio predioSeleccionado) {
        this.predioSeleccionado = predioSeleccionado;
    }

    public List<CatPredio> getPrediosSeleccionados() {
        return prediosSeleccionados;
    }

    public void setPrediosSeleccionados(List<CatPredio> prediosSeleccionados) {
        this.prediosSeleccionados = prediosSeleccionados;
    }

    public Integer getAnioEmisionFin() {
        anioEmisionFin = Utils.getAnio(new Date());
        return anioEmisionFin;
    }

    public void setAnioEmisionFin(Integer anioEmisionFin) {
        this.anioEmisionFin = anioEmisionFin;
    }

    public Integer getAnioEmisionInicio() {
        anioEmisionInicio = Utils.getAnio(new Date());
        return anioEmisionInicio;
    }

    public void setAnioEmisionInicio(Integer anioEmisionInicio) {
        this.anioEmisionInicio = anioEmisionInicio;
    }

    public AvalBandaImpositiva getAvalBandaImpositiva() {
        avalBandaImpositiva.setEstado("A");
        return avalBandaImpositiva;
    }

    public void setAvalBandaImpositiva(AvalBandaImpositiva avalBandaImpositiva) {
        this.avalBandaImpositiva = avalBandaImpositiva;
    }

    public List<AvalBandaImpositiva> getAvalBandaImpositivaList() {
        return avalBandaImpositivaList;
    }

    public void setAvalBandaImpositivaList(List<AvalBandaImpositiva> avalBandaImpositivaList) {
        this.avalBandaImpositivaList = avalBandaImpositivaList;
    }

    public List<PrediosManzanaDTO> getPrediosXManzana() {
        return prediosXManzana;
    }

    public void setPrediosXManzana(List<PrediosManzanaDTO> prediosXManzana) {
        this.prediosXManzana = prediosXManzana;
    }

    public Boolean getControlPredeterminada() {
        return controlPredeterminada;
    }

    public void setControlPredeterminada(Boolean controlPredeterminada) {
        this.controlPredeterminada = controlPredeterminada;
    }

    public AvalBandaImpositiva getAvalBandaImpositivaSeleccionada() {
        return avalBandaImpositivaSeleccionada;
    }

    public void setAvalBandaImpositivaSeleccionada(AvalBandaImpositiva avalBandaImpositivaSeleccionada) {
        this.avalBandaImpositivaSeleccionada = avalBandaImpositivaSeleccionada;
    }

    public AvalImpuestoPredios getAvalImpuestoPredios() {
        avalImpuestoPredios.setAnioFin(Utils.getAnio(new Date()));
        avalImpuestoPredios.setAnioInicio(Utils.getAnio(new Date()));
        return avalImpuestoPredios;
    }

    public void setAvalImpuestoPredios(AvalImpuestoPredios avalImpuestoPredios) {
        this.avalImpuestoPredios = avalImpuestoPredios;
    }

    public String getTipoDefinicion() {
        return tipoDefinicion;
    }

    public void setTipoDefinicion(String tipoDefinicion) {
        this.tipoDefinicion = tipoDefinicion;
    }

    public List<AvalImpuestoPredios> getImpuestoPrediosList() {
        return impuestoPrediosList;
    }

    public void setImpuestoPrediosList(List<AvalImpuestoPredios> impuestoPrediosList) {
        this.impuestoPrediosList = impuestoPrediosList;
    }

    public List<Short> getZonaList() {
        return zonaList;
    }

    public void setZonaList(List<Short> zonaList) {
        this.zonaList = zonaList;
    }

    public List<Short> getMzList() {
        return mzList;
    }

    public void setMzList(List<Short> mzList) {
        this.mzList = mzList;
    }

    public List<Short> getSectorList() {
        return sectorList;
    }

    public void setSectorList(List<Short> sectorList) {
        this.sectorList = sectorList;
    }

    public Short getZona() {
        return zona;
    }

    public void setZona(Short zona) {
        this.zona = zona;
    }

    public Short getMz() {
        return mz;
    }

    public void setMz(Short mz) {
        this.mz = mz;
    }

    public Short getSector() {
        return sector;
    }

    public void setSector(Short sector) {
        this.sector = sector;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public List<Short> getLotesList() {
        return lotesList;
    }

    public void setLotesList(List<Short> lotesList) {
        this.lotesList = lotesList;
    }

    public Short getLotes() {
        return lotes;
    }

    public void setLotes(Short lotes) {
        this.lotes = lotes;
    }

    public List<RenRubrosLiquidacion> getRubrosList() {
        return rubrosList;
    }

    public void setRubrosList(List<RenRubrosLiquidacion> rubrosList) {
        this.rubrosList = rubrosList;
    }

    public List<RenRubrosLiquidacion> getRubrosSeleccionados() {
        return rubrosSeleccionados;
    }

    public void setRubrosSeleccionados(List<RenRubrosLiquidacion> rubrosSeleccionados) {
        this.rubrosSeleccionados = rubrosSeleccionados;
    }

    public Date getHabilitarEmisionTotal() {
        return habilitarEmisionTotal;
    }

    public void setHabilitarEmisionTotal(Date habilitarEmisionTotal) {
        this.habilitarEmisionTotal = habilitarEmisionTotal;
    }

    public Boolean getHabilitarBotonEmision() {
        return habilitarBotonEmision;
    }

    public void setHabilitarBotonEmision(Boolean habilitarBotonEmision) {
        this.habilitarBotonEmision = habilitarBotonEmision;
    }

    public Integer getAnioEmisionSeleccionadaPredio() {
        anioEmisionSeleccionadaPredio = Utils.getAnio(new Date());
        return anioEmisionSeleccionadaPredio;
    }

    public void setAnioEmisionSeleccionadaPredio(Integer anioEmisionSeleccionadaPredio) {
        this.anioEmisionSeleccionadaPredio = anioEmisionSeleccionadaPredio;
    }

    public Integer getAnioTotlesEmision() {
        return anioTotlesEmision;
    }

    public void setAnioTotlesEmision(Integer anioTotlesEmision) {
        this.anioTotlesEmision = anioTotlesEmision;
    }

    
    
}
