/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.tesoreria;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.bpm.models.ModelCarteraVencida;
import com.origami.sgm.bpm.models.ModelCarteraVencidaParroquia;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.CtlgCatalogo;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EmisionesRuralesExcel;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.hibernate.HibernateException;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author origami
 */
@Named(value = "informesResoluciones")
@ViewScoped
public class InformesResoluciones implements Serializable {

    public static final Long serialVersionUID = 1L;

    //private static final Logger LOG = Logger.getLogger(InformesResoluciones.class.getName());
    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private RecaudacionesService serv;
    /*AGREGO UNA VAFRIABLE*/
    private Integer tipoReporte;

    @Inject
    private ServletSession ss;

    protected List<CatParroquia> parroquiasRurales;
    protected CtlgCatalogo catalogoTramitesResolucion;
    protected CtlgItem tramiteResolucion;
    protected Long tipoConsultaUrbano = 1L;
    protected CatPredioModel predioModel = new CatPredioModel();
    protected List<CatPredio> prediosUrbanos = new ArrayList<>();
    protected List<CatPredioRustico> prediosRurales = new ArrayList<>();
    protected List<EmisionesRuralesExcel> prediosRurales2017 = new ArrayList<>();
    protected CatPredio predioUrbanoConsulta;
    protected CatPredioRustico predioRuralConsulta;
    protected EmisionesRuralesExcel predioRural2017;
    protected String codigoRural2017;
    protected List<CatPredio> prediosUrbanosConsulta;
    protected String solicitante;
    protected String memorandum;
    protected String memoSolicitante;
    protected String observacion;
    private Map<String, Object> parametros;
    protected List<CatPredio> prediosUrbanosConsultaSeleccionados;

    private BigDecimal axisY = BigDecimal.ZERO;
    private BigDecimal axisX = BigDecimal.ZERO;
    private BarChartModel barModel;

    private PieChartModel pieModel;

    private List<ModelCarteraVencida> modelCartera;
    private List<ModelCarteraVencidaParroquia> modelCarteraParroquia;

    private ModelCarteraVencidaParroquia carteraParroquia;
    private ModelCarteraVencida carteraVencida;

    @PostConstruct
    public void initView() {
        try {
            catalogoTramitesResolucion = manager.find(CtlgCatalogo.class, 23L);
            parroquiasRurales = manager.findAllEntCopy(Querys.parroquiasRurales);
        } catch (Exception e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void consultarPredioUrbano() {
        predioUrbanoConsulta = null;
        parametros = new HashMap<>();
        prediosUrbanosConsultaSeleccionados = null;
        try {
            switch (tipoConsultaUrbano.intValue()) {
                case 1://NUMERO PREDIAL
                    if (predioModel.getNumPredio() != null && predioModel.getNumPredio().compareTo(BigInteger.ZERO) > 0) {
                        parametros.put("numPredio", predioModel.getNumPredio());
                        predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                    }
                    break;
                case 3://CODIGO PREDIAL
                    if (predioModel.getSector() > 0 || predioModel.getMz() > 0 || predioModel.getCdla() > 0 || predioModel.getMzDiv() > 0 || predioModel.getSolar() > 0 || predioModel.getDiv1() > 0 || predioModel.getDiv2() > 0 || predioModel.getDiv3() > 0 || predioModel.getDiv4() > 0 || predioModel.getDiv5() > 0 || predioModel.getDiv6() > 0 || predioModel.getDiv7() > 0 || predioModel.getDiv8() > 0 || predioModel.getDiv9() > 0 || predioModel.getPhv() > 0 || predioModel.getPhh() > 0) {
                        parametros.put("estado", "A");
                        if (predioModel.getSector() > 0) {
                            parametros.put("sector", predioModel.getSector());
                        }
                        if (predioModel.getMz() > 0) {
                            parametros.put("mz", predioModel.getMz());
                        }
                        if (predioModel.getCdla() > 0) {
                            parametros.put("cdla", predioModel.getCdla());
                        }
                        if (predioModel.getMzDiv() > 0) {
                            parametros.put("mzdiv", predioModel.getMzDiv());
                        }
                        if (predioModel.getSolar() > 0) {
                            parametros.put("solar", predioModel.getSolar());
                        }
                        if (predioModel.getDiv1() > 0) {
                            parametros.put("div1", predioModel.getDiv1());
                        }
                        if (predioModel.getDiv2() > 0) {
                            parametros.put("div2", predioModel.getDiv2());
                        }
                        if (predioModel.getDiv3() > 0) {
                            parametros.put("div3", predioModel.getDiv3());
                        }
                        if (predioModel.getDiv4() > 0) {
                            parametros.put("div4", predioModel.getDiv4());
                        }
                        if (predioModel.getDiv5() > 0) {
                            parametros.put("div5", predioModel.getDiv5());
                        }
                        if (predioModel.getDiv6() > 0) {
                            parametros.put("div6", predioModel.getDiv6());
                        }
                        if (predioModel.getDiv7() > 0) {
                            parametros.put("div7", predioModel.getDiv7());
                        }
                        if (predioModel.getDiv8() > 0) {
                            parametros.put("div8", predioModel.getDiv8());
                        }
                        if (predioModel.getDiv9() > 0) {
                            parametros.put("div9", predioModel.getDiv9());
                        }
                        if (predioModel.getPhv() > 0) {
                            parametros.put("phv", predioModel.getPhv());
                        }
                        if (predioModel.getPhh() > 0) {
                            parametros.put("phh", predioModel.getPhh());
                        }
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                    } else {
                        JsfUti.messageError(null, "Error", "Codigo Predial no es valido.");
                    }
                    break;
                case 5:
                    if (predioModel.getCodAnt1() != null && predioModel.getCodAnt2() != null && predioModel.getCodAnt3() != null && predioModel.getCodAnt5() != null && predioModel.getCodAnt6() != null && predioModel.getCodAnt7() != null && predioModel.getCodAnt8() != null) {
                        parametros = new HashMap<>();
                        parametros.put("estado", "A");
                        parametros.put("predialant", predioModel.getCodAnt1() + "-" + predioModel.getCodAnt2() + "-" + predioModel.getCodAnt3() + "-" + predioModel.getCodAnt4() + "-" + predioModel.getCodAnt5() + "-" + predioModel.getCodAnt6() + "-" + predioModel.getCodAnt7() + "-" + predioModel.getCodAnt8());
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                default:
                    break;
            }
            if (prediosUrbanosConsulta != null && !prediosUrbanosConsulta.isEmpty() && prediosUrbanosConsulta.size() == 1) {
                parametros = new HashMap<>();
                parametros.put("numPredio", prediosUrbanosConsulta.get(0).getNumPredio());
                predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
            }
            if (predioUrbanoConsulta != null) {
                if (!this.prediosUrbanos.contains(predioUrbanoConsulta)) {
                    this.prediosUrbanos.add(predioUrbanoConsulta);
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Predio ya se encuentra agregado.");
                }
            } else {
                if (prediosUrbanosConsulta != null && prediosUrbanosConsulta.size() > 1) {
                    JsfUti.update("frmPredios");
                    JsfUti.executeJS("PF('dlgPrediosConsulta').show();");
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void consultarPredioRural() {
        predioRuralConsulta = null;
        parametros = new HashMap<>();
        try {
            if (predioModel.getRegCatastral() != null && predioModel.getIdPredial() != null && predioModel.getParroquia() != null) {
                parametros.put("regCatastral", predioModel.getRegCatastral().trim());
                parametros.put("idPredial", predioModel.getIdPredial());
                parametros.put("parroquia", predioModel.getParroquia());
                predioRuralConsulta = (CatPredioRustico) manager.findObjectByParameter(CatPredioRustico.class, parametros);
            } else {
                JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
            }
            if (predioRuralConsulta != null) {
                if (!this.prediosRurales.contains(predioRuralConsulta)) {
                    this.prediosRurales.add(predioRuralConsulta);
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Predio ya se encuentra agregado.");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void consultarPredioRural2017() {
        predioRuralConsulta = null;
        parametros = new HashMap<>();
        try {
            if (codigoRural2017 != null && codigoRural2017.trim().length() > 0) {
                parametros.put("codigoCatastral", codigoRural2017.trim());
                predioRural2017 = (EmisionesRuralesExcel) manager.findObjectByParameter(EmisionesRuralesExcel.class, parametros);
            } else {
                JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
            }
            if (predioRural2017 != null) {
                if (!this.prediosRurales2017.contains(predioRural2017)) {
                    this.prediosRurales2017.add(predioRural2017);
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Predio ya se encuentra agregado.");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void eliminarPredioUrbano(CatPredio pu) {
        this.prediosUrbanos.remove(pu);
    }

    public void eliminarPredioRural(CatPredioRustico pr) {
        this.prediosRurales.remove(pr);
    }

    public void eliminarPredioRural2017(EmisionesRuralesExcel pr) {
        this.prediosRurales2017.remove(pr);
    }

    public void seleccionarPredio(Long tipoPredio) {
        try {
            parametros = new HashMap<>();
            switch (tipoPredio.intValue()) {
                case 1:
                    if (prediosUrbanosConsultaSeleccionados != null && !prediosUrbanosConsultaSeleccionados.isEmpty()) {
                        for (CatPredio pucs : prediosUrbanosConsultaSeleccionados) {
                            parametros.put("numPredio", pucs.getNumPredio());
                            predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                            if (!this.prediosUrbanos.contains(predioUrbanoConsulta)) {
                                this.prediosUrbanos.add(predioUrbanoConsulta);
                            }
                        }
                        JsfUti.executeJS("PF('dlgPrediosConsulta').hide();");
                    } else {
                        JsfUti.messageInfo(null, "Mensaje", "Seleccione un predio, luego clic en Seleccionar");
                    }
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarInforme(Long tipoInforme) {
        try {
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            List<Long> predios = new ArrayList<>();
            if (tipoInforme.equals(1L)) {
                ss.setNombreReporte("informeResolucion");
                for (CatPredio predioUrbano : prediosUrbanos) {
                    predios.add(predioUrbano.getId());
                }
            } else if (tipoInforme.equals(3L)) {//RURAL 2
                ss.setNombreReporte("informeResolucionRural2017");
                for (EmisionesRuralesExcel predioRural : prediosRurales2017) {
                    predios.add(predioRural.getId());
                }
            } else {//RURAL 2
                ss.setNombreReporte("informeResolucionRural");
                for (CatPredioRustico predioRural : prediosRurales) {
                    predios.add(predioRural.getId());
                }
            }
            if (!predios.isEmpty()) {
                parametros = new HashMap<>();
                parametros.put("departamento", new GeDepartamento(8L));
                parametros.put("isDirector", true);
                AclRol rol = (AclRol) manager.findObjectByParameter(AclRol.class, parametros);
                AclUser director = null;
                for (AclUser u : rol.getAclUserCollection()) {
                    if (u.getUserIsDirector() && u.getSisEnabled()) {
                        director = u;
                        break;
                    }
                }
                ss.agregarParametro("DIR_FINANCIERO", director == null ? "ASIGNAR" : director.getEnte().getTituloProf() + " " + director.getEnte().getNombres() + " " + director.getEnte().getApellidos());
                parametros = new HashMap<>();
                parametros.put("departamento", new GeDepartamento(20L));
                parametros.put("esSubDirector", true);
                rol = (AclRol) manager.findObjectByParameter(AclRol.class, parametros);
                director = null;
                for (AclUser u : rol.getAclUserCollection()) {
                    if (u.getSisEnabled()) {
                        director = u;
                        break;
                    }
                }
                ss.agregarParametro("TESORERA", director == null ? "ASIGNAR" : Utils.isEmpty(director.getEnte().getTituloProf()) + " " + director.getEnte().getNombres() + " " + director.getEnte().getApellidos());
                ss.setNombreSubCarpeta("tesoreria");

                ss.agregarParametro("LOGO", Faces.getRealPath("/").concat(SisVars.logoReportes));
                ss.agregarParametro("LOGO_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
                ss.agregarParametro("NUM_MEMORANDUM", this.memorandum);
                ss.agregarParametro("MEMO", this.memoSolicitante);
                ss.agregarParametro("SOLICITANTE", this.solicitante);
                ss.agregarParametro("OBSERVACION", this.observacion);
                ss.agregarParametro("TRAMITE", this.tramiteResolucion == null ? null : this.tramiteResolucion.getValor());
                ss.agregarParametro("TIPO_PREDIOS", 1L);
                ss.agregarParametro("PREDIOS", (Collection) predios);
                ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/").concat("/"));
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            } else {
                JsfUti.messageInfo(null, "Mensaje", "La lista de Predios no debe estar Vacia");
            }
        } catch (Exception e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void reporteCarteraVencida(Long tipo) {
        try {
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("tesoreria");
            ss.setFondoBlanco(Boolean.FALSE);
            switch (tipo.intValue()) {
                case 1:
                    ss.setNombreReporte("carteraVencida");
                    ss.agregarParametro("PARROQUIA", (predioModel.getParroquia() == null ? null : predioModel.getParroquia().getCodNac()));
                    ss.agregarParametro("URBANIZACION", predioModel.getCiudadela() != null ? predioModel.getCiudadela().getId() : null);
                    ss.agregarParametro("MZ", predioModel.getMzUrb() != null ? "%" + predioModel.getMzUrb() + "%" : null);
                    ss.agregarParametro("SL", predioModel.getSlUrb() != null ? "%" + predioModel.getSlUrb() + "%" : null);
                    ss.agregarParametro("ANIO_DESDE", predioModel.getAnioDesde());
                    ss.agregarParametro("ANIO_HASTA", predioModel.getAnioHasta());
                    break;
                case 2:
                    ss.setNombreReporte("carteraVencidaRural");
                    ss.agregarParametro("PARROQUIA", predioModel.getParroquia() != null ? predioModel.getParroquia().getId() : null);
                    ss.agregarParametro("REGISTRO", predioModel.getRegCatastral() != null ? "%" + predioModel.getRegCatastral() + "%" : null);
                    ss.agregarParametro("ID_PREDIAL", predioModel.getIdPredial() != null ? predioModel.getIdPredial() : null);
                    break;
                default:
                    break;
            }
            Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");
        } catch (Exception e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarPredioRural(Boolean excel) {
        try {
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("tesoreria");
            ss.setFondoBlanco(Boolean.TRUE);

            ss.agregarParametro("PARROQUIA", (predioModel.getParroquia() == null ? null : predioModel.getParroquia().getCodNac()));
            if (tipoReporte != null) {
                switch (tipoReporte) {
                    case 1:
                        ss.setNombreReporte("carteraVencidaRuralBarra");
                        ss.agregarParametro("ANIO", Utils.getAnio(new Date()));
                        ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
                        break;
                    case 2:
                        ss.setNombreReporte("carteraVancidaPredioRurales");
                        ss.agregarParametro("ANIO", Utils.getAnio(new Date()));
                        ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
                        break;
                    case 3:
                        ss.setNombreReporte("carteraVencidaUrbanoBarra");
                        ss.agregarParametro("ANIO", Utils.getAnio(new Date()));
                        ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
                        break;
                    case 4:
                        ss.setNombreReporte("carteraVancidaPredioUrbano");
                        ss.agregarParametro("ANIO", Utils.getAnio(new Date()));
                        ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
                        break;
                    default:
                        break;
                }
            }

            if (excel) {
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");
            } else {
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            }
        } catch (Exception e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<CatParroquia> getParroquias() {
        Map<String, Object> paramt = new HashMap<>();
        paramt.put("idCanton", manager.find(Querys.getParroquiasByCanton, new String[]{"codigoNacional", "codNac"}, new Object[]{SisVars.CANTON, SisVars.PROVINCIA}));
        paramt.put("estado", true);
        return manager.findObjectByParameterOrderList(CatParroquia.class, paramt, new String[]{"idCanton"}, true);
    }

    public List<ModelCarteraVencidaParroquia> getCateraVencida() {
        try {
            modelCarteraParroquia = serv.getCarteraModelParroquias(getParroquias());
            if (Utils.isNotEmpty(modelCarteraParroquia)) {
                createPieModel("Cartera por Parroquias");
                createBarModel();
                return modelCarteraParroquia;
            }
        } catch (HibernateException e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return null;
    }

    private void createPieModel(String title) {
        pieModel = new PieChartModel();
        for (ModelCarteraVencidaParroquia cr : modelCarteraParroquia) {
            if (cr.getTotalEmitido() != null && cr.getTotalEmitido().compareTo(BigDecimal.ZERO) > 0) {
                pieModel.set(cr.getParroquia(), cr.getTotalCartera());
            }
        }
        pieModel.setTitle(title);
        pieModel.setLegendPosition("w");
    }

    private void createBarModel() {
        barModel = initBarModel();

        barModel.setTitle("Cartera Vencida");
        barModel.setLegendPosition("ne");

        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Parroquias");

        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Valor en dolares");
        yAxis.setMin(0);
        yAxis.setMax(axisY);
    }

    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();

        for (ModelCarteraVencidaParroquia cv : modelCarteraParroquia) {
            if (cv.getTotalEmitido() != null && cv.getTotalEmitido().compareTo(BigDecimal.ZERO) > 0) {
                if (axisY.compareTo(cv.getTotalEmitido()) < 0) {
                    axisY = cv.getTotalEmitido();
                }

                // Chart Series cartera vencida
                ChartSeries cart = new ChartSeries();
                cart.setLabel("Cartera de " + cv.getParroquia());
                cart.set(cv.getParroquia(), cv.getTotalCartera());
                // Chart Series Valores cobrados
                ChartSeries cobr = new ChartSeries();
                cobr.setLabel("Cobrado de " + cv.getParroquia());
                cobr.set(cv.getParroquia(), cv.getTotalCobrado());
                model.addSeries(cart);
                model.addSeries(cobr);
            }
        }
        return model;
    }

    public void limpiarTablas() {
        try {
            prediosUrbanos = new ArrayList<>();
            prediosRurales = new ArrayList<>();
            prediosRurales2017 = new ArrayList<>();
        } catch (Exception e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public CtlgCatalogo getCatalogoTramitesResolucion() {
        return catalogoTramitesResolucion;
    }

    public void setCatalogoTramitesResolucion(CtlgCatalogo catalogoTramitesResolucion) {
        this.catalogoTramitesResolucion = catalogoTramitesResolucion;
    }

    public CtlgItem getTramiteResolucion() {
        return tramiteResolucion;
    }

    public void setTramiteResolucion(CtlgItem tramiteResolucion) {
        this.tramiteResolucion = tramiteResolucion;
    }

    public Long getTipoConsultaUrbano() {
        return tipoConsultaUrbano;
    }

    public void setTipoConsultaUrbano(Long tipoConsultaUrbano) {
        this.tipoConsultaUrbano = tipoConsultaUrbano;
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public List<CatPredio> getPrediosUrbanos() {
        return prediosUrbanos;
    }

    public void setPrediosUrbanos(List<CatPredio> prediosUrbanos) {
        this.prediosUrbanos = prediosUrbanos;
    }

    public List<CatPredioRustico> getPrediosRurales() {
        return prediosRurales;
    }

    public void setPrediosRurales(List<CatPredioRustico> prediosRurales) {
        this.prediosRurales = prediosRurales;
    }

    public List<CatPredio> getPrediosUrbanosConsulta() {
        return prediosUrbanosConsulta;
    }

    public void setPrediosUrbanosConsulta(List<CatPredio> prediosUrbanosConsulta) {
        this.prediosUrbanosConsulta = prediosUrbanosConsulta;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getMemorandum() {
        return memorandum;
    }

    public void setMemorandum(String memorandum) {
        this.memorandum = memorandum;
    }

    public String getMemoSolicitante() {
        return memoSolicitante;
    }

    public void setMemoSolicitante(String memoSolicitante) {
        this.memoSolicitante = memoSolicitante;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<CatParroquia> getParroquiasRurales() {
        return parroquiasRurales;
    }

    public void setParroquiasRurales(List<CatParroquia> parroquiasRurales) {
        this.parroquiasRurales = parroquiasRurales;
    }

    public List<CatPredio> getPrediosUrbanosConsultaSeleccionados() {
        return prediosUrbanosConsultaSeleccionados;
    }

    public void setPrediosUrbanosConsultaSeleccionados(List<CatPredio> prediosUrbanosConsultaSeleccionados) {
        this.prediosUrbanosConsultaSeleccionados = prediosUrbanosConsultaSeleccionados;
    }

    public List<EmisionesRuralesExcel> getPrediosRurales2017() {
        return prediosRurales2017;
    }

    public void setPrediosRurales2017(List<EmisionesRuralesExcel> prediosRurales2017) {
        this.prediosRurales2017 = prediosRurales2017;
    }

    public EmisionesRuralesExcel getPredioRural2017() {
        return predioRural2017;
    }

    public void setPredioRural2017(EmisionesRuralesExcel predioRural2017) {
        this.predioRural2017 = predioRural2017;
    }

    public String getCodigoRural2017() {
        return codigoRural2017;
    }

    public void setCodigoRural2017(String codigoRural2017) {
        this.codigoRural2017 = codigoRural2017;
    }

    public Integer getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(Integer tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    public void setPieModel(PieChartModel pieModel) {
        this.pieModel = pieModel;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    public List<ModelCarteraVencida> getModelCartera() {
        return modelCartera;
    }

    public void setModelCartera(List<ModelCarteraVencida> modelCartera) {
        this.modelCartera = modelCartera;
    }

    public List<ModelCarteraVencidaParroquia> getModelCarteraParroquia() {
        return modelCarteraParroquia;
    }

    public void setModelCarteraParroquia(List<ModelCarteraVencidaParroquia> modelCarteraParroquia) {
        this.modelCarteraParroquia = modelCarteraParroquia;
    }

    public ModelCarteraVencidaParroquia getCarteraParroquia() {
        return carteraParroquia;
    }

    public void setCarteraParroquia(ModelCarteraVencidaParroquia carteraParroquia) {
        this.carteraParroquia = carteraParroquia;
    }

    public ModelCarteraVencida getCarteraVencida() {
        return carteraVencida;
    }

    public void setCarteraVencida(ModelCarteraVencida carteraVencida) {
        this.carteraVencida = carteraVencida;
    }

}
